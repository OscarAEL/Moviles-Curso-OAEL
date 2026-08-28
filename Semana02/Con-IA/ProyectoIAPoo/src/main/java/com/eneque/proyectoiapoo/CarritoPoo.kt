package com.eneque.proyectoiapoo

data class Producto(
    val id: String,
    val nombre: String,
    val precio: Double,
    private var _stock: Int
) {
    val stock: Int get() = _stock

    fun tieneStock(cantidad: Int): Boolean = _stock >= cantidad

    fun reducirStock(cantidad: Int) {
        if (!tieneStock(cantidad)) throw IllegalArgumentException("Stock insuficiente para $nombre")
        _stock -= cantidad
    }

    fun reponerStock(cantidad: Int) {
        _stock += cantidad
    }
}

abstract class Descuento {
    abstract fun aplicar(monto: Double): Double
}

fun main() {
    println("Proyecto POO - Carrito de Compras (con IA)")
}