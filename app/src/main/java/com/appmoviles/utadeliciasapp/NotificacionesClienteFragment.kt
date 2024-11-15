package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class NotificacionesClienteFragment : Fragment() {
    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var notificacionesClienteAdapter: NotificacionesClienteAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private var notificacionesListener: ListenerRegistration? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notificacion_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNotificaciones = view.findViewById(R.id.rvNotificacionesCliente)
        rvNotificaciones.layoutManager = LinearLayoutManager(requireContext())
        setupAdapter()
        setupSwipeToDelete()

        cargarNotificacionesCliente()
    }

    private fun setupAdapter() {
        notificacionesClienteAdapter = NotificacionesClienteAdapter(
            onDelete = { pedidoId ->
                eliminarNotificacion(pedidoId)
            }
        )
        rvNotificaciones.adapter = notificacionesClienteAdapter
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = SwipeToDeleteCallback(notificacionesClienteAdapter) { position ->
            notificacionesClienteAdapter.removeNotification(position)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(rvNotificaciones)
    }

    private fun eliminarNotificacion(pedidoId: String) {
        firestore.collection("pedidos").document(pedidoId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Notificación eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al eliminar la notificación", Toast.LENGTH_SHORT).show()
            }
    }
    private fun cargarNotificacionesCliente() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: return

        notificacionesListener = firestore.collection("pedidos")
            .whereEqualTo("userId", usuarioActual)
            .addSnapshotListener { snapshot, exception ->
                if (!isAdded) return@addSnapshotListener

                if (exception != null) {
                    mostrarMensaje("Error al cargar notificaciones")
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificaciones = snapshot.documents.mapNotNull { document ->
                        val pedido = document.toObject(NotificacionPedido::class.java)
                        if (pedido?.estado == "enviado" || pedido?.estado == "pendiente") {
                            // Llamar a la notificación cuando hay un nuevo pedido o actualización
                            mostrarNotificacionPedidoCliente(requireContext())
                            pedido
                        } else {
                            null
                        }
                    }
                    notificacionesClienteAdapter.setData(notificaciones)
                } else {
                    mostrarMensaje("No tienes pedidos")
                }
            }
    }


    private fun mostrarMensaje(mensaje: String) {
        context?.let { ctx ->
            if (isAdded) {
                Toast.makeText(ctx, mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        notificacionesListener?.remove()
        super.onDestroyView()
    }

    private fun mostrarNotificacionPedidoCliente(context: Context) {
        // ID único para el canal de notificación
        val channelId = "pedido_cliente_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (necesario en Android Oreo y versiones posteriores)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pedidos Nuevos para Cliente",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.cabitonormal)  // Usa un icono de tu elección
            .setContentTitle("Pedido Realizado")
            .setContentText("El comercio recibió tu pedido")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación
        notificationManager.notify(1, notification)
    }
}