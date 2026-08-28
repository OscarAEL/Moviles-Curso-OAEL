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
    println("=========================================")
    println(" CARRITO DE COMPRAS POO - TIENDA TECSUP ")
    println("=========================================")

    val laptop = Producto("P01", "Laptop HP", 2500.0, 10)
    val mouse = Producto("P02", "Mouse Logitech", 45.5, 50)
    val teclado = Producto("P03", "Teclado Mecanico", 120.0, 20)
    val monitor = Producto("P04", "Monitor 24", 650.0, 15)

    val carrito = Carrito()
    carrito.agregarProducto(laptop, 1)
    carrito.agregarProducto(mouse, 2)
    carrito.agregarProducto(teclado, 1)
    carrito.agregarProducto(monitor, 2)

    println()
    carrito.mostrarDetalle()

    val subtotal = carrito.calcularSubtotal()
    val igv = carrito.calcularIGV(subtotal)
    val montoBase = subtotal + igv

    val descuento: Descuento? = when {
        montoBase > 5000 -> DescuentoPorcentaje(10.0)
        montoBase > 3000 -> DescuentoPorcentaje(5.0)
        else -> null
    }

    val total = carrito.calcularTotal(subtotal, igv, descuento)

    println()
    println(String.format("Subtotal: S/ %.2f", subtotal))
    println(String.format("IGV (18%%): S/ %.2f", igv))
    if (descuento != null) {
        println("Descuento aplicado (polimorfismo: ${descuento::class.simpleName})")
    } else {
        println("No se aplico descuento")
    }
    println(String.format("TOTAL FINAL: S/ %.2f", total))
}