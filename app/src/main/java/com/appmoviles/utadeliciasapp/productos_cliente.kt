package com.appmoviles.utadeliciasapp

import AdaptadorClienteProducto
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class productos_cliente : Fragment(), AdaptadorClienteProducto.OnItemClickListener {

    // Variables y configuración básica
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdaptadorClienteProducto

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
        val whatsappLink = "https://chat.whatsapp.com/DdQ2CrzuwWYKTBJUplxflC"
        val btnWhatsApp: Button = view.findViewById(R.id.btnWhatsApp)
        btnWhatsApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(whatsappLink)
            startActivity(intent)
        }
        // Referencia al TextView
        val tvUser: TextView = view.findViewById(R.id.tvUser)

        // Obtener el ID del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Referencia al documento del usuario en Firestore
            val userDocRef = db.collection("user-info").document(userId)

            // Obtener el nombre del usuario
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Obtener el nombre del campo 'nombre'
                        val nombre = documentSnapshot.getString("nombre")
                        if (nombre != null) {
                            tvUser.text = nombre // Establece el nombre en el TextView
                        } else {
                            tvUser.text = "Nombre no disponible"
                        }
                    } else {
                        tvUser.text = "Usuario no encontrado"
                    }
                }
                .addOnFailureListener { exception ->
                    tvUser.text = "Error al obtener el nombre"
                }
        } else {
            tvUser.text = "No hay usuario autenticado"
        }

        recyclerView = view.findViewById(R.id.rvproductsCliente)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Inicializa el adaptador con this como el listener para los clics
        adapter = AdaptadorClienteProducto(this)
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
                    val txtcantidad = document.getLong("Cantidad")?.toInt() ?: 0
                    val txtprecio = document.getDouble("Precio")?.toDouble() ?: 0.0
                    val ID = document.id

                    if (nombre != null && descripcion != null) {
                        val producto = Products(ID, nombre, descripcion, imagen, txtcantidad, txtprecio)
                        listaProductos.add(producto)
                    }
                }
                adapter.setDatos(listaProductos)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener los productos: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }





    override fun onItemClick(product: Products) {
        // Inicia el fragmento de detalle y pasa los datos
        val detalleFragment = DetalleProducto.newInstance(
            product.id,product.nombre, product.descripcion, product.imagen, product.cantidad, product.precio
        )

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.navcliente, detalleFragment)  // Asegúrate de que el ID sea correcto
            .addToBackStack(null)
            .commit()
    }
}
