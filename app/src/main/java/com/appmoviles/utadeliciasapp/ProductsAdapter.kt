package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductsAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    interface OnItemClickListener {
        fun onDeleteItemClick(productId: String)
        fun onItemClick(product: Products)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productosList[position]
        holder.bind(producto)
        holder.nombreTextView.text = producto.nombre


        // Cargar la imagen con Glide
        Glide.with(holder.itemView.context)
            .load(producto.imagen)
            .into(holder.ivProducto)


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
        val nombreTextView: TextView = itemView.findViewById(R.id.txtNombre)
        val ivProducto: ImageView = itemView.findViewById(R.id.ivProducto)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productosList[position]
                    listener.onItemClick(product)
                }
            }
        }

        fun bind(product: Products) {
            nombreTextView.text = product.nombre
            Glide.with(itemView.context)
                .load(product.imagen)
                .into(ivProducto)



            // Al hacer clic en un elemento, abrir ProductDetailFragment con los datos del producto
            itemView.setOnClickListener {
                val fragment = ProductDetailFragment(
                    product.nombre,
                    product.descripcion,
                    product.imagen,
                    product.precio,
                    product.cantidad
                )
                val bundle = Bundle()
                bundle.putString("productName", product.nombre)
                bundle.putString("productDescription", product.descripcion)
                bundle.putString("productImageUrl", product.imagen)
                bundle.putInt("productQuantity", product.cantidad)
                bundle.putInt("productPrice", product.precio)
                fragment.arguments = bundle

                // Reemplaza el fragmento actual con ProductDetailFragment
                (itemView.context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragment)
                    ?.addToBackStack(null)
                    ?.commit()

            }
        }
    }
}