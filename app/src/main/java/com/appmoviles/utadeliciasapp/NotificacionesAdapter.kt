package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificacionesAdapter : RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder>() {
    private var notificaciones = mutableListOf<NotificacionPedido>()

    class NotificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notificacion = notificaciones[position]

        holder.tvCliente.text = "${notificacion.clienteNombre} ${notificacion.clienteApellido}"

        val productosText = notificacion.productos.joinToString("\n") { producto ->
            "${producto.nombre} x${producto.cantidad} - $${producto.precio}"
        }
        holder.tvProductos.text = productosText

        val fecha = Date(notificacion.fecha)
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvFecha.text = formatoFecha.format(fecha)
    }

    override fun getItemCount() = notificaciones.size

    fun actualizarNotificaciones(nuevasNotificaciones: List<NotificacionPedido>) {
        notificaciones.clear()
        notificaciones.addAll(nuevasNotificaciones)
        notifyDataSetChanged()
    }
}