package com.appmoviles.utadeliciasapp

data class NotificacionPedido(
    val clienteNombre: String = "",
    val clienteApellido: String = "",
    val productos: List<ProductoPedido> = listOf(),
    val fecha: Long = System.currentTimeMillis(),
    val pedidoId: String = "",
    val userId: String = "",
)

// ProductoPedido.kt
