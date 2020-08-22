package com.theshoremedia.floatingview.credibility_checker.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.theshoremedia.R
import com.theshoremedia.activity.MainActivity
import com.theshoremedia.floatingview.credibility_checker.ui.RootView

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

    override fun onCreate() {
        super.onCreate()

        instance = this
        initialized = true

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        rootView = RootView(
            this
        )

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

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setOngoing(true)
            .setContentTitle(getString(R.string.the_shore_is_active))
            .setSmallIcon(R.drawable.ic_logo)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent).build()

        startForeground(101, notification)
    }

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
        initialized = false
        unregisterReceiver(innerReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    fun removeBubbleView() {
        rootView.collapse()
        rootView.removeAllViews()
    }

    fun updateViewLayout(view: View, params: ViewGroup.LayoutParams) {
        windowManager.updateViewLayout(
            view,
            params
        )
    }
}

