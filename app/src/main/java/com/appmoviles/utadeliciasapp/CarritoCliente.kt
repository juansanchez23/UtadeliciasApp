package com.appmoviles.utadeliciasapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log

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
            val userId = auth.currentUser?.uid ?: return@setOnClickListener

            // Recuperar la información del usuario de Firestore
            firestore.collection("user-info").document(userId).get()
                .addOnSuccessListener { userDoc ->
                    val nombre = userDoc.getString("nombre") ?: ""
                    val apellido = userDoc.getString("apellido") ?: ""

                    // Crear una lista para los productos con sus respectivos comercio_id
                    val productosPedido = mutableListOf<ProductoPedido>()

                    // Recorrer los productos del carrito
                    val productosPendientes = carrito.items.map { item ->
                        // Obtener el producto desde Firestore para obtener su comercio_id
                        firestore.collection("Products")
                            .document(item.producto.id)
                            .get()
                            .addOnSuccessListener { productoDoc ->
                                // Obtener el comercio_id de Firestore
                                val comercioId = productoDoc.getString("comercio_id") ?: ""

                                // Crear el ProductoPedido con el comercio_id de ese producto
                                val productoPedido = ProductoPedido(
                                    productoId = item.producto.id,
                                    nombre = item.producto.nombre,
                                    cantidad = item.selectedQuantity,
                                    precio = item.producto.precio,
                                    comercio_id = comercioId // Usamos el comercio_id de Firestore
                                )

                                // Agregar el ProductoPedido a la lista
                                productosPedido.add(productoPedido)

                                // Si ya hemos procesado todos los productos, proceder a crear el pedido
                                if (productosPedido.size == carrito.items.size) {
                                    // Crear el objeto NotificacionPedido
                                    val pedido = NotificacionPedido(
                                        clienteNombre = nombre,
                                        clienteApellido = apellido,
                                        productos = productosPedido,
                                        fecha = System.currentTimeMillis(),
                                        pedidoId = firestore.collection("pedidos").document().id,
                                        userId = userId,
                                        comercio_id = productosPedido.firstOrNull()?.comercio_id ?: ""
                                    )

                                    // Guardar el pedido en Firestore
                                    firestore.collection("pedidos").document(pedido.pedidoId)
                                        .set(pedido)
                                        .addOnSuccessListener {
                                            val carritoRef = firestore.collection("user-info").document(userId)
                                                .collection("carrito").document("carritoId")

                                            carritoRef.update("items", emptyList<String>())
                                                .addOnSuccessListener {
                                                    carrito.items.clear()
                                                    carritoAdapter.notifyDataSetChanged()
                                                    actualizarTotal()

                                                    val cuponNoDisponible = CuponNoDisponible()
                                                    parentFragmentManager.beginTransaction()
                                                        .replace(R.id.navcliente, cuponNoDisponible)
                                                        .addToBackStack(null)
                                                        .commit()
                                                }
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(context, "Error al procesar el pedido", Toast.LENGTH_SHORT).show()
                                            exception.printStackTrace()
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Error al obtener el comercio_id del producto", Toast.LENGTH_SHORT).show()
                                exception.printStackTrace()
                            }
                    }

                    // Si el carrito está vacío, se muestra un mensaje o se maneja el caso
                    if (productosPendientes.isEmpty()) {
                        Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error al obtener la información del usuario", Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
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


    internal fun actualizarTotal() {
        val total = carrito.obtenerTotal().toInt() // Obtener el total actualizado (0 si el carrito está vacío)

        // Crear un formato de número con separadores de miles
        val formattedTotal = NumberFormat.getNumberInstance(Locale.getDefault()).format(total)

        tvTotal.text = "Total: $$formattedTotal" // Mostrar el total con formato de miles
    }

}

// Función de extensión para formatear el total
fun Double.format(digits: Int) = "%.${digits}f".format(this)
