package com.appmoviles.utadeliciasapp

data class NotificacionPedido(
    val clienteNombre: String = "",
    val clienteApellido: String = "",
    val productos: List<ProductoPedido> = listOf(),
    val fecha: Long = System.currentTimeMillis(),
    var pedidoId: String = "",
    val userId: String = "",
    val estado: String? = null  // Nuevo campo para el estado

)

// ProductoPedido.kt
