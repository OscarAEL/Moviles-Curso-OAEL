package com.eneque.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.spatialk.geojson.Position

/** Estado exclusivamente visual, guardado fuera del mapa y de las ramas del layout adaptable. */
class EstadoCamaraMapa(
    var posicion: CameraPosition = CameraPosition(
        center = Position(latitude = -12.0464, longitude = -77.0428), zoom = 12.0),
    var ultimaUbicacion: String? = null,
    var ultimaSeleccion: Int? = null
) {
    companion object {
        val Saver = mapSaver(
            save = { estado: EstadoCamaraMapa ->
                mapOf("lat" to estado.posicion.center.latitude,
                    "lon" to estado.posicion.center.longitude,
                    "zoom" to estado.posicion.zoom,
                    "bearing" to estado.posicion.bearing,
                    "pitch" to estado.posicion.pitch,
                    "ubicacion" to estado.ultimaUbicacion,
                    "seleccion" to estado.ultimaSeleccion)
            },
            restore = {
                EstadoCamaraMapa(
                    CameraPosition(center = Position(latitude = it["lat"] as Double,
                        longitude = it["lon"] as Double),
                        zoom = it["zoom"] as Double, bearing = it["bearing"] as Double,
                        pitch = it["pitch"] as Double),
                    it["ubicacion"] as String?, it["seleccion"] as Int?)
            }
        )
    }
}

@Composable
fun rememberEstadoCamaraMapa(): EstadoCamaraMapa =
    rememberSaveable(saver = EstadoCamaraMapa.Saver) { EstadoCamaraMapa() }
