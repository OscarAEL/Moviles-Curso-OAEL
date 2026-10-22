package com.tecsup.lavacampus.ui.util

import com.tecsup.lavacampus.data.local.entity.Servicio
import java.util.Locale

fun Servicio.textoPrecio(): String {
    val precioFormateado = String.format(Locale.US, "%.2f", precio)
    val unidadTexto = when (unidad.uppercase()) {
        "KG" -> "por kg"
        "PRENDA" -> "por prenda"
        "UNIDAD" -> "por unidad"
        else -> "por ${unidad.lowercase()}"
    }
    return "S/ $precioFormateado $unidadTexto"
}
