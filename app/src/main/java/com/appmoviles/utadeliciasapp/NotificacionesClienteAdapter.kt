package com.appmoviles.utadeliciasapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificacionesClienteAdapter(
    private val onDelete: (String) -> Unit // Nuevo parámetro

) : RecyclerView.Adapter<NotificacionesClienteAdapter.NotificacionClienteViewHolder>() {
    private val notificaciones = mutableListOf<NotificacionPedido>()
    private val hiddenNotifications = mutableSetOf<String>()

    fun setData(newNotificaciones: List<NotificacionPedido>) {
        notificaciones.clear()
        notificaciones.addAll(newNotificaciones.filter { !hiddenNotifications.contains(it.pedidoId) })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionClienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion_cliente, parent, false)
        return NotificacionClienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionClienteViewHolder, position: Int) {
        holder.bind(notificaciones[position])
    }

    override fun getItemCount(): Int = notificaciones.size

    inner class NotificacionClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaCliente)
        private val tvProductos: TextView = itemView.findViewById(R.id.tvProductosCliente)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotalCliente)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoCliente)

        fun bind(notificacion: NotificacionPedido) {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            tvFecha.text = "Fecha: ${formatoFecha.format(Date(notificacion.fecha))}"

            val productosText = notificacion.productos.joinToString("\n") {
                "${it.nombre} x${it.cantidad} = $${it.precio * it.cantidad}"
            }
            tvProductos.text = "Productos:\n$productosText"

            val total = notificacion.productos.sumOf { it.precio * it.cantidad }
            tvTotal.text = "Total: $${String.format("%.2f", total)}"

            // Mostrar el estado del pedido con un color distintivo
            tvEstado.text = when (notificacion.estado) {
                "enviado" -> "¡Tu pedido está en camino!"
                "pendiente" -> "Pedido en proceso"
                else -> "Estado: ${notificacion.estado}"
            }

            // Cambiar el color del texto según el estado
            tvEstado.setTextColor(when (notificacion.estado) {
                "enviado" -> Color.parseColor("#4CAF50")  // Verde
                "pendiente" -> Color.parseColor("#FFA000")  // Naranja
                else -> Color.BLACK
            })
        }


    }
    fun hideNotification(position: Int) {
        if (position >= 0 && position < notificaciones.size) {
            val notification = notificaciones[position]
            hiddenNotifications.add(notification.pedidoId)
            notificaciones.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}