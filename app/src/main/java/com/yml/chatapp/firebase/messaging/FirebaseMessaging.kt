package com.yml.chatapp.firebase.messaging

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yml.chatapp.R
import com.yml.chatapp.ui.authentication.MainActivity

class FirebaseMessaging: FirebaseMessagingService() {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    companion object {
        const val CHANNEL_ID = "notification channel"
        const val CHANNEL_NAME = "com.yml.chatapp"
    }

    fun getToken(callback: (String) -> Unit) {
        firebaseMessaging.token.addOnCompleteListener { task ->
            if(!task.isSuccessful) {
                callback("")
            }
            callback(task.result.toString())
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: ""
        val content = message.notification?.body ?: ""
        val image = message.notification?.imageUrl ?: ""

        val notification = if(content.isNotEmpty()) {
            generateMessageNotification(title,content)
        }else {
            generateImageNotification(title, image.toString())
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(notificationChannel)

        manager.notify(1001, notification)
    }

    private fun generateMessageNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun generateImageNotification(title: String, image: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val glideBitMap = Glide.with(this).asBitmap().load(image).submit()
        val bitMapImage = glideBitMap.get()
        val notificationBigPicture = NotificationCompat.BigPictureStyle().bigPicture(bitMapImage)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setStyle(notificationBigPicture)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }
}