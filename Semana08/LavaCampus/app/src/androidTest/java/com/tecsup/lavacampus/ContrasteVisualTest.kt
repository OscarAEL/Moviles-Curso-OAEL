package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.ui.theme.LocalModoOscuro
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContrasteVisualTest : MejorasTestBase() {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun temaClaroPermaneceAunqueExistaPreferenciaOscura() {
        val nuevo =
            vm(NuevoPedidoViewModel::class.java) { NuevoPedidoViewModel(repo, prefs, alumno) }
        val oscuro = mutableStateOf(false)
        var fondo = Color.Unspecified
        compose.runOnUiThread {
            compose.activity.setContent {
                CompositionLocalProvider(LocalModoOscuro provides oscuro.value) {
                    LavaTheme {
                        fondo = MaterialTheme.colorScheme.background
                        NuevoPedidoScreen(nuevo, {}, {})
                    }
                }
            }
        }
        compose.waitUntil(15000) { nuevo.serviciosActivos.value.size == 2 }
        compose.runOnIdle { nuevo.cambiarCantidad(1, 1.0) }
        compose.waitUntil(15000) { nuevo.totalCalculado.value == 8.0 }
        compose.onNodeWithText("Resumen").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Confirmar pedido").assertIsDisplayed().assertIsEnabled()
        assertEquals(Color(0xFFF4FBFC), fondo)
        compose.runOnIdle { oscuro.value = true }
        compose.waitForIdle()
        assertEquals(Color(0xFFF4FBFC), fondo)
        compose.onNodeWithText("Confirmar pedido").assertIsDisplayed()
    }
}
