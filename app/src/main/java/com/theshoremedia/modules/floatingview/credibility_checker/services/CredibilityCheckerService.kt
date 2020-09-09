package com.theshoremedia.modules.floatingview.credibility_checker.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.database.entity.FactCheckHistoryModel
import com.theshoremedia.database.helper.FactCheckHistoryDatabaseHelper
import com.theshoremedia.modules.floatingview.credibility_checker.model.ValidateNewsReqModel
import com.theshoremedia.modules.floatingview.credibility_checker.ui.RootView
import com.theshoremedia.retrofit.API
import com.theshoremedia.retrofit.model.GenericResponseModel
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.NotificationsUtils
import com.theshoremedia.utils.ObjectUtils
import com.theshoremedia.utils.extensions.makeVisible
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import com.theshoremedia.utils.whatsapp.WhatsAppUtils
import com.theshoremedia.views.BubbleCredibilityCheckerView


/**
 * @author- Nitin Khanna
 * @date - 22/08/2020
 */

class CredibilityCheckerService : Service() {
    private var isAPIRunning: Boolean = false

    companion object {
        private lateinit var instance: CredibilityCheckerService
        var isInitialized = false

        fun getInstance(): CredibilityCheckerService {
            return instance
        }
    }

    private lateinit var windowManager: WindowManager
    lateinit var displayMetrics: DisplayMetrics
    private lateinit var innerReceiver: CredibilityCheckerReceiver

    lateinit var rootView: RootView


    override fun onDestroy() {
        unregisterReceiver(innerReceiver)
        removeBubbleView()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        OnDrawPermissionsUtils.checkPermission(this) {
            //Initialize service instance
            instance = this
            isInitialized = true

            //Here, we're initializing RootView
            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            rootView = RootView(this)
            windowManager.addView(rootView, rootView.params)
            rootView.addShoreBubble()

            //We're using custom BroadcastReceiver to close overlay view for system generated dialog
            innerReceiver = CredibilityCheckerReceiver()
            val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            registerReceiver(innerReceiver, intentFilter)

            //Finally, starting foreground service
            startForeground(101, createNotification())

            newDataListener.invoke()
        }
        return START_NOT_STICKY
    }

    /**
     * @method is used for creating notification for foreground service.
     */
    private fun createNotification(): Notification {
        val channelId = NotificationsUtils.createNotificationChannel(this)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java), 0
        )
        val cancelIntent = Intent(
            "com.theshoremedia.cancel"
        )
        cancelIntent.putExtra(AppConstants.Key.REQUEST_CODE, 1001)
        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext, 1001, cancelIntent, 0
        )


        val cancelAction: NotificationCompat.Action = NotificationCompat.Action(
            R.drawable.ic_logo,
            getString(R.string.cancel),
            cancelPendingIntent
        )


        return NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setContentTitle(getString(R.string.the_shore_is_active))
            .setSmallIcon(R.drawable.ic_logo)
            .addAction(cancelAction)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent).build()
    }

    fun removeBubbleView() {
        isInitialized = false
        rootView.onClose()
        BubbleCredibilityCheckerView.getInstance()?.close()
        windowManager.removeViewImmediate(rootView)
        windowManager.removeViewImmediate(rootView.motionTracker)
        stopForeground(true)
    }

    fun addView(view: View, param: ViewGroup.LayoutParams) = windowManager.addView(view, param)
    fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
        try {
            windowManager.updateViewLayout(
                view,
                params
            )
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    var newDataListener: (() -> Unit) = {
        callValidateNews()
    }


    private fun callValidateNews() {
        Log.d("Nitin", "callValidateNews()")
        //Will return if forwardMessageList is empty, or If API is already running
        if (WhatsAppUtils.getInstance() == null || WhatsAppUtils.getInstance()?.forwardedMessagesList.isNullOrEmpty()) return
        if (isAPIRunning) return

        //Find the index of next message which has not been processed yet
        val indexOfFirst = WhatsAppUtils.getInstance()?.forwardedMessagesList!!.indexOfFirst {
            !it.isProcessed
        }
        //Will return if @var indexOfFirst is equals to -1, which state all messages are already validated
        if (indexOfFirst == -1) {
            rootView.bubbleView?.progressBar?.makeVisible(isVisible = false)
            return
        }

        //Mark @val isAPIRunning as true, for further processing updates
        isAPIRunning = true
        rootView.bubbleView?.progressBar?.makeVisible(isVisible = true)

        val reqModel: ValidateNewsReqModel =
            WhatsAppUtils.getInstance()?.forwardedMessagesList?.get(indexOfFirst)!!

        //First, We'll check if forwardedMessage stored in local database or not. If yes, then we'll return it from there. Else, will hit API
        FactCheckHistoryDatabaseHelper.instance?.getNews(reqModel.query) {
            //Here it == null states, that searched message is not stored in local database. Thus, we'll hit API for further verification.
            if (it == null) {
                Log.d(
                    "Nitin",
                    "Did not find the search query in local database, thus we're hitting API for further verification"
                )
                callAPIAsDataNotInLocal(reqModel)
                return@getNews
            }

            Log.d("Nitin", "Searched query has been found in local database.")
            reqModel.isProcessed = true
            resetAPIStatus()
        }

    }

    //@method is used for calling news validation API
    private fun callAPIAsDataNotInLocal(reqModel: ValidateNewsReqModel) {
        reqModel.isProcessing = true
        API.callValidateNews(mContext = this, requestBody = reqModel) {
            Log.d("Nitin", "validateNews API response ${ObjectUtils.toString(it)}")
            if (it !is GenericResponseModel<*>) {
                reqModel.isProcessed = true
                resetAPIStatus()
                return@callValidateNews
            }
            FactCheckHistoryDatabaseHelper.instance?.insertNews(it.data as FactCheckHistoryModel)
            reqModel.isProcessed = true
            resetAPIStatus()
        }
    }


    private fun resetAPIStatus() {
        isAPIRunning = false
        newDataListener.invoke()
        rootView.refreshData()
    }


}

