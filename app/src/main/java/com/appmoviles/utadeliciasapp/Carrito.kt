package com.appmoviles.utadeliciasapp

import com.google.firebase.firestore.FirebaseFirestore

data class Carrito(
    val items: MutableList<CarritoItem> = mutableListOf()
) {
    fun obtenerTotal(): Double {
        var total = 0.0
        for (item in items) {
            total += item.producto.precio * item.selectedQuantity // Asumiendo que `precio` está en la clase `Products`
        }
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
}



