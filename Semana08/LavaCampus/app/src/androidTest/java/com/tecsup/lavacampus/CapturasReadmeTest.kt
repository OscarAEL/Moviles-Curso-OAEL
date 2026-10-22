package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import com.tecsup.lavacampus.ui.administrador.*
import com.tecsup.lavacampus.ui.estudiante.*
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.ui.theme.LocalModoOscuro
import com.tecsup.lavacampus.viewmodel.*
import kotlinx.coroutines.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Seis pantallas reales con la siembra del laboratorio en una BD aislada. */
@RunWith(AndroidJUnit4::class)
class CapturasReadmeTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun seisPantallasYControlesDeAceptacion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, LavaDatabase::class.java).build()
        val repo = LavaRepositoryRoom(db)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val archivo = File(context.cacheDir, "capturas-${UUID.randomUUID()}.preferences_pb")
        val prefs = PreferenciasStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { archivo }))
        val store = ViewModelStore()
        val owner = object : ViewModelStoreOwner { override val viewModelStore = store }
        val vista = mutableIntStateOf(-1)
        try {
            val alumno = runBlocking { withTimeout(15000) {
                DatosIniciales.sembrar(db)
                repo.obtenerUsuarioPorRol("CLIENTE")!!
            } }
            lateinit var inicio: InicioViewModel
            lateinit var nuevo: NuevoPedidoViewModel
            lateinit var detalle: DetallePedidoViewModel
            lateinit var panel: PanelViewModel
            lateinit var bandeja: BandejaViewModel
            lateinit var gestion: GestionPedidoViewModel
            compose.runOnUiThread {
                inicio = ViewModelProvider(owner, InicioViewModel.Factory(repo, alumno))[InicioViewModel::class.java]
                nuevo = ViewModelProvider(owner, NuevoPedidoViewModel.Factory(repo, prefs, alumno))[NuevoPedidoViewModel::class.java]
                detalle = ViewModelProvider(owner, DetallePedidoViewModel.Factory(repo, 4, alumno.id))[DetallePedidoViewModel::class.java]
                panel = ViewModelProvider(owner, PanelViewModel.Factory(repo))[PanelViewModel::class.java]
                bandeja = ViewModelProvider(owner, BandejaViewModel.Factory(repo, "EN_PROCESO"))[BandejaViewModel::class.java]
                gestion = ViewModelProvider(owner, GestionPedidoViewModel.Factory(repo, 7))[GestionPedidoViewModel::class.java]
                compose.activity.setContent {
                    CompositionLocalProvider(LocalModoOscuro provides false) {
                        LavaTheme {
                            when (vista.intValue) {
                                0 -> InicioScreen(inicio, {}, {}, {}, {}, {})
                                1 -> NuevoPedidoScreen(nuevo, {}, {})
                                2 -> DetallePedidoScreen(detalle, {})
                                3 -> PanelScreen(panel, {}, {}, {}, {}, {})
                                4 -> BandejaScreen(bandeja, filtroInicialStr = "EN_PROCESO", onGestionarPedido = {})
                                5 -> GestionPedidoScreen(gestion, {})
                            }
                        }
                    }
                }
            }
            compose.runOnIdle { vista.intValue = 0 }
            compose.waitUntil(15000) { inicio.pedidoActivo.value != null }
            compose.onNodeWithText("Hola, Ana").assertIsDisplayed()
            captura(context.cacheDir, "estudiante-inicio.png")

            compose.runOnIdle { vista.intValue = 1 }
            compose.waitUntil(15000) { nuevo.serviciosActivos.value.size == 3 }
            compose.onNodeWithText("Confirmar pedido").performScrollTo().assertIsNotEnabled()
            compose.runOnIdle {
                val lavado = nuevo.serviciosActivos.value.single { it.unidad == "KG" }
                val planchado = nuevo.serviciosActivos.value.single { it.nombre == "Planchado" }
                nuevo.cambiarCantidad(lavado.id, 1.0)
                repeat(5) { nuevo.cambiarCantidad(lavado.id, 0.5) }
                repeat(5) { nuevo.cambiarCantidad(planchado.id, 1.0) }
            }
            compose.waitUntil(15000) { nuevo.totalCalculado.value == 40.5 }
            compose.onNodeWithText("Confirmar pedido").performScrollTo().assertIsEnabled()
            compose.onNodeWithText("S/ 40.50").assertIsDisplayed()
            captura(context.cacheDir, "estudiante-nuevo-pedido.png")

            compose.runOnIdle { vista.intValue = 2 }
            compose.waitUntil(15000) { detalle.pedido.value != null }
            compose.onNodeWithText("Seguimiento").assertIsDisplayed()
            captura(context.cacheDir, "estudiante-detalle-pedido.png")

            compose.runOnIdle { vista.intValue = 3 }
            compose.waitUntil(15000) { panel.porEstado.value.sumOf { it.valor } == 9.0 }
            compose.onNodeWithText("Panel de la lavandería").assertIsDisplayed()
            captura(context.cacheDir, "administrador-panel.png")

            compose.runOnIdle { vista.intValue = 4 }
            compose.waitUntil(15000) { bandeja.pedidos.value.size == 2 }
            compose.onNodeWithText("Pedido #007").assertIsDisplayed()
            captura(context.cacheDir, "administrador-bandeja.png")

            compose.runOnIdle { vista.intValue = 5 }
            compose.waitUntil(15000) { "C-05" in gestion.casillerosOcupados.value }
            compose.onNodeWithText("C-05").assertHasNoClickAction()
            compose.runOnIdle { gestion.elegirCasillero("C-06") }
            compose.waitForIdle()
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(4)
            compose.onNodeWithText("C-06").assertIsDisplayed()
            captura(context.cacheDir, "administrador-gestion-pedido.png")
        } finally {
            compose.runOnUiThread { vista.intValue = -1; store.clear() }
            compose.waitForIdle()
            runBlocking { scope.coroutineContext[Job]!!.cancelAndJoin() }
            db.close()
            archivo.delete()
        }
    }

    private fun captura(cache: File, nombre: String) {
        val carpeta = File(cache, "readme").apply { mkdirs() }
        File(carpeta, nombre).outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
