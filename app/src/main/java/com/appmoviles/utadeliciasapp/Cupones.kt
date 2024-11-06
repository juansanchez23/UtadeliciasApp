package com.appmoviles.utadeliciasapp



data class Cupones(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val imagenUrl: String = "",
    val userId: String,
    var nombreComercio: String = ""
)

