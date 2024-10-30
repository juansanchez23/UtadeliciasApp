package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorClienteProducto: RecyclerView.Adapter<AdaptadorClienteProducto.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_producto2, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = productosList.size

    fun setDatos(listaProductos: List<Products>) {
        productosList.clear() // Limpia la lista anterior
        productosList.addAll(listaProductos) // Agrega los nuevos productos
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.txtNombre)


        fun bind(product: Products) {
            nombreTextView.text = product.nombre

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productosList[position]
        holder.bind(product) // Bind data to the view
    }
}
