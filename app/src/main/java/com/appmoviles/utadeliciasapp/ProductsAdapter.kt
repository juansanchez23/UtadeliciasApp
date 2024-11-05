package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductsAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    interface OnItemClickListener {
        fun onDeleteItemClick(productId: String)
        fun onItemClick(product: Products) // Método para manejar clic en el producto
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productosList[position]
        holder.bind(producto) // Llamamos al método bind aquí
    }

    override fun getItemCount(): Int = productosList.size

    fun setDatos(listaProductos: List<Products>) {
        productosList.clear()
        productosList.addAll(listaProductos)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtNombre)
        private val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        private val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productosList[position]
                    listener.onItemClick(product) // Llama al método en el listener
                }
            }
        }

        // Método bind para establecer los datos del producto
        fun bind(producto: Products) {
            nombreTextView.text = producto.nombre

            // Cargar la imagen con Glide
            Glide.with(itemView.context)
                .load(producto.imagen)
                .into(ivProducto)

            // Configura el clic en el botón de eliminar
            btnDelete.setOnClickListener {
                listener.onDeleteItemClick(producto.id)
            }
        }
    }
}