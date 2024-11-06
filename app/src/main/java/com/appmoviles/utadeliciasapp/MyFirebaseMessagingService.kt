package com.appmoviles.utadeliciasapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.appmoviles.utadeliciasapp.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Si el mensaje contiene datos
        remoteMessage.data.isNotEmpty().let {
            val message = remoteMessage.data["message"]
            if (!message.isNullOrEmpty()) {
                showNotification(message)
            }
        }

        // Si el mensaje tiene una notificación
        remoteMessage.notification?.let {
            showNotification(it.body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        TokenManager.sendRegistrationToServer(token)
    }

    private fun showNotification(message: String?) {
        // Crear canal de notificación solo en Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val notification = Notification.Builder(this, "default_channel")
            .setContentTitle("Nueva Notificación")
            .setContentText(message)
            .setSmallIcon(R.drawable.logoapp)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
