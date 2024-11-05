package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CarritoAdapter(
    private val items: MutableList<CarritoItem>,
    private val onEliminar: (String, Int) -> Unit // Lambda para eliminar
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcionProducto)
        val cantidad: TextView = view.findViewById(R.id.tvCantidadProducto)
        val imagen: ImageView = view.findViewById(R.id.ivProductoImagen)
        val ivEliminar: ImageView = itemView.findViewById(R.id.ivEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito_producto, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val carritoItem = items[position]
        holder.nombre.text = carritoItem.producto.nombre
        holder.descripcion.text = carritoItem.producto.descripcion
        holder.cantidad.text = "Cantidad: ${carritoItem.selectedQuantity}"

        // Cargar imagen del producto con Glide
        Glide.with(holder.itemView.context)
            .load(carritoItem.producto.imagen)
            .into(holder.imagen)

        // Manejar clic en el icono de eliminar
        holder.ivEliminar.setOnClickListener {
            onEliminar(carritoItem.producto.id, position) // Llama a la función de eliminar
        }
    }

    override fun getItemCount() = items.size

    // Método para eliminar un producto por posición
    fun eliminarProducto(position: Int) {
        items.removeAt(position) // Elimina el producto de la lista
        notifyItemRemoved(position) // Notifica que un ítem ha sido removido
        notifyItemRangeChanged(position, items.size) // Actualiza el rango de elementos
    }
}
