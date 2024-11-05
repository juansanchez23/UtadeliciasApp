package com.appmoviles.utadeliciasapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class Carrito(
    val items: MutableList<CarritoItem> = mutableListOf()
) {
    fun obtenerTotal(): Double {
        var total = 0.0
        for (item in items) {
            Log.d("Carrito", "Producto: ${item.producto.nombre}, Precio: ${item.producto.precio}, Cantidad: ${item.selectedQuantity}")
            total += item.producto.precio * item.selectedQuantity
        }
        Log.d("Carrito", "Total: $total")
        return total
    }


    fun guardarCarrito(userId: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("user-info").document(userId)
            .collection("carrito").document("carritoId") // Cambia "carritoId" según tu lógica
            .set(this)
            .addOnSuccessListener {
                // Manejar éxito
            }
            .addOnFailureListener { exception ->
                // Manejar error
                exception.printStackTrace()
            }
    }
    fun eliminarProducto(productoId: String) {
        items.removeIf { it.producto.id == productoId }
    }

}



