package com.appmoviles.utadeliciasapp

data class CarritoItem(
    var producto: Products = Products(), // Producto con un constructor vacío en Products
    var selectedQuantity: Int = 1
) {
    constructor() : this(Products(), 1)
}