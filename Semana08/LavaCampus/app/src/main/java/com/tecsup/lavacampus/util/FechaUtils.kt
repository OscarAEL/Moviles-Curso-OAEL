package com.tecsup.lavacampus.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object FechaUtils {
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
