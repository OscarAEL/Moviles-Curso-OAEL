package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.campusgps.model.Lugar
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.ui.theme.CampusMono
import com.eneque.campusgps.util.formatearDistancia
import java.util.Locale

data class EstadoPantallaCampus(
    val ubicacion: UbicacionUi = UbicacionUi.Inicial,
    val lugares: List<LugarConDistancia> = emptyList(),
    val categorias: List<String> = emptyList(),
    val categoria: String? = null,
    val radios: List<Int> = listOf(100, 200, 500),
    val radio: Int = 100,
    val seleccionado: Int? = null,
    val masCercano: Lugar? = null,
    val dentroDelRadio: Set<Int> = emptySet()
)

/** Presentación sin repositorios ni permisos: sirve también para previews y pruebas de UI. */
@Composable
fun ContenidoCampus(
    estado: EstadoPantallaCampus,
    onBuscar: () -> Unit,
    onReintentar: () -> Unit,
    onAbrirAjustes: () -> Unit,
    onCategoria: (String?) -> Unit,
    onRadio: (Int) -> Unit,
    onSeleccionar: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    mapa: @Composable (Modifier) -> Unit
) {
    val scroll = rememberLazyListState()
    BoxWithConstraints(modifier.fillMaxSize()) {
        val dosColumnas = maxWidth >= 600.dp && maxWidth > maxHeight
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!dosColumnas) BarraMasCercano(
                    estado, onSeleccionar,
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars))
            }
        ) { insets ->
            BoxWithConstraints(Modifier.padding(insets).fillMaxSize()) {
                val alturaMapa = (maxHeight * 0.36f).coerceIn(168.dp, 240.dp)
                Row(
                    Modifier.fillMaxSize().testTag(if (dosColumnas) "campus-horizontal" else "campus-vertical"),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (dosColumnas) {
                        mapa(Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                            .weight(1.05f).fillMaxHeight().testTag("mapa-campus"))
                    }
                    Column(Modifier.weight(1f).fillMaxHeight()) {
                        LazyColumn(
                            state = scroll,
                            modifier = Modifier.weight(1f).fillMaxWidth().testTag("contenido-campus"),
                            contentPadding = PaddingValues(
                                start = if (dosColumnas) 0.dp else 12.dp,
                                end = 12.dp, top = 12.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item(key = "cabecera") { CabeceraCampus(estado.ubicacion, onBuscar) }
                            val error = estado.ubicacion as? UbicacionUi.Error
                            if (error != null) item(key = "error") {
                                MensajeError(error, onReintentar, onAbrirAjustes)
                            }
                            item(key = "radio") {
                                SelectorRadio(estado.radios, estado.radio, onRadio, Modifier.fillMaxWidth())
                            }
                            if (!dosColumnas) item(key = "mapa") {
                                mapa(Modifier.fillMaxWidth().height(alturaMapa).testTag("mapa-campus"))
                            }
                            item(key = "categorias") {
                                FiltrosCategorias(estado.categorias, estado.categoria, onCategoria,
                                    Modifier.fillMaxWidth())
                            }
                            item(key = "titulo-lugares") {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text("Lugares", style = MaterialTheme.typography.titleSmall)
                                    Text(if (estado.lugares.any { it.metros != null }) "Por distancia" else "Campus",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            items(estado.lugares, key = { "lugar-${it.lugar.id}" }) { item ->
                                TarjetaLugar(
                                    item = item,
                                    seleccionado = item.lugar.id == estado.seleccionado,
                                    dentroDelRadio = item.lugar.id in estado.dentroDelRadio,
                                    masCercano = item.lugar.id == estado.masCercano?.id,
                                    onClick = { onSeleccionar(item.lugar.id) },
                                    modifier = Modifier.testTag("tarjeta-${item.lugar.id}")
                                )
                            }
                        }
                        if (dosColumnas) BarraMasCercano(estado, onSeleccionar,
                            Modifier.padding(end = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CabeceraCampus(estado: UbicacionUi, onBuscar: () -> Unit) {
    val buscando = estado == UbicacionUi.Buscando
    val posicion = (estado as? UbicacionUi.Lista)?.posicion
    @Composable
    fun Datos() {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("CampusGPS", style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary)
            if (buscando) Text("Buscando tu ubicación…", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            else {
                Text("Lat ${posicion?.let { String.format(Locale.ROOT, "%.6f", it.latitud) } ?: "—"}",
                    fontFamily = CampusMono, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Lon ${posicion?.let { String.format(Locale.ROOT, "%.6f", it.longitud) } ?: "—"}",
                    fontFamily = CampusMono, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    @Composable
    fun Ubicar(modifier: Modifier = Modifier) {
        Button(
            enabled = !buscando, onClick = onBuscar, modifier = modifier.heightIn(min = 52.dp),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (buscando) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else IconoCampus(IconoCampus.Ubicacion)
            Spacer(Modifier.width(8.dp))
            Text(if (buscando) "Buscando…" else "Obtener mi\nubicación",
                style = MaterialTheme.typography.labelLarge)
        }
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        if (maxWidth >= 320.dp && LocalDensity.current.fontScale < 1.3f) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Datos()
                Ubicar(Modifier.widthIn(max = 152.dp))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Datos()
                Ubicar()
            }
        }
    }
}

@Composable
private fun BarraMasCercano(
    estado: EstadoPantallaCampus,
    onSeleccionar: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val lugar = estado.masCercano
    val metros = estado.lugares.firstOrNull { it.lugar.id == lugar?.id }?.metros
    Surface(
        modifier.fillMaxWidth().testTag("barra-cercano"),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        BoxWithConstraints(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            val compacto = maxWidth >= 300.dp && LocalDensity.current.fontScale < 1.3f
            @Composable
            fun Resumen(modifier: Modifier = Modifier) {
                Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Más cercano", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(lugar?.nombre ?: "Sin calcular", style = MaterialTheme.typography.titleSmall)
                    if (metros != null) Text(formatearDistancia(metros), fontFamily = CampusMono,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
            @Composable
            fun Accion(modifier: Modifier = Modifier) {
                Button(
                    enabled = lugar != null, onClick = { lugar?.let { onSeleccionar(it.id) } },
                    modifier = modifier.heightIn(min = 48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    IconoCampus(IconoCampus.Ruta, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Ir al más cercano", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            if (compacto) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Resumen(Modifier.weight(1f))
                Accion()
            } else Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Resumen()
                Accion(Modifier.fillMaxWidth())
            }
        }
    }
}
