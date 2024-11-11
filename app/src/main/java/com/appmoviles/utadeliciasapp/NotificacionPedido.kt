package com.appmoviles.utadeliciasapp

data class NotificacionPedido(
    val pedidoId: String = "",
    val clienteNombre: String = "",
    val clienteApellido: String = "",
    val productos: List<ProductoPedido> = emptyList(),
    val fecha: Long = 0,
    val comercio_id: String = "",
    val userId: String = "",
    val estado: String = "pendiente"
)

// ProductoPedido.kt
