package com.eneque.campusgps.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.map.AndroidRenderMode
import org.maplibre.compose.map.MapUiOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.MapOverlay
import org.maplibre.compose.overlay.include
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.model.UbicacionUi

@Suppress("UNUSED_PARAMETER")
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    var lugarAbierto by rememberSaveable { mutableStateOf<Int?>(null) }
    val posicion = (estado as? UbicacionUi.Lista)?.posicion?.takeIf {
        it.latitud.isFinite() && it.longitud.isFinite() &&
            it.latitud in -90.0..90.0 && it.longitud in -180.0..180.0
    }
    val centro = posicion?.let {
        Position(latitude = it.latitud, longitude = it.longitud)
    }
    val mapa = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = CameraPosition(
            center = Position(latitude = -12.0464, longitude = -77.0428),
            zoom = 12.0
        )
    )

    LaunchedEffect(estado) {
        if (centro != null) {
            mapa.animateCamera(CameraUpdate(center = centro, zoom = 16.0))
        }
    }

    MaplibreMap(
        modifier = modifier,
        state = mapa,
        // TextureView mantiene la composición y los límites del mapa dentro de Compose.
        uiOptions = MapUiOptions { renderMode = AndroidRenderMode.Texture }
    ) {
        // Conservar el logo y el control de atribución del proveedor.
        include(MapOverlay.Default)
        lugares.forEach { item ->
            val lugar = item.lugar
            key(lugar.id) {
                PinUbicacion(
                    color = Color(0xFF007FFF),
                    modifier = Modifier
                        .placedAt(
                            Position(latitude = lugar.latitud, longitude = lugar.longitud),
                            alignment = Alignment.BottomCenter
                        )
                        .semantics { contentDescription = "Marcador de ${lugar.nombre}" }
                        .clickable(onClickLabel = "Ver nombre y categoría") {
                            lugarAbierto = if (lugarAbierto == lugar.id) null else lugar.id
                        }
                )
            }
        }
        if (centro != null) {
            Column(
                modifier = Modifier.placedAt(centro, alignment = Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface {
                    Text("Estás aquí", modifier = Modifier.padding(4.dp))
                }
                PinUbicacion(color = Color(0xFFD32F2F))
            }
        }
        val lugarInfo = lugares.firstOrNull { it.lugar.id == lugarAbierto }?.lugar
        if (lugarInfo != null) {
            Column(
                modifier = Modifier
                    .placedAt(
                        Position(latitude = lugarInfo.latitud, longitude = lugarInfo.longitud),
                        alignment = Alignment.BottomCenter
                    )
                    .padding(bottom = 40.dp)
            ) {
                Surface(shadowElevation = 4.dp) {
                    Column(Modifier.padding(8.dp)) {
                        Text(lugarInfo.nombre)
                        Text(lugarInfo.categoria)
                    }
                }
            }
        }
    }

    // TODO Paso 6: mover la cámara al lugar seleccionado.
    // TODO Paso 7: resaltar el lugar más cercano.
}

@Composable
private fun PinUbicacion(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.size(28.dp, 36.dp)) {
        val pin = Path().apply {
            moveTo(size.width / 2, size.height)
            cubicTo(-size.width / 2, size.height / 3,
                size.width / 4, 0f, size.width / 2, 0f)
            cubicTo(size.width * 3 / 4, 0f,
                size.width * 3 / 2, size.height / 3,
                size.width / 2, size.height)
            close()
        }
        drawPath(pin, color)
        drawCircle(
            Color.White,
            radius = size.width / 6,
            center = Offset(size.width / 2, size.height / 3)
        )
    }
}
