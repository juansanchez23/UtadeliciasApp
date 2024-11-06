import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.appmoviles.utadeliciasapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Método que se llama cuando se recibe un mensaje FCM
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Comprueba si el mensaje tiene datos (en este caso, texto de la notificación)
        remoteMessage.data.isNotEmpty().let {
            val message = remoteMessage.data["message"]
            if (!message.isNullOrEmpty()) {
                // Muestra la notificación con el mensaje
                showNotification(message)
            }
        }

        // También puedes manejar las notificaciones en el campo de "notification"
        remoteMessage.notification?.let {
            showNotification(it.body)
        }
    }

    private fun showNotification(message: String?) {
        // Crear el canal de notificación (solo en Android 8.0 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Crea y muestra la notificación
        val notification = Notification.Builder(this, "default_channel")
            .setContentTitle("Nueva Notificación")
            .setContentText(message)
            .setSmallIcon(R.drawable.logoapp) // Asegúrate de tener un ícono
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
