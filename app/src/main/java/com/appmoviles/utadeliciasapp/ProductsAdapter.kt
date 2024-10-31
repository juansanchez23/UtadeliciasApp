package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProductsAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    interface OnItemClickListener {
        fun onDeleteItemClick(productId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productosList[position]
        holder.bind(producto)


        // Configura el clic en el botón de eliminar
        holder.btnDelete.setOnClickListener {
            listener.onDeleteItemClick(producto.id)
        }
    }

    override fun getItemCount(): Int = productosList.size

    fun setDatos(listaProductos: List<Products>) {
        productosList.clear()
        productosList.addAll(listaProductos)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtNombre)
        private val descripcionTextView: TextView = itemView.findViewById(R.id.txtDescripcion)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(product: Products) {
            nombreTextView.text = product.nombre
            descripcionTextView.text = product.descripcion
        }
    }
}