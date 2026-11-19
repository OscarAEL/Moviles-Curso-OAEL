package com.eneque.campusgps.util

import com.eneque.campusgps.model.Posicion
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioTest {
    @Test
    fun limiteDelRadioEsInclusivo() {
        assertTrue(estaDentroDelRadio(0f, 100))
        assertTrue(estaDentroDelRadio(100f, 100))
        assertFalse(estaDentroDelRadio(100.01f, 100))
        assertTrue(estaDentroDelRadio(200f, 200))
        assertFalse(estaDentroDelRadio(200.01f, 200))
        assertTrue(estaDentroDelRadio(500f, 500))
        assertFalse(estaDentroDelRadio(500.01f, 500))
    }

    @Test
    fun distanciaDesconocidaOInvalidaNoSeMarcaDentro() {
        assertFalse(estaDentroDelRadio(null, 500))
        assertFalse(estaDentroDelRadio(Float.NaN, 500))
        assertFalse(estaDentroDelRadio(Float.POSITIVE_INFINITY, 500))
        assertFalse(estaDentroDelRadio(-1f, 500))
    }

    @Test
    fun coordenadasInvalidasNoProducenGeometria() {
        assertTrue(crearContornoRadio(Posicion(Double.NaN, -77.0), 100).isEmpty())
        assertTrue(crearContornoRadio(Posicion(91.0, -77.0), 100).isEmpty())
        assertTrue(crearContornoRadio(Posicion(-12.0, 181.0), 100).isEmpty())
    }
}
