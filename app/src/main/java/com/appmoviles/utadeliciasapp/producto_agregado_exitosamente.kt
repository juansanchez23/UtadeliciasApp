package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

private var nombre: String? = null
private var imagen: String? = null

class producto_agregado_exitosamente : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_producto_agregado_exitosamente, container, false)

        nombre = arguments?.getString("nombreProducto")
        imagen = arguments?.getString("imagenProducto")

        view.findViewById<TextView>(R.id.tvnombre).text = nombre

        val imagenUrl = view.findViewById<ImageView>(R.id.ivProducts)

        Glide.with(this)
            .load(imagen)
            .into(imagenUrl)


        // Configurar el click listener para el botón de regreso
        view.findViewById<View>(R.id.btn_volver_comercio_eliminado).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Configurar el click listener para el botón de agregar producto
        view.findViewById<View>(R.id.btn_volver_comercio_eliminado).setOnClickListener {
            // Cambiar a otro fragmento al hacer clic en el botón
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, ProductosFragmentos()) // Cambia ProductosFragmentos por el fragmento al que deseas volver
                .addToBackStack(null)
                .commit()
        }

        // Configurar el click listener para ivBackAdd
        view.findViewById<View>(R.id.vovler_agregado).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        fun newInstance(nombre: String, imagen: String): producto_agregado_exitosamente {
            val fragment = producto_agregado_exitosamente()
            val args = Bundle()
            args.putString("nombreProducto", nombre)
            args.putString("imagenProducto", imagen)
            fragment.arguments = args
            return fragment
        }
    }
}
