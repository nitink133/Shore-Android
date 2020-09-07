package com.theshoremedia.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.theshoremedia.R
import java.io.IOException

object NotificationsUtils {
    private var SENDER_ID = "1027736000656"

    fun createNotificationChannel(mContext: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                mContext.getString(R.string.app_name),
                mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            mContext.getString(R.string.app_name)
        }
        return mContext.getString(R.string.app_name)
    }

    fun showNotification(
        mContext: Context,
        title: String?,
        body: String?
    ) {
        if (title.isNullOrEmpty() || body.isNullOrEmpty()) return
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            mContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )


        val channelId = mContext.getString(R.string.app_name)
        val channelName = mContext.getString(R.string.app_name)
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(chan)
            mContext.getString(R.string.app_name)
        }
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(mContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        notificationManager.notify(100, notificationBuilder.build())
    }

    fun getFirebaseToken(callBack: ((token: String?) -> Unit)) {
        Thread(Runnable {
            try {
                callBack.invoke(
                    FirebaseInstanceId.getInstance()
                        .getToken(SENDER_ID, "FCM")
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }
}