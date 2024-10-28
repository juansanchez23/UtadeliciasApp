package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductsAdapter : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private var datos: List<Products> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtIdproduct:TextView = itemView.findViewById(R.id.txtIdproduct)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val ivProducto: ImageView= itemView.findViewById(R.id.ivProducto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return datos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtIdproduct.text=item.id.toString()
        holder.txtNombre.text=item.nombre
        holder.txtDescripcion.text=item.descripcion
        if (item.imagen.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imagen)
                .into(holder.ivProducto)
        }

    }

}