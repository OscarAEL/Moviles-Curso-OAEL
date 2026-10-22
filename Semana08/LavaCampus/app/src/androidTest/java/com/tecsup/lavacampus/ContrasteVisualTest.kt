package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.ui.theme.LocalModoOscuro
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/** Revisión visual de ambas pantallas; no confirma, cancela ni modifica pedidos o preferencias. */
@RunWith(AndroidJUnit4::class)
class ContrasteVisualTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun avisoYResumenEnClaroYOscuro() {
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as LavaApp
        val alumno = runBlocking {
            withTimeout(15000) {
                app.esperarInicializacion().getOrThrow()
                app.repository.obtenerUsuarioPorRol("CLIENTE")!!
            }
        }
        val oscuro = mutableStateOf(false)
        val nuevo = mutableStateOf(false)
        lateinit var pedidosVm: MisPedidosViewModel
        lateinit var nuevoVm: NuevoPedidoViewModel
        compose.runOnUiThread {
            pedidosVm = ViewModelProvider(compose.activity, MisPedidosViewModel.Factory(app.repository, alumno.id))[MisPedidosViewModel::class.java]
            nuevoVm = ViewModelProvider(compose.activity, NuevoPedidoViewModel.Factory(app.repository, app.preferenciasStore, alumno))[NuevoPedidoViewModel::class.java]
            compose.activity.setContent {
                CompositionLocalProvider(LocalModoOscuro provides oscuro.value) {
                    LavaTheme {
                        if (nuevo.value) {
                            NuevoPedidoScreen(nuevoVm, onBackClick = {}, onPedidoCreadoExito = {})
                        } else {
                            MisPedidosScreen(pedidosVm, onBackClick = {}, onNuevoPedidoClick = {}, onInicioClick = {},
                                onTarifasClick = {}, onPerfilClick = {}, onPedidoClick = {})
                        }
                    }
                }
            }
        }
        var cantidadesSeleccionadas = false
        for (modoOscuro in listOf(false, true)) {
            val modo = if (modoOscuro) "oscuro" else "claro"
            compose.runOnIdle { oscuro.value = modoOscuro; nuevo.value = false }
            compose.waitUntil(15000) { compose.onAllNodesWithText("Aviso").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Aviso").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Tu pedido #", substring = true).assertIsDisplayed()
            captura(app, "aviso-$modo.png")
            compose.runOnIdle { nuevo.value = true }
            compose.waitUntil(15000) { nuevoVm.serviciosActivos.value.isNotEmpty() }
            if (!cantidadesSeleccionadas) {
                compose.runOnIdle { nuevoVm.serviciosActivos.value.forEach { nuevoVm.cambiarCantidad(it.id, 1.0) } }
                cantidadesSeleccionadas = true
            }
            compose.waitUntil(15000) { compose.onAllNodesWithText("Total").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Total").performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("Resumen").assertIsDisplayed()
            captura(app, "resumen-$modo.png")
        }
    }

    private fun captura(app: LavaApp, nombre: String) {
        val carpeta = File(app.cacheDir, "contraste").apply { mkdirs() }
        File(carpeta, nombre).outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
