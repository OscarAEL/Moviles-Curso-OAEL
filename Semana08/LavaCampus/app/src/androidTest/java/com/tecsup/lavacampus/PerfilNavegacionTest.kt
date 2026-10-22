package com.tecsup.lavacampus

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PerfilNavegacionTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test
    fun cambiarPerfilReiniciaNavegacionYPreferenciasPersisten() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val app = context.applicationContext as LavaApp
        io { app.esperarInicializacion().getOrThrow() }
        val store = app.preferenciasStore
        val sesionAnterior = io { store.sesionGuardada.first() }
        val oscuroAnterior = io { store.modoOscuro.first() }
        val avisosAnteriores = io { store.notificaciones.first() }
        val recibirAnterior = io { store.recibirPedidos.first() }
        val clienteAnterior = io { store.ultimoUsuario(Rol.CLIENTE).first() }
        val adminAnterior = io { store.ultimoUsuario(Rol.ADMIN).first() }
        val alumno = io {
            app.repository.obtenerUsuario(clienteAnterior)?.takeIf { it.rol == "CLIENTE" }
                ?: app.repository.obtenerUsuarioPorRol("CLIENTE")!!
        }
        var actividad: ActivityScenario<MainActivity>? = null
        try {
            io {
                store.guardarSesion(Rol.CLIENTE, alumno.id)
                store.guardarModoOscuro(false)
                store.guardarRecibirPedidos(true)
            }
            actividad = ActivityScenario.launch(MainActivity::class.java)
            esperarTexto("Perfil")
            compose.onNodeWithText("Perfil").performClick()
            compose.onNodeWithText(alumno.nombre).assertIsDisplayed()
            compose.onNodeWithText("Pedidos totales").assertIsDisplayed()
            captura(File(context.cacheDir, "pruebas"), "perfil-claro.png")
            compose.onAllNodes(isToggleable())[0].performClick()
            compose.waitUntil(10000) { runBlocking { store.modoOscuro.first() } }
            compose.onAllNodes(isToggleable())[0].assertIsOn()
            actividad.close()
            actividad = ActivityScenario.launch(MainActivity::class.java)
            esperarTexto("Perfil")
            compose.onNodeWithText("Perfil").performClick()
            compose.onAllNodes(isToggleable())[0].assertIsOn()
            captura(File(context.cacheDir, "pruebas"), "perfil-oscuro.png")
            compose.onNodeWithText("Cambiar de perfil").performClick()
            esperarTexto("Panel de la lavandería")
            compose.onNodeWithText("Panel de la lavandería").assertIsDisplayed()
            compose.onNodeWithText("Ajustes").performClick()
            compose.onAllNodes(isToggleable())[0].assertIsOn()
            compose.onAllNodes(isToggleable())[2].performClick()
            compose.waitUntil(10000) { runBlocking { !store.recibirPedidos.first() } }
            captura(File(context.cacheDir, "pruebas"), "ajustes-oscuro.png")
            compose.onNodeWithText("Cambiar de perfil").performClick()
            esperarTexto("Nuevo pedido")
            compose.onNodeWithText("Nuevo pedido").assertIsDisplayed()
            assertEquals(alumno.id, io { store.sesionGuardada.first().usuarioId })
            compose.onNodeWithText("Nuevo pedido").performClick()
            esperarTexto("La lavandería no está recibiendo pedidos")
            compose.onNodeWithText("La lavandería no está recibiendo pedidos").assertIsDisplayed()
        } finally {
            actividad?.close()
            io {
                store.guardarModoOscuro(oscuroAnterior)
                store.guardarNotificaciones(avisosAnteriores)
                store.guardarRecibirPedidos(recibirAnterior)
                if (clienteAnterior > 0) store.guardarSesion(Rol.CLIENTE, clienteAnterior)
                if (adminAnterior > 0) store.guardarSesion(Rol.ADMIN, adminAnterior)
                if (sesionAnterior.usuarioId > 0) store.guardarSesion(Rol.valueOf(sesionAnterior.perfil), sesionAnterior.usuarioId)
                else store.cerrarSesion()
            }
        }
    }

    private fun <T> io(block: suspend () -> T): T = runBlocking { withTimeout(15000) { block() } }

    private fun esperarTexto(texto: String) {
        compose.waitUntil(10000) { compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty() }
    }

    private fun captura(directorio: File?, nombre: String) {
        if (directorio == null) return
        directorio.mkdirs()
        compose.onRoot().captureToImage().asAndroidBitmap().let { bitmap ->
            File(directorio, nombre).outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
