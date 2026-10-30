package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.ui.administrador.*
import com.tecsup.lavacampus.ui.components.PerfilAjustesScreen
import com.tecsup.lavacampus.ui.estudiante.*
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.*
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Recorrido visual con base y preferencias aisladas; no modifica las cuentas del dispositivo. */
@RunWith(AndroidJUnit4::class)
class RedisenoPantallasTest : MejorasTestBase() {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun ambosPerfilesConservanAccionesYPreferencias() {
        val admin = Usuario(3, "Administración", "", "admin@lavacampus.test", "admin", "ADMIN")
        val fecha = LocalDate.now().plusDays(2).toString()
        val recibido: Int
        val listo: Int
        io {
            db.usuarioDao().insertarTodos(listOf(admin))
            db.servicioDao()
                .insertarTodos(
                    listOf(Servicio(3, "Lavado en seco", "", "PRENDA", 12.0, 48, true, "shirt"))
                )
        }
        recibido = io {
            repo.crearPedido(1, listOf(LineaPedido(1, 3.5), LineaPedido(2, 5.0)), fecha, "").toInt()
        }
        listo = io { repo.crearPedido(1, listOf(LineaPedido(1, 2.0)), fecha, "").toInt() }
        io { db.pedidoDao().actualizarEstado(listo, "LISTO", "C-05") }
        val entregado = io { repo.crearPedido(1, listOf(LineaPedido(2, 6.0)), fecha, "").toInt() }
        io {
            db.pedidoDao().actualizarEstado(entregado, "ENTREGADO", null)
            repo.marcarPagado(entregado, true)
        }
        val cancelado = io { repo.crearPedido(1, listOf(LineaPedido(3, 2.0)), fecha, "").toInt() }
        io {
            db.pedidoDao().actualizarEstado(cancelado, "CANCELADO", null)
            prefs.guardarModoOscuro(true)
        }
        val inicio = vm(InicioViewModel::class.java) { InicioViewModel(repo, alumno) }
        val nuevo =
            vm(NuevoPedidoViewModel::class.java) { NuevoPedidoViewModel(repo, prefs, alumno) }
        val pedidos = vm(MisPedidosViewModel::class.java) { MisPedidosViewModel(repo, 1) }
        val detalle =
            vm(DetallePedidoViewModel::class.java) { DetallePedidoViewModel(repo, listo, 1) }
        val tarifas = vm(TarifasViewModel::class.java) { TarifasViewModel(repo) }
        val sesion =
            vm(SesionViewModel::class.java) {
                SesionViewModel(repo, prefs) { Result.success(Unit) }
            }
        val preferencias =
            vm(PreferenciasViewModel::class.java) { PreferenciasViewModel(repo, prefs, 1) }
        val panel = vm(PanelViewModel::class.java) { PanelViewModel(repo) }
        val bandeja = vm(BandejaViewModel::class.java) { BandejaViewModel(repo, null) }
        val gestion =
            vm(GestionPedidoViewModel::class.java) { GestionPedidoViewModel(repo, recibido) }
        val servicios = vm(ServiciosViewModel::class.java) { ServiciosViewModel(repo) }
        val clientes = vm(ClientesViewModel::class.java) { ClientesViewModel(repo) }
        val vista = mutableIntStateOf(0)
        esperar(sesion.restaurandoSesion) { !it }
        compose.runOnUiThread {
            compose.activity.setContent {
                LavaTheme {
                    when (vista.intValue) {
                        0 -> SplashScreen({})
                        1 -> LoginScreen(sesion)
                        2 -> RegistroScreen(sesion, {})
                        3 -> InicioScreen(inicio, {}, {}, {}, {}, {})
                        4 -> NuevoPedidoScreen(nuevo, {}, {})
                        5 -> MisPedidosScreen(pedidos, {}, {}, {}, {}, {}, {})
                        6 -> DetallePedidoScreen(detalle, {})
                        7 -> TarifasScreen(tarifas, {}, {}, {}, {})
                        8 -> PerfilAjustesScreen(alumno, preferencias, sesion, {}, {})
                        9 -> PanelScreen(panel, {}, {}, {}, {}, {})
                        10 -> BandejaScreen(bandeja, onGestionarPedido = {})
                        11 -> GestionPedidoScreen(gestion, {})
                        12 -> ServiciosScreen(servicios, {}, {})
                        13 ->
                            FormularioServicioScreen(
                                servicios,
                                onBackClick = {},
                                onGuardadoExito = {},
                            )
                        14 ->
                            FormularioServicioScreen(
                                servicios,
                                servicioId = 1,
                                onBackClick = {},
                                onGuardadoExito = {},
                            )
                        15 -> ClientesScreen(clientes, {}, {}, {}, {}, {})
                        16 -> PerfilAjustesScreen(admin, preferencias, sesion, {}, {})
                    }
                }
            }
        }
        captura("01-bienvenida")
        mostrar(vista, 1)
        compose.onNodeWithText("Bienvenido").assertIsDisplayed()
        captura("02-login-estudiante")
        compose.onNodeWithText("Administrador").performClick()
        compose.onNodeWithText("¿No tienes cuenta? Regístrate").assertDoesNotExist()
        captura("03-login-administrador")
        mostrar(vista, 2)
        compose.onNodeWithText("Crea tu cuenta").assertIsDisplayed()
        captura("04-registro")
        mostrar(vista, 3)
        compose.waitUntil(15000) { inicio.recientes.value.isNotEmpty() }
        captura("05-inicio")
        mostrar(vista, 4)
        compose.waitUntil(15000) { nuevo.serviciosActivos.value.size == 3 }
        compose.onNodeWithText("Confirmar pedido").assertIsNotEnabled()
        compose.runOnIdle {
            nuevo.cambiarCantidad(1, 1.0)
            repeat(10) { nuevo.cambiarCantidad(1, .5) }
            repeat(5) { nuevo.cambiarCantidad(2, 1.0) }
        }
        compose.waitUntil(15000) { nuevo.totalCalculado.value == 55.7 }
        compose
            .onNodeWithText("Descuento por volumen\n10% en lavado")
            .performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithText("Confirmar pedido").assertIsEnabled()
        captura("06-nuevo-pedido")
        mostrar(vista, 5)
        compose.waitUntil(15000) { pedidos.activos.value.size == 2 }
        captura("07-pedidos-activos")
        compose.onNodeWithText("Historial").performClick()
        compose.onAllNodesWithText("Pedir de nuevo").assertCountEquals(2)
        captura("08-historial")
        mostrar(vista, 6)
        compose.waitUntil(15000) { detalle.pedido.value != null }
        compose.onNodeWithText("C-05").assertIsDisplayed()
        captura("09-detalle")
        compose.onNodeWithText("Cancelar pedido").performScrollTo().assertIsNotEnabled()
        mostrar(vista, 7)
        compose.waitUntil(15000) { tarifas.serviciosActivos.value.size == 3 }
        captura("10-tarifas")
        mostrar(vista, 8)
        compose.onAllNodes(isToggleable())[0].assertIsNotEnabled().assertIsOff()
        captura("11-perfil")
        assertTrue(io { prefs.modoOscuro.first() })
        mostrar(vista, 9)
        compose.waitUntil(15000) { panel.porEstado.value.sumOf { it.valor } == 3.0 }
        captura("12-panel")
        mostrar(vista, 10)
        compose.waitUntil(15000) { bandeja.pedidos.value.size == 4 }
        captura("13-bandeja")
        mostrar(vista, 11)
        compose.waitUntil(15000) {
            gestion.pedido.value != null && "C-05" in gestion.casillerosOcupados.value
        }
        compose
            .onNodeWithContentDescription("Casillero C-05")
            .assertIsNotEnabled()
            .assertHasNoClickAction()
        compose.onNodeWithContentDescription("Casillero C-06").performScrollTo().performClick()
        compose.waitUntil(10000) { gestion.casilleroElegido.value == "C-06" }
        captura("14-gestion")
        mostrar(vista, 12)
        compose.waitUntil(15000) { servicios.servicios.value.size == 3 }
        captura("15-servicios")
        mostrar(vista, 13)
        compose.onNodeWithText("Nuevo servicio").assertIsDisplayed()
        captura("16-nuevo-servicio")
        mostrar(vista, 14)
        compose.onNodeWithText("Editar servicio").assertIsDisplayed()
        captura("17-editar-servicio")
        mostrar(vista, 15)
        compose.waitUntil(15000) { clientes.clientes.value.isNotEmpty() }
        captura("18-clientes")
        mostrar(vista, 16)
        compose.onAllNodes(isToggleable())[0].assertIsNotEnabled().assertIsOff()
        captura("19-ajustes")
        assertEquals(4, io { repo.observarPedidosDeUsuario(1).first().size })
        compose.runOnIdle { vista.intValue = -1 }
    }

    private fun mostrar(vista: MutableIntState, valor: Int) {
        compose.runOnIdle { vista.intValue = valor }
        compose.waitForIdle()
    }

    private fun captura(nombre: String) {
        val folder = File(context.cacheDir, "rediseno").apply { mkdirs() }
        File(folder, "$nombre.png").outputStream().use {
            compose
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
                .compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
