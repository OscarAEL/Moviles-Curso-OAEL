package com.tecsup.lavacampus.model

import java.math.BigDecimal
import java.math.RoundingMode

data class LineaCalculo(val unidad: String, val cantidad: Double, val precio: Double)
data class TotalesPedido(
    val subtotal: Double = 0.0,
    val descuento: Double = 0.0,
    val total: Double = 0.0,
    val descuentosPorLinea: List<Double> = emptyList()
)

/** Misma regla monetaria para la previsualización y la transacción que guarda el pedido. */
object CalculoPedido {
    private fun moneda(valor: BigDecimal) = valor.setScale(2, RoundingMode.HALF_UP)
    fun importe(cantidad: Double, precio: Double): Double =
        moneda(BigDecimal.valueOf(cantidad).multiply(BigDecimal.valueOf(precio))).toDouble()

    fun calcular(lineas: List<LineaCalculo>): TotalesPedido {
        val volumen = lineas.filter { it.unidad == "KG" }.sumOf { it.cantidad } > 5.0
        val importes = lineas.map { BigDecimal.valueOf(importe(it.cantidad, it.precio)) }
        val descuentos = lineas.mapIndexed { i, linea ->
            if (volumen && linea.unidad == "KG") moneda(importes[i].multiply(BigDecimal("0.10")))
            else BigDecimal.ZERO
        }
        val subtotal = importes.fold(BigDecimal.ZERO, BigDecimal::add)
        val descuento = descuentos.fold(BigDecimal.ZERO, BigDecimal::add)
        return TotalesPedido(subtotal.toDouble(), descuento.toDouble(), subtotal.subtract(descuento).toDouble(), descuentos.map { it.toDouble() })
    }
}
