// Products.kt
package com.appmoviles.utadeliciasapp

data class Products(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagen: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0,
    val userId: String = ""
)
