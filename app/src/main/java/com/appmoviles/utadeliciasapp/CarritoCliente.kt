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
import java.text.NumberFormat
import java.util.Locale

class CarritoCliente : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirmarCompra: Button
    private lateinit var carrito: Carrito
    private lateinit var carritoAdapter: CarritoAdapter // Declarar el adaptador aquí

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carrito_cliente, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvCarrito)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnConfirmarCompra = view.findViewById(R.id.btnConfirmarCompra)

        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Configurar el layout manager

        cargarCarrito() // Cargar el carrito desde Firestore

        btnConfirmarCompra.setOnClickListener {
            confirmarCompra() // Llamar a la función de confirmación de compra
        }
    }

    private fun cargarCarrito() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("user-info").document(userId)
            .collection("carrito").document("carritoId") // Cambia esto según la lógica de tu aplicación
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    carrito = document.toObject(Carrito::class.java) ?: Carrito()
                } else {
                    carrito = Carrito()
                    carrito.guardarCarrito(userId)
                }

                carritoAdapter = CarritoAdapter(carrito.items.toMutableList()) { productoId, position ->
                    // Eliminar el producto del carrito
                    carrito.eliminarProducto(productoId) // Eliminar del carrito
                    carritoAdapter.eliminarProducto(position) // Llama al método de eliminar en la instancia del adaptador
                    actualizarTotal() // Actualiza el total
                    carrito.guardarCarrito(userId) // Guarda el carrito actualizado en Firestore
                }
                recyclerView.adapter = carritoAdapter // Asigna el adaptador a RecyclerView

                actualizarTotal() // Actualiza el total después de cargar el carrito
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun confirmarCompra() {
        // Implementar la lógica de confirmación de compra
    }

    internal fun actualizarTotal() {
        val total = carrito.obtenerTotal().toInt() // Convertir el total a entero

        // Crear un formato de número con separadores de miles
        val formattedTotal = NumberFormat.getNumberInstance(Locale.getDefault()).format(total)

        tvTotal.text = "Total: $$formattedTotal" // Mostrar el total con formato de miles
    }
}

// Función de extensión para formatear el total
fun Double.format(digits: Int) = "%.${digits}f".format(this)
