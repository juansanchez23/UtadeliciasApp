package com.appmoviles.utadeliciasapp

import android.text.Editable

data class Products(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val imagen: String = ""
)
