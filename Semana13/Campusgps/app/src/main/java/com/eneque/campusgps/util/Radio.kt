package com.eneque.campusgps.util

import com.eneque.campusgps.model.Posicion
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun estaDentroDelRadio(metros: Float?, radioMetros: Int): Boolean =
    metros != null && metros.isFinite() && metros >= 0f && metros <= radioMetros

/** Aproximación esférica en metros; anillo cerrado y antihorario para GeoJSON. */
fun crearContornoRadio(centro: Posicion, radioMetros: Int): List<Posicion> {
    require(radioMetros > 0)
    if (!centro.latitud.isFinite() || !centro.longitud.isFinite() ||
        centro.latitud !in -90.0..90.0 || centro.longitud !in -180.0..180.0
    ) return emptyList()

    val latitud = Math.toRadians(centro.latitud)
    val longitud = Math.toRadians(centro.longitud)
    val distanciaAngular = radioMetros / 6_371_008.8
    val senoLatitud = sin(latitud)
    val cosenoLatitud = cos(latitud)
    val senoDistancia = sin(distanciaAngular)
    val cosenoDistancia = cos(distanciaAngular)
    val puntos = List(64) { indice ->
        val rumbo = -2.0 * PI * indice / 64
        val senoDestino = (senoLatitud * cosenoDistancia +
            cosenoLatitud * senoDistancia * cos(rumbo)).coerceIn(-1.0, 1.0)
        val destinoLatitud = asin(senoDestino)
        val destinoLongitud = longitud + atan2(
            sin(rumbo) * senoDistancia * cosenoLatitud,
            cosenoDistancia - senoLatitud * senoDestino
        )
        Posicion(
            latitud = Math.toDegrees(destinoLatitud),
            longitud = (Math.toDegrees(destinoLongitud) + 540.0) % 360.0 - 180.0
        )
    }
    return puntos + puntos.first()
}
