package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificacionesComercioFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificacionesAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notificaciones_comercio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvNotificaciones)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = NotificacionesAdapter()
        recyclerView.adapter = adapter

        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        val comercioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("pedidos")
            .whereEqualTo("userId", comercioId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("NotificacionesComercio", "Error al escuchar cambios", e)
                    return@addSnapshotListener
                }

                val notificaciones = mutableListOf<NotificacionPedido>()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(NotificacionPedido::class.java)?.let {
                        notificaciones.add(it)
                    }
                }
                adapter.actualizarNotificaciones(notificaciones)
            }
    }
}
