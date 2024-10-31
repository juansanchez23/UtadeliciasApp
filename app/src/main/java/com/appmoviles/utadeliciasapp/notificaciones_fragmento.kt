package com.appmoviles.utadeliciasapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class notificaciones_fragmento : Fragment() { // Usar PascalCase para nombres de clases

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notificaciones_fragmento, container, false)

        val myNotificationButton = view.findViewById<Button>(R.id.btnNotification)
        createChannel()
        myNotificationButton.setOnClickListener {
            createSimpleNotification()
        }

        return view
    }

    fun createChannel() {
        val channel = NotificationChannel(
            MY_CHANNEL_ID,
            "mySuperChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pruebita"
        }

        // Obtener el servicio de notificaciones
        val notificationManager: NotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createSimpleNotification() {
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logoapp) // Imagen grande

        // Crea el Intent para abrir el fragmento
        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra("fragment_name", "notificaciones_fragmento") // Indica qué fragmento cargar
        }

        // Crea el PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Asegúrate de usar el flag adecuado
        )

        val builder = NotificationCompat.Builder(requireContext(), MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.logoapp) // Icono pequeño
            .setLargeIcon(largeIcon) // Icono grande
            .setContentTitle("GOUF!!")
            .setContentText("Tienes un pedido nuevo")
            .setStyle(NotificationCompat.BigTextStyle().bigText("TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO TEXTO"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Asocia el PendingIntent a la notificación
            .setAutoCancel(true) // Cierra la notificación al tocarla

        with(NotificationManagerCompat.from(requireContext())) {
            // Verifica si se tiene el permiso de enviar notificaciones
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }

    override fun onResume() {
        super.onResume()
        // Verifica si el fragmento fue llamado desde la notificación
        val fragmentName = arguments?.getString("fragment_name")
        if (fragmentName == "notificaciones_fragmento") {
            // Carga tu fragmento aquí, por ejemplo:
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.notification, notificaciones_fragmento())
            transaction.addToBackStack(null) // Permite volver al fragmento anterior
            transaction.commit()
        }
    }
}