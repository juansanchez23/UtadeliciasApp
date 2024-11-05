package com.appmoviles.utadeliciasapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CarritoCliente : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirmarCompra: Button
    private lateinit var carrito: Carrito

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para el fragmento del carrito
        return inflater.inflate(R.layout.fragment_carrito_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar los elementos de UI
        recyclerView = view.findViewById(R.id.rvCarrito)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnConfirmarCompra = view.findViewById(R.id.btnConfirmarCompra)

        // Cargar el carrito desde Firebase
        cargarCarrito()

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Configurar el botón de confirmación
        btnConfirmarCompra.setOnClickListener {
            confirmarCompra()
        }
    }

    private fun cargarCarrito() {
        val userId = auth.currentUser?.uid ?: return // Obtener el ID del usuario actual

        // Acceder a la subcolección "carrito" y cargar los items
        firestore.collection("user-info").document(userId)
            .collection("carrito").document("carritoId") // Cambia esto según la lógica de tu aplicación
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Si el carrito ya existe, cargarlo
                    carrito = document.toObject(Carrito::class.java) ?: Carrito()
                } else {
                    // Si no existe, crear un nuevo carrito
                    carrito = Carrito()
                    // Guarda el carrito vacío si es necesario
                    carrito.guardarCarrito(userId)
                }

                // Configurar el adaptador después de cargar el carrito
                val carritoAdapter = CarritoAdapter(carrito.items)
                recyclerView.adapter = carritoAdapter

                // Actualizar el total
                actualizarTotal()
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener el carrito
                exception.printStackTrace()
            }
    }

    private fun actualizarTotal() {
        val total = carrito.obtenerTotal()
        // Convierte total a entero
        val totalEntero = total.toInt()
        tvTotal.text = "Total: $totalEntero"
    }

    private fun confirmarCompra() {
        // Implementar la lógica de confirmación de compra, por ejemplo, guardar el carrito en Firebase
    }


}
