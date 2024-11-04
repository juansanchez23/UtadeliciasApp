package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class DetalleProducto : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera los datos de los argumentos
        val nombre = arguments?.getString("nombre") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val imagenUrl = arguments?.getString("imagenUrl") ?: ""

        // Configura las vistas
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreDetalle)
        val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcionDetalle)
        val ivImagen = view.findViewById<ImageView>(R.id.ivImagenDetalle)
        val btnVolver = view.findViewById<Button>(R.id.btnVolver)

        // Asigna los datos a las vistas
        tvNombre.text = nombre
        tvDescripcion.text = descripcion
        Glide.with(requireContext()).load(imagenUrl).into(ivImagen)

        // Configura el botón de volver
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(nombre: String, descripcion: String, imagenUrl: String) =
            DetalleProducto().apply {
                arguments = Bundle().apply {
                    putString("nombre", nombre)
                    putString("descripcion", descripcion)
                    putString("imagenUrl", imagenUrl)
                }
            }
    }
}
