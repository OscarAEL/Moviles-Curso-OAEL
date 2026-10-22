package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.ui.administrador.BandejaScreen
import com.tecsup.lavacampus.ui.estudiante.*
import com.tecsup.lavacampus.ui.theme.*
import com.tecsup.lavacampus.viewmodel.*
import kotlinx.coroutines.flow.first
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MejorasVisualTest : MejorasTestBase() {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()
    @Test fun atrasadoHistorialYResumenEnModoOscuro() {
        val id = io { repo.crearPedido(1, listOf(LineaPedido(1, 6.0)), LocalDate.now().minusDays(1).toString(), "").toInt() }
        val bandeja = vm(BandejaViewModel::class.java) { BandejaViewModel(repo, null) }
        val historial = vm(MisPedidosViewModel::class.java) { MisPedidosViewModel(repo, 1) }
        val vista = mutableIntStateOf(0)
        var nuevo: NuevoPedidoViewModel? = null
        compose.runOnUiThread {
            compose.activity.setContent {
                CompositionLocalProvider(LocalModoOscuro provides true) {
                    LavaTheme {
                        when (vista.intValue) {
                            0 -> BandejaScreen(bandeja, onGestionarPedido = {})
                            1 -> MisPedidosScreen(historial, {}, {}, {}, {}, {}, {}, pestanaInicial = 1,
                                onRepetirPedido = { vista.intValue = 2 })
                            2 -> NuevoPedidoScreen(nuevo!!, {}, {})
                        }
                    }
                }
            }
        }
        compose.waitUntil(15000) { bandeja.pedidosAtrasados.value.contains(id) }
        compose.onNodeWithText("Atrasado · entrega", substring = true).assertIsDisplayed()
        captura("ia-atrasados.png")
        io { db.pedidoDao().actualizarEstado(id, "ENTREGADO", null); repo.guardarServicio(lavado.copy(precio = 10.0)) }
        nuevo = vm(NuevoPedidoViewModel::class.java) { NuevoPedidoViewModel(repo, prefs, alumno, id) }
        compose.runOnIdle { vista.intValue = 1 }
        compose.waitUntil(15000) { historial.historial.value.isNotEmpty() }
        compose.onNodeWithText("Pedir de nuevo").assertIsDisplayed()
        captura("ia-historial.png")
        compose.onNodeWithText("Pedir de nuevo").performClick()
        compose.waitUntil(15000) { nuevo!!.totalCalculado.value == 54.0 }
        compose.onNodeWithText("Confirmar pedido").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Descuento por volumen\n10% en lavado").assertIsDisplayed()
        compose.onNodeWithText("S/ 54.00").assertIsDisplayed()
        captura("ia-descuento-repeticion.png")
        assertEquals(1, io { repo.observarPedidosDeUsuario(1).first().size })
        compose.runOnIdle { vista.intValue = -1 }
    }
    private fun captura(nombre: String) {
        val carpeta = File(context.cacheDir, "mejoras").apply { mkdirs() }
        File(carpeta, nombre).outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
