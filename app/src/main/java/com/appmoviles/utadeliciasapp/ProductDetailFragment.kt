package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class ProductDetailFragment : Fragment() {

    private lateinit var productId: String
    private lateinit var productName: String
    private lateinit var productDescription: String
    private lateinit var productImage: String
    private var productQuantity: Int = 0
    private var tvprecio:Double = 0.0
    private lateinit var ivVovler:ImageView
    private lateinit var btnEdit:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getString("productId") ?: ""
            productName = it.getString("productName") ?: ""
            productDescription = it.getString("productDescription") ?: ""
            productImage = it.getString("productImage") ?: ""
            productQuantity = it.getInt("productQuantity", 0)
            tvprecio = it.getDouble("productPrice",0.0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout y configura las vistas
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val ivProductImage = view.findViewById<ImageView>(R.id.ivProducts)
        val tvQuantity = view.findViewById<TextView>(R.id.tvCantidad)
        val tvPrecio = view.findViewById<TextView>(R.id.tvPrecio)

        btnEdit=view.findViewById(R.id.btnEdit)
        btnEdit.setOnClickListener {
            val editProductFragment = EditProductFragment()

            // Crea un Bundle y pasa los datos del producto
            val bundle = Bundle().apply {
                putString("productId", productId)
                putString("productName", productName)
                putString("productDescription", productDescription)
                putString("productImage", productImage)
                putInt("productQuantity", productQuantity)
                putDouble("productPrice", tvprecio)
            }
            editProductFragment.arguments = bundle

            // Reemplaza el fragmento actual con EditProductFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, editProductFragment)
                .addToBackStack(null)
                .commit()
        }

        ivVovler=view.findViewById(R.id.ivVovler)
        ivVovler.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvName.text = productName
        tvDescription.text = productDescription
        tvQuantity.text = productQuantity.toString()
        tvPrecio.text = tvprecio.toString()


        // Cargar la imagen del producto (usando Glide)
        Glide.with(this)
            .load(productImage)
            .into(ivProductImage)

        return view
    }
}
