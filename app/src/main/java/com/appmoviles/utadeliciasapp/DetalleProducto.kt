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
import com.google.firebase.auth.FirebaseAuth

class DetalleProducto : Fragment() {

    private lateinit var producto: Products

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera los datos de los argumentos
        val id = arguments?.getString("id") ?: ""
        val nombre = arguments?.getString("nombre") ?: ""
        val descripcion = arguments?.getString("descripcion") ?: ""
        val imagenUrl = arguments?.getString("imagenUrl") ?: ""
        val cantidad = arguments?.getInt("cantidad")?: 9999
        val precio = arguments?.getInt("precio") ?: 9999

        // Crea el objeto Producto
        producto = Products(id, nombre, descripcion, imagenUrl, cantidad,precio)

        // Configura las vistas
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreDetalle)
        val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcionDetalle)
        val ivImagen = view.findViewById<ImageView>(R.id.ivImagenDetalle)
        val btnVolver = view.findViewById<Button>(R.id.btnVolver)
        val btnAgregarCarrito = view.findViewById<Button>(R.id.btnAñadirAlCarrito)

        // Asigna los datos a las vistas
        tvNombre.text = producto.nombre
        tvDescripcion.text = producto.descripcion
        Glide.with(requireContext()).load(producto.imagen).into(ivImagen)

        // Configura el botón de volver
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Configura el botón de agregar al carrito
        btnAgregarCarrito.setOnClickListener {
            agregarProductoAlCarrito(producto, 1) // Puedes cambiar la cantidad según las necesidades
        }
    }

    private fun agregarProductoAlCarrito(producto: Products, cantidad: Int) {
        val carrito = Carrito()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Agregar el producto al carrito
        val item = CarritoItem(producto, cantidad)
        carrito.items.add(item)

        // Guardar el carrito en Firestore
        carrito.guardarCarrito(userId)
    }

    companion object {
        fun newInstance(id: String, nombre: String, descripcion: String, imagenUrl: String, cantidad:Int, precio: Int) =
            DetalleProducto().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                    putString("nombre", nombre)
                    putString("descripcion", descripcion)
                    putString("imagenUrl", imagenUrl)
                    putInt("cantidad",cantidad)
                    putInt("precio", precio)
                }
            }
    }
}