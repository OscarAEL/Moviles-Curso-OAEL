package com.tecsup.lavacampus

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek
import java.util.UUID

/** Casos del Paso 14. BD y preferencias propias: no modifican la instalación del estudiante. */
@RunWith(AndroidJUnit4::class)
class AceptacionLaboratorioTest {
    private val instrumentacion = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentacion.targetContext
    private lateinit var db: LavaDatabase
    private lateinit var repo: LavaRepositoryRoom
    private lateinit var prefs: PreferenciasStore
    private lateinit var scope: CoroutineScope
    private lateinit var archivo: File
    private val storeVm = ViewModelStore()
    private val propietario = object : ViewModelStoreOwner { override val viewModelStore = storeVm }
    private val alumno = Usuario(1, "Ana García", "U202310234", "ana@correo.test", "1234", "CLIENTE")
    private val lavado = Servicio(1, "Lavado y secado", "", "KG", 8.0, 24, true, "wash")
    private val planchado = Servicio(2, "Planchado", "", "PRENDA", 2.5, 48, true, "iron")

    private fun <T> io(block: suspend () -> T): T = runBlocking { withTimeout(15000) { block() } }
    private fun main(block: () -> Unit) = instrumentacion.runOnMainSync(block)
    private inline fun <reified T : ViewModel> vm(crossinline crear: () -> T): T {
        lateinit var resultado: T
        main {
            resultado = ViewModelProvider(propietario, object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <V : ViewModel> create(modelClass: Class<V>): V = crear() as V
            })[T::class.java]
        }
        return resultado
    }
    private fun observar(vararg flujos: Flow<*>) {
        flujos.forEach { flujo -> scope.launch { flujo.collect() } }
    }
    private fun <T> esperar(flujo: Flow<T>, condicion: (T) -> Boolean): T = io { flujo.first(condicion) }
    private fun pedido(estado: String = "RECIBIDO", casillero: String? = null, pagado: Boolean = false, total: Double = 16.0, fecha: String = LocalDate.now().toString()): Int = io {
        db.pedidoDao().insertar(Pedido(usuarioId = 1, fechaRegistro = fecha,
            fechaEntrega = LocalDate.now().plusDays(2).toString(), estado = estado,
            total = total, casillero = casillero, pagado = pagado)).toInt()
    }

    @Before fun preparar() {
        db = Room.inMemoryDatabaseBuilder(context, LavaDatabase::class.java).build()
        repo = LavaRepositoryRoom(db)
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        archivo = File(context.cacheDir, "aceptacion-${UUID.randomUUID()}.preferences_pb")
        prefs = PreferenciasStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { archivo }))
        io {
            db.usuarioDao().insertarTodos(listOf(alumno, Usuario(2, "Administración", "", "admin@lavacampus.test", "admin", "ADMIN")))
            db.servicioDao().insertarTodos(listOf(lavado, planchado))
        }
    }
    @After fun limpiar() {
        main { storeVm.clear() }
        io { scope.coroutineContext[Job]!!.cancelAndJoin() }
        db.close()
        archivo.delete()
    }

    @Test fun claveIncorrectaNoIniciaSesion() {
        val sesion = vm { SesionViewModel(repo, prefs) { Result.success(Unit) } }
        esperar(sesion.restaurandoSesion) { !it }
        main { sesion.login(alumno.correo, "clave-incorrecta", Rol.CLIENTE) }
        esperar(sesion.error) { it != null }
        assertNull(sesion.usuarioActual.value)
        assertEquals(0, io { prefs.sesionGuardada.first().usuarioId })
    }

    private fun nuevo(): NuevoPedidoViewModel {
        val nuevo = vm { NuevoPedidoViewModel(repo, prefs, alumno) }
        observar(nuevo.serviciosActivos, nuevo.lineasResumen, nuevo.totalCalculado, nuevo.fechaEstimadaIso, nuevo.recibirPedidos)
        esperar(nuevo.serviciosActivos) { it.size == 2 }
        esperar(nuevo.recibirPedidos) { it == true }
        return nuevo
    }
    private fun seleccionar(nuevo: NuevoPedidoViewModel) {
        main {
            nuevo.cambiarCantidad(1, 1.0)
            repeat(5) { nuevo.cambiarCantidad(1, 0.5) }
            repeat(5) { nuevo.cambiarCantidad(2, 1.0) }
        }
    }

    @Test fun mezclaDeServiciosTotalYFechaConSaltoDeDomingo() {
        val nuevo = nuevo()
        seleccionar(nuevo)
        esperar(nuevo.totalCalculado) { it == 40.5 }
        val fecha = esperar(nuevo.fechaEstimadaIso) { it != null }!!
        assertEquals(FechaUtils.calcularFechaEntrega(LocalDate.now(), 48), fecha)
        assertEquals("2026-10-26", FechaUtils.calcularFechaEntrega(LocalDate.of(2026, 10, 23), 48))
        assertEquals("2026-10-23", FechaUtils.calcularFechaEntrega(LocalDate.of(2026, 10, 21), 48))
    }

    @Test fun cuartoPedidoActivoSeRechazaSinInsertarlo() {
        repeat(3) { pedido() }
        val nuevo = nuevo()
        seleccionar(nuevo)
        main { nuevo.confirmar() }
        esperar(nuevo.error) { it?.contains("3 pedidos activos") == true }
        assertEquals(3, io { repo.contarActivos(1) })
        assertFalse(nuevo.pedidoCreadoExito.value)
    }

    @Test fun estudianteNoCancelaEnProcesoYObservaCambioEnVivo() {
        val id = pedido("EN_PROCESO")
        val detalle = vm { DetallePedidoViewModel(repo, id, 1) }
        observar(detalle.puedeCancelar)
        esperar(detalle.pedido) { it?.pedido?.estado == "EN_PROCESO" }
        assertFalse(detalle.puedeCancelar.value)
        main { detalle.cancelar() }
        assertEquals("EN_PROCESO", io { db.pedidoDao().obtener(id)!!.estado })
        io { repo.cambiarEstado(id, EstadoPedido.LISTO, "C-06") }
        esperar(detalle.pedido) { it?.pedido?.estado == "LISTO" && it.pedido.casillero == "C-06" }
        assertEquals("C-06", detalle.pedido.value!!.pedido.casillero)
    }

    @Test fun listoRequiereCasilleroYNoAceptaUnoOcupado() {
        pedido("LISTO", "C-05")
        val id = pedido("EN_PROCESO")
        val gestion = vm { GestionPedidoViewModel(repo, id) }
        observar(gestion.pedido, gestion.casillerosOcupados)
        esperar(gestion.pedido) { it != null }
        esperar(gestion.casillerosOcupados) { "C-05" in it }
        main { gestion.avanzar() }
        esperar(gestion.error) { it?.contains("seleccionar un casillero") == true }
        esperar(gestion.operando) { !it }
        assertEquals("EN_PROCESO", io { db.pedidoDao().obtener(id)!!.estado })
        main { gestion.limpiarError(); gestion.elegirCasillero("C-05") }
        esperar(gestion.error) { it?.contains("ocupado") == true }
        assertNull(gestion.casilleroElegido.value)
        main { gestion.elegirCasillero("C-06"); gestion.avanzar() }
        esperar(gestion.pedido) { it?.pedido?.estado == "LISTO" }
        assertEquals("C-06", io { db.pedidoDao().obtener(id)!!.casillero })
    }

    @Test fun entregaRechazaSinCobroYPermiteAlCobrar() {
        val id = pedido("LISTO", "C-06")
        val gestion = vm { GestionPedidoViewModel(repo, id) }
        observar(gestion.pedido)
        esperar(gestion.pedido) { it != null }
        main { gestion.avanzar() }
        esperar(gestion.error) { it?.contains("sin cobrar") == true }
        esperar(gestion.operando) { !it }
        assertEquals("LISTO", io { db.pedidoDao().obtener(id)!!.estado })
        main { gestion.marcarPagado(true) }
        esperar(gestion.pedido) { it?.pedido?.pagado == true }
        esperar(gestion.operando) { !it }
        main { gestion.avanzar() }
        esperar(gestion.pedido) { it?.pedido?.estado == "ENTREGADO" }
    }

    @Test fun ingresosSemanaSonLaSumaDePedidosPagados() {
        pedido(pagado = true, total = 40.5)
        pedido(pagado = true, total = 16.0)
        pedido(pagado = false, total = 100.0)
        val lunes = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        pedido(pagado = true, total = 500.0, fecha = lunes.minusDays(1).toString())
        val panel = vm { PanelViewModel(repo) }
        observar(panel.ingresosSemana)
        esperar(panel.ingresosSemana) { it == 56.5 }
        assertEquals(56.5, panel.ingresosSemana.value, 0.001)
    }

    @Test fun servicioUsadoNoSeEliminaYMuestraMensaje() {
        io { repo.crearPedido(1, listOf(LineaPedido(1, 2.0)), LocalDate.now().plusDays(1).toString(), "") }
        val servicios = vm { ServiciosViewModel(repo) }
        main { servicios.eliminar(lavado) }
        esperar(servicios.error) { it?.contains("pedidos asociados") == true }
        assertNotNull(io { db.servicioDao().obtener(1) })
        assertFalse(servicios.exitoEliminado.value)
    }

    @Test fun pedidosPerfilYOscuroPersistenAlCerrarYReabrir() {
        val nombre = "aceptacion-${UUID.randomUUID()}.db"
        var persistente = Room.databaseBuilder(context, LavaDatabase::class.java, nombre).build()
        try {
            io {
                persistente.usuarioDao().insertarTodos(listOf(alumno))
                persistente.pedidoDao().insertar(Pedido(usuarioId = 1, fechaRegistro = "2026-10-21", fechaEntrega = "2026-10-23", estado = "RECIBIDO", total = 40.5))
                prefs.guardarSesion(Rol.CLIENTE, 1)
                prefs.guardarModoOscuro(true)
            }
            persistente.close()
            persistente = Room.databaseBuilder(context, LavaDatabase::class.java, nombre).build()
            val sesion = vm { SesionViewModel(LavaRepositoryRoom(persistente), prefs) { Result.success(Unit) } }
            esperar(sesion.restaurandoSesion) { !it }
            assertEquals(1, sesion.usuarioActual.value!!.id)
            assertEquals("CLIENTE", sesion.usuarioActual.value!!.rol)
            observar(sesion.modoOscuro)
            esperar(sesion.modoOscuro) { it }
            assertEquals(40.5, io { persistente.pedidoDao().obtener(1)!!.total }, 0.001)
        } finally { persistente.close(); context.deleteDatabase(nombre) }
    }
}
