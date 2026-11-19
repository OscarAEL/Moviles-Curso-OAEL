package com.eneque.campusgps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.eneque.campusgps.model.*
import com.eneque.campusgps.ui.ContenidoCampus
import com.eneque.campusgps.ui.EstadoPantallaCampus
import com.eneque.campusgps.ui.theme.CampusGPSTheme

@Preview(name = "400x400", widthDp = 400, heightDp = 400)
@Preview(name = "400x500", widthDp = 400, heightDp = 500)
@Preview(name = "400x1000", widthDp = 400, heightDp = 1000)
@Preview(name = "610x400", widthDp = 610, heightDp = 400)
@Preview(name = "610x500", widthDp = 610, heightDp = 500)
@Preview(name = "610x1000", widthDp = 610, heightDp = 1000)
@Preview(name = "900x400", widthDp = 900, heightDp = 400)
@Preview(name = "900x500", widthDp = 900, heightDp = 500)
@Preview(name = "900x1000", widthDp = 900, heightDp = 1000)
annotation class TamanosCampus

private val biblioteca = Lugar(1, "Biblioteca Central", "Estudio", -12.0460, -77.0435)
private val lugares = listOf(
    LugarConDistancia(biblioteca, 88f),
    LugarConDistancia(Lugar(2, "Cafetería Campus", "Comida", -12.0470, -77.0420), 109f),
    LugarConDistancia(Lugar(3, "Laboratorio de Cómputo", "Estudio", -12.0455, -77.0415), 172f),
    LugarConDistancia(Lugar(4, "Auditorio Principal", "Eventos", -12.0480, -77.0440), 220f),
    LugarConDistancia(Lugar(5, "Estacionamiento Norte", "Servicios", -12.0440, -77.0430), 266f)
)
private val obtenida = EstadoPantallaCampus(
    ubicacion = UbicacionUi.Lista(Posicion(-12.0464, -77.0428)),
    lugares = lugares, categorias = listOf("Estudio", "Comida", "Eventos", "Servicios"),
    seleccionado = 1, masCercano = biblioteca, dentroDelRadio = setOf(1)
)

// El renderer nativo y la red no forman parte de esta prueba de presentación.
// La carga del mapa, sus marcadores y gestos se comprueban en el emulador.
@Composable
private fun PantallaDePrueba(estado: EstadoPantallaCampus, oscuro: Boolean = false) {
    CampusGPSTheme(darkTheme = oscuro) {
        ContenidoCampus(estado, {}, {}, {}, {}, {}, {}, mapa = { modifier ->
            Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center) { Text("Área del mapa · prueba de presentación") }
        })
    }
}

@PreviewTest
@TamanosCampus
@Composable
fun UbicacionObtenida() = PantallaDePrueba(obtenida)

@PreviewTest
@Preview(name = "Inicio", widthDp = 360, heightDp = 640)
@Composable
fun Inicio() = PantallaDePrueba(obtenida.copy(
    ubicacion = UbicacionUi.Inicial, lugares = lugares.map { it.copy(metros = null) },
    seleccionado = null, masCercano = null, dentroDelRadio = emptySet()))

@PreviewTest
@Preview(name = "Buscando", widthDp = 360, heightDp = 640)
@Composable
fun Buscando() = PantallaDePrueba(obtenida.copy(ubicacion = UbicacionUi.Buscando))

@PreviewTest
@Preview(name = "Comida200", widthDp = 360, heightDp = 640)
@Composable
fun Comida() = PantallaDePrueba(obtenida.copy(
    categoria = "Comida", radio = 200, lugares = listOf(lugares[1]),
    seleccionado = 2, masCercano = lugares[1].lugar, dentroDelRadio = setOf(2)))

@PreviewTest
@Preview(name = "Texto130", widthDp = 360, heightDp = 640, fontScale = 1.3f)
@Preview(name = "Texto150", widthDp = 360, heightDp = 640, fontScale = 1.5f)
@Composable
fun TextoGrande() = PantallaDePrueba(obtenida)

@PreviewTest
@Preview(name = "Oscuro", widthDp = 360, heightDp = 640)
@Composable
fun Oscuro() = PantallaDePrueba(obtenida, oscuro = true)

@PreviewTest
@Preview(name = "PermisoDenegado", widthDp = 360, heightDp = 640)
@Preview(name = "PermisoHorizontal", widthDp = 640, heightDp = 360)
@Composable
fun ErrorPermiso() = PantallaDePrueba(obtenida.copy(
    ubicacion = UbicacionUi.Error("Permite el acceso a la ubicación para calcular distancias.",
        TipoError.SIN_PERMISO)))
