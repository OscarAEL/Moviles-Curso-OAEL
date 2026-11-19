package com.eneque.campusgps.util

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatoTest {
    @Test
    fun metrosEnterosPorDebajoDelUmbral() {
        assertEquals("0 m", formatearDistancia(0f))
        assertEquals("350 m", formatearDistancia(350f))
        assertEquals("999 m", formatearDistancia(999.9f))
    }

    @Test
    fun kilometrosConUnDecimalDesdeMilMetros() {
        assertEquals("1.0 km", formatearDistancia(1000f))
        assertEquals("1.5 km", formatearDistancia(1500f))
        assertEquals("12.3 km", formatearDistancia(12345f))
    }

    @Test
    fun conservaPuntoDecimalConConfiguracionEnEspanol() {
        val anterior = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("es-PE"))
            assertEquals("1.5 km", formatearDistancia(1500f))
        } finally {
            Locale.setDefault(anterior)
        }
    }
}
