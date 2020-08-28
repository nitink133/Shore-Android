package com.theshoremedia.modules.floatingview.credibility_checker.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.modules.floatingview.credibility_checker.ui.RootView
import com.theshoremedia.utils.AppConstants
import com.theshoremedia.utils.permissions.OnDrawPermissionsUtils
import com.theshoremedia.views.BubbleCredibilityCheckerView


/**
 * @author- Nitin Khanna
 * @date -
 */

class CredibilityCheckerService : Service() {
    companion object {
        private lateinit var instance: CredibilityCheckerService
        private var initialized = false

        fun getInstance(): CredibilityCheckerService {
            return instance
        }

        fun isInitialized(): Boolean =
            initialized
    }

    lateinit var windowManager: WindowManager
    lateinit var rootView: RootView


    private lateinit var innerReceiver: CredibilityCheckerReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

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
            instance = this
            initialized = true

            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            rootView = RootView(
                this
            )
            Log.d("Nitin","Root view added")
            windowManager.addView(rootView, rootView.params)
            rootView.addShoreBubble()

            innerReceiver =
                CredibilityCheckerReceiver()
            val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            registerReceiver(innerReceiver, intentFilter)

            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("overlay_service", getString(R.string.app_name))
                } else {
                    ""
                }

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


            val notification = NotificationCompat.Builder(this, channelId)
                .setOngoing(true)
                .setContentTitle(getString(R.string.the_shore_is_active))
                .setSmallIcon(R.drawable.ic_logo)
                .addAction(cancelAction)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntent).build()


            startForeground(101, notification)
        }
        return START_STICKY
    }


    fun removeBubbleView() {
        initialized = false
        rootView.onClose()
        BubbleCredibilityCheckerView.getInstance(this).close()
        windowManager.removeViewImmediate(rootView)
        stopForeground(true)
    }

    fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
        windowManager.updateViewLayout(
            view,
            params
        )
    }

}

