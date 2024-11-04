package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorClienteProducto : RecyclerView.Adapter<AdaptadorClienteProducto.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_producto2, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = productosList.size

    fun setDatos(listaProductos: List<Products>) {
        productosList.clear()
        productosList.addAll(listaProductos)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imagen: ImageView = itemView.findViewById(R.id.ivProducto)

        fun bind(product: Products) {


            // Usar Glide para cargar la imagen desde la URL
            Glide.with(itemView.context)
                .load(product.imagen)  // Aquí la URL de la imagen
                .into(imagen)  // Cargar en el ImageView
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productosList[position]
        holder.bind(product)
    }
}
