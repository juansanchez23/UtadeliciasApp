package com.appmoviles.utadeliciasapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class NotificacionesAdapter : RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder>() {
    private var notificaciones = mutableListOf<NotificacionPedido>()

    class NotificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvTotal: TextView = view.findViewById(R.id.tvTotal)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notificacion = notificaciones[position]
        val context = holder.itemView.context

        // Mostrar información del cliente
        holder.tvCliente.text = buildString {
            append("Cliente: ")
            append(notificacion.clienteNombre)
            append(" ")
            append(notificacion.clienteApellido)
        }

        // Mostrar lista de productos con formato
        val productosText = buildString {
            append("Productos:\n")
            notificacion.productos.forEach { producto ->
                append("• ${producto.nombre}\n")
                append("  Cantidad: ${producto.cantidad}\n")
                append("  Precio unitario: $${producto.precio}\n")
                append("  Subtotal: $${producto.cantidad * producto.precio}\n")
            }
        }
        holder.tvProductos.text = productosText

        // Calcular y mostrar el total
        val total = notificacion.productos.sumOf { it.cantidad * it.precio }
        holder.tvTotal.text = "Total: $${total}"

        // Formatear y mostrar la fecha
        val fecha = Date(notificacion.fecha)
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvFecha.text = "Fecha: ${formatoFecha.format(fecha)}"

        // Mostrar estado del pedido
        holder.tvEstado.text = "Estado: ${notificacion.estado ?: "Pendiente"}"
        holder.tvEstado.setTextColor(
            when(notificacion.estado) {
                "Aceptado" -> Color.GREEN
                "Rechazado" -> Color.RED
                else -> Color.GRAY
            }
        )

        // Configurar botones de aceptar/rechazar
        if (notificacion.estado == null || notificacion.estado == "Pendiente") {
            holder.btnAceptar.visibility = View.VISIBLE
            holder.btnRechazar.visibility = View.VISIBLE

            holder.btnAceptar.setOnClickListener {
                actualizarEstadoPedido(notificacion.pedidoId, "Aceptado", position)
            }

            holder.btnRechazar.setOnClickListener {
                actualizarEstadoPedido(notificacion.pedidoId, "Rechazado", position)
            }
        } else {
            holder.btnAceptar.visibility = View.GONE
            holder.btnRechazar.visibility = View.GONE
        }
    }

    private fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: String, position: Int) {
        FirebaseFirestore.getInstance()
            .collection("pedidos")
            .document(pedidoId)
            .update("estado", nuevoEstado)
            .addOnSuccessListener {
                // Actualizar el estado en la lista local
                notificaciones[position] = notificaciones[position].copy(estado = nuevoEstado)
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Log.e("NotificacionesAdapter", "Error al actualizar estado", e)
            }
    }

    override fun getItemCount() = notificaciones.size

    fun actualizarNotificaciones(nuevasNotificaciones: List<NotificacionPedido>) {
        notificaciones.clear()
        notificaciones.addAll(nuevasNotificaciones)
        notifyDataSetChanged()
    }
}