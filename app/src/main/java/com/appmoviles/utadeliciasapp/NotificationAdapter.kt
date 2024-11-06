package com.appmoviles.utadeliciasapp

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView

//class NotificationAdapter(private val notifications: List<Notification>) :
    //RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    //class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val title: TextView = itemView.findViewById(R.id.notificationTitle)
        //val message: TextView = itemView.findViewById(R.id.notificationMessage)
        //val button: Button = itemView.findViewById(R.id.notificationButton)
   // }

    //override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        //val view = LayoutInflater.from(parent.context)
         //   .inflate(R.layout.notification_item, parent, false)
        //return NotificationViewHolder(view)
    //}

   // override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
    ////   holder.title.text = notification.title
       // holder.message.text = notification.message
//
        //if (notification.isForClient) {
          //  holder.button.text = "Confirmar Pedido"
       // } else {
      //      holder.button.text = "Orden Generada"
     //   }

     //   holder.button.setOnClickListener {
            // Lógica para enviar la notificación a los clientes o a los dueños del comercio
      //      if (notification.isForClient) {
       //         sendClientNotification(notification.id)
        //    } else {
        //        sendMerchantNotification(notification.id)
       //     }
      //  }
  //  }

 //   override fun getItemCount() = notifications.size

   // private fun sendClientNotification(orderId: String) {
   //     val notificationMessage = "Tu pedido con ID $orderId ha sido aceptado."
      //  sendNotification("Pedido Aceptado", notificationMessage)
    //}

  //  private fun sendMerchantNotification(orderId: String) {
  //      val notificationMessage = "La orden con ID $orderId ha sido generada."
   //     sendNotification("Orden Generada", notificationMessage)
    //}

   // private fun sendNotification(title: String, message: String) {
        //val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logoapp)

      //  val intent = Intent(requireContext(), AuthActivity::class.java).apply {
      //      putExtra("fragment_name", "notificaciones_fragmento")
    //    }

     //   val pendingIntent = PendingIntent.getActivity(
        //    requireContext(),
          //  0,
          //  intent,
          //  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       // )

        //val builder = NotificationCompat.Builder(requireContext(), MY_CHANNEL_ID)
           // .setSmallIcon(R.drawable.logoapp)
            //.setLargeIcon(largeIcon)
           // .setContentTitle(title)
            //.setContentText(message)
            //.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent(pendingIntent)
            //.setAutoCancel(true)

        ///with(NotificationManagerCompat.from(requireContext())) {
          //  if (ActivityCompat.checkSelfPermission(
            //        requireContext(),
              //      Manifest.permission.POST_NOTIFICATIONS
               // ) == PackageManager.PERMISSION_GRANTED
            //) {
              //  notify(1, builder.build())
           // /}
        //}
    //}
//}