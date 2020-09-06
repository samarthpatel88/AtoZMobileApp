package com.atozkids.firebase

import com.atozkids.utils.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        NotificationUtils.displayNotification(applicationContext, p0)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}
