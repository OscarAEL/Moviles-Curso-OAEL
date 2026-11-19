package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.campusgps.model.Lugar
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.ui.theme.CampusMarker
import com.eneque.campusgps.ui.theme.CampusNearest
import com.eneque.campusgps.ui.theme.CampusUser
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.FillLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.map.AndroidRenderMode
import org.maplibre.compose.map.MapState
import org.maplibre.compose.map.MapUiOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.overlay.GeographicLayout
import org.maplibre.compose.overlay.MaplibreLogo
import org.maplibre.compose.overlay.attributions
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Polygon
import org.maplibre.spatialk.geojson.Position
import kotlin.math.roundToInt

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier,
    masCercano: Int? = null,
    contornoRadio: List<Posicion> = emptyList(),
    onSeleccionarLugar: (Int?) -> Unit,
    estadoCamara: EstadoCamaraMapa = rememberEstadoCamaraMapa()
) {
    val alcance = rememberCoroutineScope()
    val colores = MaterialTheme.colorScheme
    val escala = LocalDensity.current.fontScale
    val alturaAtribucion = if (escala >= 1.6f) 88.dp else if (escala >= 1.3f) 64.dp else 44.dp
    var mostrarAtribucion by rememberSaveable { mutableStateOf(false) }
    val posicion = (estado as? UbicacionUi.Lista)?.posicion?.takeIf {
        it.latitud.isFinite() && it.longitud.isFinite() &&
            it.latitud in -90.0..90.0 && it.longitud in -180.0..180.0
    }
    val centro = posicion?.let { Position(latitude = it.latitud, longitude = it.longitud) }
    val areaRadio = remember(contornoRadio) {
        contornoRadio.takeIf { it.isNotEmpty() }?.let { puntos ->
            Polygon(listOf(puntos.map { Position(latitude = it.latitud, longitude = it.longitud) }))
        }
    }
    val mapa = rememberMapState(
        baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
        initialCameraPosition = estadoCamara.posicion
    ) {
        if (areaRadio != null) {
            val fuente = rememberGeoJsonSource(GeoJsonData.Features(areaRadio))
            FillLayer("radio-cercania-relleno", fuente, color = const(colores.primary),
                opacity = const(0.12f))
            LineLayer("radio-cercania-borde", fuente, color = const(colores.primary),
                width = const(1.5.dp))
        }
    }
    LaunchedEffect(mapa) {
        snapshotFlow { mapa.cameraPosition }.collect { estadoCamara.posicion = it }
    }
    LaunchedEffect(estado) {
        if (estado == UbicacionUi.Buscando) estadoCamara.ultimaUbicacion = null
        val clave = posicion?.let { "${it.latitud},${it.longitud}" }
        if (centro != null && estadoCamara.ultimaUbicacion != clave) {
            estadoCamara.ultimaUbicacion = clave
            mapa.animateCamera(CameraUpdate(center = centro, zoom = 16.0))
        }
    }
    LaunchedEffect(seleccionado) {
        if (seleccionado != estadoCamara.ultimaSeleccion) {
            estadoCamara.ultimaSeleccion = seleccionado
            val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
            if (lugar != null) mapa.animateCamera(CameraUpdate(
                center = Position(latitude = lugar.latitud, longitude = lugar.longitud), zoom = 17.0))
        }
    }
    MaplibreMap(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).clipToBounds(),
        state = mapa,
        viewportInsets = PaddingValues(
            top = if (seleccionado != null) (if (escala >= 1.3f) 64.dp else 40.dp) else 0.dp,
            bottom = alturaAtribucion),
        interactions = MapInteractions {
            callbacks {
                click { onUnhandled { onSeleccionarLugar(null); ClickResult.Consume } }
            }
        },
        uiOptions = MapUiOptions { renderMode = AndroidRenderMode.Texture }
    ) {
        // Esta región termina antes de la atribución: ningún marcador o ventana puede taparla.
        GeographicLayout(Modifier.matchParentSize().padding(bottom = alturaAtribucion).clipToBounds()) {
            lugares.forEach { item ->
                val lugar = item.lugar
                key(lugar.id) {
                    MarcadorMapa(
                        color = if (lugar.id == masCercano) CampusNearest else CampusMarker,
                        seleccionado = lugar.id == seleccionado,
                        modifier = Modifier.placedAt(
                            Position(latitude = lugar.latitud, longitude = lugar.longitud))
                            .semantics { contentDescription = "Marcador de ${lugar.nombre}" }
                            .clickable(onClickLabel = "Seleccionar lugar") {
                                onSeleccionarLugar(if (seleccionado == lugar.id) null else lugar.id)
                            }
                    )
                }
            }
            if (centro != null) {
                MarcadorMapa(CampusUser, false, Modifier.placedAt(centro))
                Surface(
                    modifier = Modifier.placedAt(centro, Alignment.BottomCenter).padding(bottom = 20.dp),
                    shape = RoundedCornerShape(8.dp), color = colores.surface
                ) {
                    Text("Estás aquí", Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall)
                }
            }
            val lugarInfo = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
            if (lugarInfo != null) VentanaLugar(
                mapa, lugarInfo, Modifier.matchParentSize())
            MaplibreLogo(Modifier.align(Alignment.BottomStart).padding(4.dp).width(64.dp))
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                shape = RoundedCornerShape(12.dp),
                color = colores.surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    IconButton(
                        onClick = { alcance.launch {
                            mapa.animateCamera(CameraUpdate(
                                zoom = (mapa.cameraPosition.zoom + 1.0).coerceIn(0.0, 22.0)))
                        } },
                        modifier = Modifier.size(48.dp).semantics { contentDescription = "Acercar mapa" }
                    ) { Text("+", fontSize = 24.sp) }
                    IconButton(
                        onClick = { alcance.launch {
                            mapa.animateCamera(CameraUpdate(
                                zoom = (mapa.cameraPosition.zoom - 1.0).coerceIn(0.0, 22.0)))
                        } },
                        modifier = Modifier.size(48.dp).semantics { contentDescription = "Alejar mapa" }
                    ) { Text("−", fontSize = 24.sp) }
                }
            }
        }
        Surface(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(alturaAtribucion),
            color = colores.surface) {
            Row(Modifier.padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("OpenFreeMap © OpenMapTiles Data from OpenStreetMap",
                    Modifier.weight(1f), fontSize = 10.sp, lineHeight = 12.sp,
                    color = colores.onSurfaceVariant)
                IconButton(onClick = { mostrarAtribucion = true },
                    modifier = Modifier.size(48.dp).semantics { contentDescription = "Información del mapa" }) {
                    IconoCampus(IconoCampus.Info)
                }
            }
        }
    }
    if (mostrarAtribucion) {
        val uri = LocalUriHandler.current
        val creditos = mapa.style.attributions()
        AlertDialog(
            onDismissRequest = { mostrarAtribucion = false },
            title = { Text("Información del mapa") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = { uri.openUri("https://maplibre.org/") }) { Text("MapLibre") }
                    TextButton(onClick = { uri.openUri("https://openfreemap.org/") }) { Text("OpenFreeMap") }
                    creditos.forEach { html ->
                        Text(AnnotatedString.fromHtml(html), style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarAtribucion = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun MarcadorMapa(color: Color, seleccionado: Boolean, modifier: Modifier = Modifier) {
    val anillo = MaterialTheme.colorScheme.primary
    Canvas(modifier.size(32.dp)) {
        if (seleccionado) drawCircle(anillo, 14.dp.toPx(), style = Stroke(2.dp.toPx()))
        drawCircle(Color.White, 10.dp.toPx())
        drawCircle(color, 7.dp.toPx())
    }
}

/** Posición de presentación; el cálculo de distancias y la selección siguen en el ViewModel. */
@Composable
private fun VentanaLugar(mapa: MapState, lugar: Lugar, modifier: Modifier) {
    val viewport = mapa.viewport
    val punto = viewport?.let {
        mapa.screenLocationFromPosition(Position(latitude = lugar.latitud, longitude = lugar.longitud))
    }
    Layout(
        modifier = modifier,
        content = {
            Surface(
                shape = RoundedCornerShape(12.dp), shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = {}) }
            ) {
                Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(lugar.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(lugar.categoria, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    ) { medibles, limites ->
        val margen = 8.dp.roundToPx()
        val reservaZoom = 60.dp.roundToPx()
        val ancho = limites.maxWidth
        val alto = limites.maxHeight
        val maxVentana = minOf(220.dp.roundToPx(), (ancho - reservaZoom - margen * 2).coerceAtLeast(1))
        val ventana = medibles.single().measure(Constraints(maxWidth = maxVentana,
            maxHeight = (alto - margen * 2).coerceAtLeast(1)))
        layout(ancho, alto) {
            if (punto != null) {
                val px = punto.x.toPx()
                val py = punto.y.toPx()
                if (px in 0f..ancho.toFloat() && py in 0f..alto.toFloat()) {
                    val x = (px - ventana.width / 2).roundToInt()
                        .coerceIn(margen.coerceAtMost((ancho - ventana.width).coerceAtLeast(0)),
                            (ancho - reservaZoom - ventana.width - margen).coerceAtLeast(margen))
                    val separacion = 24.dp.roundToPx()
                    val arriba = py.roundToInt() - separacion - ventana.height
                    val abajo = py.roundToInt() + separacion
                    val cabeAbajo = abajo + ventana.height <= alto - margen
                    // Si ninguna posición cabe con margen, usar el lado con más espacio.
                    // Forzar abajo y recortarlo podía terminar cubriendo el propio marcador.
                    val usarArriba = arriba >= margen ||
                        (!cabeAbajo && py >= alto / 2f)
                    val preferido = if (usarArriba) arriba else abajo
                    val y = preferido.coerceIn(0, (alto - ventana.height - margen).coerceAtLeast(0))
                    ventana.place(x, y)
                }
            }
        }
    }
}
