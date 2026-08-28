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

class Carrito {
    private val items = mutableListOf<Pair<Producto, Int>>()

    fun agregarProducto(producto: Producto, cantidad: Int) {
        require(cantidad > 0) { "La cantidad debe ser positiva" }
        if (producto.tieneStock(cantidad)) {
            producto.reducirStock(cantidad)
            items.add(Pair(producto, cantidad))
        } else {
            println("Error: Stock insuficiente de ${producto.nombre}")
        }
    }

    fun calcularSubtotal(): Double {
        var subtotal = 0.0
        for ((producto, cantidad) in items) {
            subtotal += producto.precio * cantidad
        }
        return subtotal
    }

    fun calcularIGV(subtotal: Double): Double = subtotal * 0.18

    fun calcularTotal(subtotal: Double, igv: Double, descuento: Descuento? = null): Double {
        val base = subtotal + igv
        return descuento?.aplicar(base) ?: base
    }

    fun mostrarDetalle() {
        println("--------- DETALLE DEL CARRITO ---------")
        var i = 1
        for ((producto, cantidad) in items) {
            val importe = producto.precio * cantidad
            println(String.format("%d. %-20s x%d S/ %8.2f", i, producto.nombre, cantidad, importe))
            i++
        }
        println("---------------------------------------")
    }
}

fun main() {
    println("Proyecto POO - Carrito de Compras (con IA)")
}