package com.appmoviles.utadeliciasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorCupones : RecyclerView.Adapter<AdaptadorCupones.ViewHolder> (){

    private var datos: List<Cupones> = ArrayList()
    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtId: TextView = itemView.findViewById(R.id.txtId)
        val txtnombre: TextView = itemView.findViewById(R.id.txtnombre)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtdescripcion)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.cupones_layout,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return datos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtId.text=item.id.toString()
        holder.txtnombre.text = item.nombre
        holder.txtDescripcion.text = item.descripcion
    }

}