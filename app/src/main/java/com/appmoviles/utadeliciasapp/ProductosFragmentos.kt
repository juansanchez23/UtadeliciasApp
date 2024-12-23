package com.appmoviles.utadeliciasapp

import android.app.AlertDialog
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
                    val txtcantidad = document.getLong("Cantidad")?.toInt() ?: 0 // Asignación segura para cantidad
                    val txtprecio = document.getDouble("Precio") ?.toDouble()?: 0.0 // Asignación segura para precio
                    val ID = document.id

                    if (nombre != null && descripcion != null) {
                        val producto = Products(ID, nombre, descripcion, imagen, txtcantidad, txtprecio)
                        listaProductos.add(producto)
                    }
                }
                adapter.setDatos(listaProductos)
            }
            .addOnFailureListener { e ->
                // Manejar el error aquí
            }
    }


    override fun onDeleteItemClick(productId: String) {
        // Diálogo de confirmación antes de eliminar
        AlertDialog.Builder(context)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
            .setPositiveButton("Sí") { dialog, which ->
                productosColeccion.document(productId).delete()
                    .addOnSuccessListener {
                        consultarColeccion()  // Actualizar lista
                    }
                    .addOnFailureListener { e ->
                        // Manejar el error en caso de fallo en la eliminación
                    }

                dialog.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onItemClick(product: Products) {
        val productDetailFragment = ProductDetailFragment()

        // Pasar datos al nuevo fragmento
        val bundle = Bundle().apply {
            putString("productId", product.id)
            putString("productName", product.nombre)
            putString("productDescription", product.descripcion)
            putString("productImage", product.imagen)
            putInt("productQuantity", product.cantidad)
            putDouble("productPrice", product.precio)
        }
        productDetailFragment.arguments = bundle

        // Reemplaza el fragmento correctamente
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, productDetailFragment) // Cambia aquí
            .addToBackStack(null)
            .commit()
    }

}
