package com.appmoviles.utadeliciasapp

import java.util.Date

data class Notificacion(
    val clienteNombre: String,
    val clienteApellido: String,
    val productos: List<ProductoPedido>,
    val fecha: Date?,
    val pedidoId: String
)