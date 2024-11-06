import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.appmoviles.utadeliciasapp.Products
import com.appmoviles.utadeliciasapp.R
import com.bumptech.glide.Glide

class AdaptadorClienteProducto(
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdaptadorClienteProducto.ViewHolder>() {

    private val productosList = mutableListOf<Products>()

    interface OnItemClickListener {
        fun onItemClick(product: Products)
    }

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
                .load(product.imagen)
                .into(imagen)

            itemView.setOnClickListener {
                itemClickListener.onItemClick(product)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productosList[position]
        holder.bind(product)
    }
}
