package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class notificaciones_fragmento : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificaciones_fragmento, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Lista de notificaciones de ejemplo
        val notifications = listOf(
            Notification("1", "Pedido Aceptado", "Tu pedido ha sido aceptado.", true),
            Notification("2", "Orden Generada", "La orden ha sido generada.", false)
        )

        // Configura el adaptador con las notificaciones
        adapter = NotificationAdapter(requireContext(), notifications)
        recyclerView.adapter = adapter

        return view
    }
}
