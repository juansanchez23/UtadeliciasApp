package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CarritoAdapter(
    private val items: List<CarritoItem>
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    class CarritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcionProducto)
        val cantidad: TextView = view.findViewById(R.id.tvCantidadProducto)
        val imagen: ImageView = view.findViewById(R.id.ivProductoImagen)
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

        // Cargar imagen del producto con Glide (asegúrate de tener Glide en tu proyecto)
        Glide.with(holder.itemView.context)
            .load(carritoItem.producto.imagen)
            .into(holder.imagen)
    }

    override fun getItemCount() = items.size
}
