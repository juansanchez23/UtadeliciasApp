package com.appmoviles.utadeliciasapp

data class ProductoPedido(
    val productoId: String = "",
    val nombre: String = "",
    val cantidad: Int = 0,
    val precio: Double = 0.0,
    val comercio_id: String = ""
)