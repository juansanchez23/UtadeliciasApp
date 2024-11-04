package com.appmoviles.utadeliciasapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class productos_cliente : Fragment() {

    // Variables y configuración básica
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorClienteProducto
    private lateinit var add: AddProductsFragment

    private val db = FirebaseFirestore.getInstance()
    private val productosColeccion = db.collection("Products")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_productos_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add = AddProductsFragment()
        recyclerView = view.findViewById(R.id.rvproductsCliente)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        adapter = AdaptadorClienteProducto()
        recyclerView.adapter = adapter

        consultarColeccion()
    }

    private fun consultarColeccion() {
        productosColeccion.get()
            .addOnSuccessListener { querySnapshot ->
                val listaProductos = mutableListOf<Products>()
                for (document in querySnapshot) {
                    val nombre = document.getString("Nombre")
                    val descripcion = document.getString("Descripción")
                    val imagen = document.getString("ImagenUrl") ?: ""
                    val ID = document.id
                    if (nombre != null && descripcion != null && imagen !=null) {
                        val producto = Products(ID, nombre, descripcion, imagen)
                        listaProductos.add(producto)
                    }
                }
                adapter.setDatos(listaProductos)
            }
            .addOnFailureListener { e ->
                // Manejar el error aquí
            }
    }
}
