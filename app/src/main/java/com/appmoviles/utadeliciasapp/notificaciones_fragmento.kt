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

class notificaciones_fragmento : Fragment() {

    companion object {
        const val MY_CHANNEL_ID = "myChannel"
        const val REQUEST_CODE_NOTIFICATIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificaciones_fragmento, container, false)

        val myNotificationButton = view.findViewById<Button>(R.id.btnNotification)
        requestNotificationPermission() // Solicitar permiso de notificación
        myNotificationButton.setOnClickListener {
            createSimpleNotification()
        }

        return view
    }

    private fun requestNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
            } else {
                // Permiso denegado, notificar al usuario si es necesario
            }
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            MY_CHANNEL_ID,
            "mySuperChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pruebita"
        }

        val notificationManager: NotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createSimpleNotification() {
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logoapp)

        val intent = Intent(requireContext(), AuthActivity::class.java).apply {
            putExtra("fragment_name", "notificaciones_fragmento")
        }

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(requireContext(), MY_CHANNEL_ID)
            .setSmallIcon(R.drawable.logoapp)
            .setLargeIcon(largeIcon)
            .setContentTitle("GOUF!!")
            .setContentText("Revisa tus notificaciones")
            .setStyle(NotificationCompat.BigTextStyle().bigText("REVISA TUS NOTIFICACIONES"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(1, builder.build())
            }
        }
    }
}
