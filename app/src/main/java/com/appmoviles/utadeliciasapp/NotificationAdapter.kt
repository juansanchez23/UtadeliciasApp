package com.appmoviles.utadeliciasapp

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.appmoviles.utadeliciasapp.AuthActivity.Companion.CHANNEL_ID

class NotificationAdapter(private val context: Context, private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notificationTitle)
        val message: TextView = itemView.findViewById(R.id.notificationMessage)
        val button: Button = itemView.findViewById(R.id.notificationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        holder.message.text = notification.message

        // Cambiar texto del botón según si es para el cliente o no
        if (notification.isForClient) {
            holder.button.text = "Confirmar Pedido"
        } else {
            holder.button.text = "Orden Generada"
        }

        // Lógica para el botón al hacer click
        holder.button.setOnClickListener {
            if (notification.isForClient) {
                sendClientNotification(notification.id)
            } else {
                sendMerchantNotification(notification.id)
            }
        }
    }

    override fun getItemCount() = notifications.size

    private fun sendClientNotification(orderId: String) {
        val notificationMessage = "Tu pedido con ID $orderId ha sido aceptado."
        sendNotification("Pedido Aceptado", notificationMessage)
    }

    private fun sendMerchantNotification(orderId: String) {
        val notificationMessage = "La orden con ID $orderId ha sido generada."
        sendNotification("Orden Generada", notificationMessage)
    }

    private fun sendNotification(title: String, message: String) {
        // Usar `context` para obtener los recursos
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.logoapp)

        // Crear el Intent que se ejecutará al hacer click en la notificación
        val intent = Intent(context, AuthActivity::class.java).apply {
            putExtra("fragment_name", "notificaciones_fragmento")
        }

        // Crear el PendingIntent para la notificación
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Crear el NotificationCompat.Builder

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logoapp)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            }
        }

    }
}
