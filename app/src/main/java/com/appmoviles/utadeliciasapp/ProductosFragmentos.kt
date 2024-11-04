package com.appmoviles.utadeliciasapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductosFragmentos : Fragment(), ProductsAdapter.OnItemClickListener {

    // Variables y configuración básica
    private lateinit var btnAddProduct: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductsAdapter

    private val db = FirebaseFirestore.getInstance()
    private val productosColeccion = db.collection("Products")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_productos_fragmentos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddProduct = view.findViewById(R.id.btnaddProduct)
        recyclerView = view.findViewById(R.id.rvproducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductsAdapter(this)
        recyclerView.adapter = adapter

        consultarColeccion()

        // Navegar a agregar producto
        btnAddProduct.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, AddProductsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun consultarColeccion() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        productosColeccion.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                val listaProductos = mutableListOf<Products>()
                for (document in querySnapshot) {
                    val nombre = document.getString("Nombre")
                    val descripcion = document.getString("Descripción")
                    val imagen = document.getString("ImagenUrl") ?: ""
                    val txtcantidad = document.getLong("Cantidad")?.toInt()
                    val ID = document.id
                    if (nombre != null && descripcion != null && txtcantidad != null) {
                        val producto = Products(ID, nombre, descripcion, imagen, txtcantidad)
                        listaProductos.add(producto)
                    }
                }
                adapter.setDatos(listaProductos)
            }
            .addOnFailureListener { e ->
                // Manejar el error en caso de fallo
            }
    }

    override fun onDeleteItemClick(productId: String) {
        productosColeccion.document(productId).delete()
            .addOnSuccessListener {
                consultarColeccion()  // Actualizar lista
            }
            .addOnFailureListener { e ->
                // Manejar el error en caso de fallo en la eliminación
            }
    }
}
