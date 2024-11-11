package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        cargarNotificacionesCliente()
    }

    private fun setupAdapter() {
        notificacionesClienteAdapter = NotificacionesClienteAdapter()
        rvNotificaciones.adapter = notificacionesClienteAdapter
    }

    private fun cargarNotificacionesCliente() {
        val usuarioActual = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("pedidos")
            .whereEqualTo("clienteId", usuarioActual)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(context, "Error al cargar notificaciones", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notificaciones = snapshot.documents.mapNotNull {
                        val pedido = it.toObject(NotificacionPedido::class.java)
                        // Filtrar solo los pedidos con estado "enviado"
                        if (pedido?.estado == "enviado") pedido else null
                    }
                    notificacionesClienteAdapter.setData(notificaciones)
                } else {
                    Toast.makeText(context, "No tienes pedidos", Toast.LENGTH_SHORT).show()
                }
            }
    }



}