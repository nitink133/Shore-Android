package com.theshoremedia.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.theshoremedia.utils.Log
import com.theshoremedia.utils.NotificationsUtils

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(tag = MyFirebaseMessagingService::class.java.simpleName, message = "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            if (remoteMessage.notification != null) {
                NotificationsUtils.showNotification(
                    this,
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            } else {
                NotificationsUtils.showNotification(
                    this, remoteMessage.data["title"], remoteMessage.data["message"]
                )
            }

        } catch (e: Exception) {
            Log.e(
                tag = MyFirebaseMessagingService::class.java.simpleName,
                message = "Notification Exception: $e.localizedMessage"
            )
        }
    }


}