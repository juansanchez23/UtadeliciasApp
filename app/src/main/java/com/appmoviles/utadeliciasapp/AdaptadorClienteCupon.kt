package com.appmoviles.utadeliciasapp



import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorClienteCupon(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AdaptadorClienteCupon.ViewHolder>() {

    private var datos: List<Cupones> = ArrayList()

    interface OnItemClickListener {
        fun onItemClick(cupon: Cupones)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtId: TextView = itemView.findViewById(R.id.txtId)
        val txtnombre: TextView = itemView.findViewById(R.id.txtnombre)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtdescripcion)
        val imagenCupon: ImageView = itemView.findViewById(R.id.imagenCupon)
        val txtNombreComercio: TextView = itemView.findViewById(R.id.txtNombreComercio)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val cupon = datos[position]
                    val intent = Intent(itemView.context, DetalleCuponActivity::class.java)
                    intent.putExtra("nombre", cupon.nombre)
                    intent.putExtra("descripcion", cupon.descripcion)
                    intent.putExtra("imagenUrl", cupon.imagenUrl)
                    intent.putExtra("userId",cupon.userId)
                    intent.putExtra("nombreComercio",cupon.nombreComercio)

                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cupones_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datos.size
    }

    fun setDatos(datos: List<Cupones>) {
        this.datos = datos
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtId.text = item.id
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