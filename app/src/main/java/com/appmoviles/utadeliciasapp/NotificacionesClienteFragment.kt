package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.util.Log
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

class NotificacionesClienteFragment : Fragment() {
    private lateinit var rvNotificaciones: RecyclerView
    private lateinit var notificacionesClienteAdapter: NotificacionesClienteAdapter
    private val firestore = FirebaseFirestore.getInstance()

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
            notificacionesClienteAdapter.hideNotification(position)
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

        Log.d("NotificacionesCliente", "Usuario actual: $usuarioActual") // Verificar el usuario actual

        firestore.collection("pedidos")
            .whereEqualTo("userId", usuarioActual)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("FirestoreError", "Error al cargar notificaciones", exception)
                    Toast.makeText(context, "Error al cargar notificaciones", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d("FirestoreSnapshot", "Documentos obtenidos: ${snapshot.size()}") // Verificar cantidad de documentos obtenidos
                    val notificaciones = snapshot.documents.mapNotNull { document ->
                        val pedido = document.toObject(NotificacionPedido::class.java)
                        Log.d("FirestoreSnapshot", "Pedido obtenido: $pedido") // Log de cada pedido
                        // Filtrar solo los pedidos con estado "enviado"
                        if (pedido?.estado == "enviado" || pedido?.estado == "pendiente") pedido else null


                    }
                    notificacionesClienteAdapter.setData(notificaciones)
                } else {
                    Log.d("FirestoreSnapshot", "No se encontraron pedidos para el usuario actual")
                    Toast.makeText(context, "No tienes pedidos", Toast.LENGTH_SHORT).show()
                }
            }
    }

}




