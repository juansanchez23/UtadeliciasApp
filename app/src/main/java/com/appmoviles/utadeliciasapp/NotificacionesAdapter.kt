package com.appmoviles.utadeliciasapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class NotificacionesAdapter(
    private val onAccept: (NotificacionPedido) -> Unit,
    private val onReject: (NotificacionPedido) -> Unit
) : RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder>() {

    private val notificaciones = mutableListOf<NotificacionPedido>()
    private var comercioId: String = "" // Añadimos variable para almacenar el ID del comercio

    fun setData(newNotificaciones: List<NotificacionPedido>, comercioId: String) {
        this.comercioId = comercioId
        notificaciones.clear()
        notificaciones.addAll(newNotificaciones)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)
        return NotificacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        holder.bind(notificaciones[position])
    }

    override fun getItemCount(): Int = notificaciones.size

    inner class NotificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCliente: TextView = itemView.findViewById(R.id.tvCliente)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        private val tvProductos: TextView = itemView.findViewById(R.id.tvProductos)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val btnAceptar: Button = itemView.findViewById(R.id.btnAceptar)

        fun bind(notificacion: NotificacionPedido) {
            tvCliente.text = "Cliente: ${notificacion.clienteNombre} ${notificacion.clienteApellido}"

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            tvFecha.text = "Fecha: ${formatoFecha.format(Date(notificacion.fecha))}"
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            // Filtramos los productos que pertenecen a este comercio
            val productosFiltrados = notificacion.productos.filter { it.comercio_id == userId }

            val productosText = productosFiltrados.joinToString("\n") {
                " ${it.nombre} x${it.cantidad} = $${it.precio * it.cantidad}"
            }
            tvProductos.text = "Productos:\n$productosText"

            // Calculamos el total solo de los productos filtrados
            val total = productosFiltrados.sumOf { it.precio * it.cantidad }
            tvTotal.text = "Total: $${String.format("%.2f", total)}"

            tvEstado.text = "Estado: ${notificacion.estado}"

            if (notificacion.estado == "pendiente") {
                btnAceptar.visibility = View.VISIBLE
            } else {
                btnAceptar.visibility = View.GONE
            }

            btnAceptar.setOnClickListener {
                onAccept(notificacion)
            }

            }

    }
}