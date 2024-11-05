package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ProductDetailFragment(
    nombre: String,
    descripcion: String,
    imagen: String,
    precio: Int,
    cantidad: Int
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productName = arguments?.getString("productName")
        val productDescription = arguments?.getString("productDescription")
        val productImage = arguments?.getString("productImageUrl")
        val productoCantidad = arguments?.getString("productQuantity")?.toIntOrNull()
        val productoPrecio = arguments?.getString("productPrice")?.toIntOrNull()

        // Ahora, asigna los valores a los elementos de tu layout, por ejemplo:
        view.findViewById<TextView>(R.id.tvName).text = productName
        view.findViewById<TextView>(R.id.tvDescription).text = productDescription
        view.findViewById<TextView>(R.id.tvCantidad).text = productoCantidad.toString()
        view.findViewById<TextView>(R.id.tvPrecio).text = productoPrecio.toString()


        val imageView = view.findViewById<ImageView>(R.id.ivProducts)
        Glide.with(this).load(productImage).into(imageView)
    }
}