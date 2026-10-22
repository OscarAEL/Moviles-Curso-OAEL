package com.tecsup.lavacampus.util

import java.time.LocalDate
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale

object FechaUtils {
    fun calcularFechaEntrega(fechaRegistro: LocalDate, horas: Int): String {
        var entrega = fechaRegistro.plusDays((horas / 24).coerceAtLeast(1).toLong())
        if (entrega.dayOfWeek == DayOfWeek.SUNDAY) entrega = entrega.plusDays(1)
        return entrega.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private val localeEs = Locale.forLanguageTag("es-ES")
    private val formatterCorta = DateTimeFormatter.ofPattern("EEE d MMM", localeEs)

    fun formatearFechaCorta(fechaIso: String): String {
        return try {
            val date = LocalDate.parse(fechaIso)
            val formatted = date.format(formatterCorta)
            formatted.replace(".", "").lowercase()
        } catch (_: Exception) {
            fechaIso
        }
    }
}
