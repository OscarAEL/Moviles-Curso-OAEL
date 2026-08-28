package com.eneque.lab02carritokotlin

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

fun mostrarDetalle(productos: List<Producto>) {
    println()
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f",
            i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else-> 0.0
    }
}

fun main() {
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP ")
    println("=========================================")

    val nombreCliente = "Oscar Eneque" // String (inferido)
    val carrito = mutableListOf<Producto>() // lista vacía de productos

    println("Cliente: $nombreCliente")
    println()

    carrito.add(Producto("Laptop HP", 2500.0, 1))
    carrito.add(Producto("Mouse Logitech", 45.5, 2))
    carrito.add(Producto("Teclado Mecanico", 120.0, 1))
    carrito.add(Producto("Monitor 24", 650.0, 2))
    //carrito.add(Producto("Impresora Epson", 900.0, 1))

    for (producto in carrito) {
        println("Producto agregado: ${producto.nombre}")
    }

    mostrarDetalle(carrito)
    println("Cantidad de productos: ${carrito.size}")

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println(String.format("Subtotal: S/ %.2f", subtotal))
    println(String.format("IGV (18%%): S/ %.2f", igv))
    println(String.format("TOTAL: S/ %.2f", total))

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println()
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format("(S/ %.2f)", masCaro.precio))
    }

    val descuento = calcularDescuento(total)
    val totalConDescuento = total - descuento

    if (descuento > 0) {
        if (total > 5000) {
            println("Descuento aplicado: 10% por compra mayor a S/ 5000")
        } else {
            println("Descuento aplicado: 5% por compra mayor a S/ 3000")
        }
    } else {
        println("No se aplico descuento")
    }

    println(String.format("TOTAL CON DESCUENTO: S/ %.2f", totalConDescuento))
}

