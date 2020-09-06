package com.atozkids.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.atozkids.R
import com.atozkids.ui.activities.SplashActivity
import com.google.firebase.messaging.RemoteMessage
import java.net.URL


object NotificationUtils {

    private const val CHANNEL_ID = "myChannel"
    private const val CHANNEL_NAME = "myChannelName"


    fun displayNotification(
        mContext: Context,
        remoteMessage: RemoteMessage
    ) {
        run {
            var title = ""
            var imageUrl = ""
            var message = ""
            if (remoteMessage.data.containsKey("title")) {
                title = remoteMessage.data.getValue("title")
            }
            if (remoteMessage.data.containsKey("message")) {
                message = remoteMessage.data.getValue("message")
            }
            if (remoteMessage.data.containsKey("imageurl")) {
                imageUrl = remoteMessage.data.getValue("imageurl")
            }

            val icon: Int = R.drawable.ic_notification
            val notification: Notification
            try {

                val bitmap: Bitmap
                bitmap = if (imageUrl.isEmpty()) {
                    BitmapFactory.decodeResource(mContext.resources, R.drawable.logo_atoz)
                } else {
                    val appImgUrlLink = URL(imageUrl)
                    BitmapFactory.decodeStream(appImgUrlLink.openConnection().getInputStream())
                }

                val notificationIntent = Intent(mContext, SplashActivity::class.java)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                val contentIntent =
                    PendingIntent.getActivity(mContext, 0, notificationIntent, 0)
                val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
                    mContext, CHANNEL_ID
                )

                val bigTextStyle =
                    NotificationCompat.BigTextStyle()
                bigTextStyle.setBigContentTitle("" + title)
                bigTextStyle.bigText("" + message)

                notification = mBuilder
                    .setSmallIcon(icon)
                    .setLargeIcon(bitmap)
                    .setStyle(bigTextStyle)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true).build()
                val notificationManager =
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                //All notifications should go through NotificationChannel on Android 26 & above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(
                    System.currentTimeMillis().toInt(),
                    notification
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}