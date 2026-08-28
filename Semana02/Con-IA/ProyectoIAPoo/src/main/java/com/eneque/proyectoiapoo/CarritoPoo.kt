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

class DescuentoFijo(private val montoARestar: Double) : Descuento() {
    override fun aplicar(monto: Double): Double = (monto - montoARestar).coerceAtLeast(0.0)
}

class DescuentoPorcentaje(private val porcentaje: Double) : Descuento() {
    init {
        require(porcentaje in 0.0..100.0) { "Porcentaje inválido" }
    }
    override fun aplicar(monto: Double): Double = monto * (1 - porcentaje / 100)
}

fun main() {
    println("Proyecto POO - Carrito de Compras (con IA)")
}