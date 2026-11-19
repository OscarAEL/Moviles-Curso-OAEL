package com.eneque.campusgps.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
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
import androidx.compose.ui.unit.sp
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.map.AndroidRenderMode
import org.maplibre.compose.map.MapUiOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.ExpandingAttributionButton
import org.maplibre.compose.overlay.MaplibreLogo
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.model.Posicion
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.Polygon
import com.eneque.campusgps.model.UbicacionUi

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier,
    masCercano: Int? = null,
    contornoRadio: List<Posicion> = emptyList()
) {
    val alcance = rememberCoroutineScope()
    var lugarAbierto by rememberSaveable { mutableStateOf<Int?>(null) }
    val posicion = (estado as? UbicacionUi.Lista)?.posicion?.takeIf {
        it.latitud.isFinite() && it.longitud.isFinite() &&
            it.latitud in -90.0..90.0 && it.longitud in -180.0..180.0
    }
    val centro = posicion?.let {
        Position(latitude = it.latitud, longitude = it.longitud)
    }
    val areaRadio = remember(contornoRadio) {
        contornoRadio.takeIf { it.isNotEmpty() }?.let { puntos ->
            Polygon(listOf(puntos.map { Position(latitude = it.latitud, longitude = it.longitud) }))
        }
    }
    val mapa = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = CameraPosition(
            center = Position(latitude = -12.0464, longitude = -77.0428),
            zoom = 12.0
        )
    ) {
        if (areaRadio != null) {
            val fuente = rememberGeoJsonSource(GeoJsonData.Features(areaRadio))
            FillLayer(
                id = "radio-cercania-relleno",
                source = fuente,
                color = const(Color(0xFF1565C0)),
                opacity = const(0.12f)
            )
            LineLayer(
                id = "radio-cercania-borde",
                source = fuente,
                color = const(Color(0xFF1565C0)),
                width = const(2.dp)
            )
        }
    }

    LaunchedEffect(estado) {
        if (centro != null) {
            mapa.animateCamera(CameraUpdate(center = centro, zoom = 16.0))
        }
    }

    LaunchedEffect(seleccionado) {
        lugarAbierto = seleccionado
        val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
        if (lugar != null) {
            mapa.animateCamera(
                CameraUpdate(
                    center = Position(latitude = lugar.latitud, longitude = lugar.longitud),
                    zoom = 17.0
                )
            )
        }
    }

    LaunchedEffect(lugares) {
        if (lugares.none { it.lugar.id == lugarAbierto }) {
            lugarAbierto = null
        }
    }

    MaplibreMap(
        modifier = modifier,
        state = mapa,
        // TextureView mantiene la composición y los límites del mapa dentro de Compose.
        uiOptions = MapUiOptions { renderMode = AndroidRenderMode.Texture }
    ) {
        lugares.forEach { item ->
            val lugar = item.lugar
            key(lugar.id) {
                PinUbicacion(
                    color = if (lugar.id == masCercano) Color(0xFF2E7D32) else Color(0xFF007FFF),
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
        // Controles sobre los marcadores, separados del logo y de la atribución.
        MaplibreLogo(
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
        ExpandingAttributionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(horizontal = 8.dp),
            contentAlignment = Alignment.BottomEnd
        )
        Surface(
            modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp),
            shape = MaterialTheme.shapes.small,
            shadowElevation = 2.dp
        ) {
            Column {
                IconButton(
                    onClick = {
                        alcance.launch {
                            mapa.animateCamera(
                                CameraUpdate(zoom = (mapa.cameraPosition.zoom + 1.0).coerceIn(0.0, 22.0))
                            )
                        }
                    },
                    modifier = Modifier.size(48.dp).semantics { contentDescription = "Acercar mapa" }
                ) {
                    Text("+", fontSize = 24.sp)
                }
                IconButton(
                    onClick = {
                        alcance.launch {
                            mapa.animateCamera(
                                CameraUpdate(zoom = (mapa.cameraPosition.zoom - 1.0).coerceIn(0.0, 22.0))
                            )
                        }
                    },
                    modifier = Modifier.size(48.dp).semantics { contentDescription = "Alejar mapa" }
                ) {
                    Text("−", fontSize = 24.sp)
                }
            }
        }
    }
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
