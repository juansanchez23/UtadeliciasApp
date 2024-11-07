package com.appmoviles.utadeliciasapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorCupones(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AdaptadorCupones.ViewHolder> (){

    private var datos: List<Cupones> = ArrayList()

    interface  OnItemClickListener{
        fun onItemClick(tuModelo : Cupones)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreComercio: TextView = itemView.findViewById(R.id.txtNombreComercio)
        val txtId: TextView = itemView.findViewById(R.id.txtId)
        val txtnombre: TextView = itemView.findViewById(R.id.txtnombre)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtdescripcion)
        val imagenCupon: ImageView = itemView.findViewById(R.id.imagenCupon)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cupon = datos[position]

                    // Iniciar la actividad de detalle
                    val intent = Intent(itemView.context, DetalleCuponComercio::class.java).apply {
                        putExtra("nombre", cupon.nombre)
                        putExtra("descripcion", cupon.descripcion)
                        putExtra("imagenUrl", cupon.imagenUrl)
                        putExtra("nombreComercio",cupon.nombreComercio)
                    }
                    itemView.context.startActivity(intent)

                    // Mantener el callback original
                    itemClickListener.onItemClick(cupon)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.cupones_layout,parent,false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return datos.size
    }

    fun setDatos(datos : List<Cupones>)
    {
        this.datos= datos
        notifyDataSetChanged()


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtId.text = item.id.toString()
        holder.txtnombre.text = item.nombre
        holder.txtDescripcion.text = item.descripcion
        holder.txtNombreComercio.text = item.nombreComercio


        if (item.imagenUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imagenUrl)
                .into(holder.imagenCupon)
        }
    }

}