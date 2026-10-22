package com.tecsup.lavacampus

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.*
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Before
import org.junit.After
import java.io.File
import java.time.LocalDate
import java.util.UUID

abstract class MejorasTestBase {
    protected val instrumentacion = InstrumentationRegistry.getInstrumentation()
    protected val context get() = instrumentacion.targetContext
    protected lateinit var db: LavaDatabase
    protected lateinit var repo: LavaRepositoryRoom
    protected lateinit var prefs: PreferenciasStore
    protected lateinit var scope: CoroutineScope
    private lateinit var archivo: File
    private val almacen = ViewModelStore()
    private val owner = object : ViewModelStoreOwner { override val viewModelStore = almacen }
    protected val alumno = Usuario(1, "Ana", "U001", "ana@correo.test", "1234", "CLIENTE")
    protected val lavado = Servicio(1, "Lavado y secado", "", "KG", 8.0, 24, true, "wash")
    protected val planchado = Servicio(2, "Planchado", "", "PRENDA", 2.5, 48, true, "iron")
    protected fun <T> io(block: suspend () -> T): T = runBlocking { withTimeout(15000) { block() } }
    protected fun main(block: () -> Unit) = instrumentacion.runOnMainSync(block)
    protected fun <T : ViewModel> vm(tipo: Class<T>, crear: () -> T): T {
        lateinit var resultado: T
        main {
            resultado = ViewModelProvider(owner, object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <V : ViewModel> create(modelClass: Class<V>): V = crear() as V
            })[tipo]
        }
        return resultado
    }
    protected fun observar(vararg flujos: Flow<*>) { flujos.forEach { f -> scope.launch { f.collect() } } }
    protected fun <T> esperar(flujo: Flow<T>, condicion: (T) -> Boolean): T = io { flujo.first(condicion) }
    protected fun pedido(estado: String, fecha: String = LocalDate.now().toString(), pagado: Boolean = false): Int = io {
        db.pedidoDao().insertar(Pedido(usuarioId = 1, fechaRegistro = LocalDate.now().toString(),
            fechaEntrega = fecha, estado = estado, total = 16.0, pagado = pagado)).toInt()
    }
    @Before fun prepararMejoras() {
        db = Room.inMemoryDatabaseBuilder(context, LavaDatabase::class.java).build()
        repo = LavaRepositoryRoom(db)
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        archivo = File(context.cacheDir, "mejoras-${UUID.randomUUID()}.preferences_pb")
        prefs = PreferenciasStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { archivo }))
        io {
            db.usuarioDao().insertarTodos(listOf(alumno, Usuario(2, "Otro", "U002", "otro@test", "1234", "CLIENTE")))
            db.servicioDao().insertarTodos(listOf(lavado, planchado))
        }
    }
    @After fun limpiarMejoras() {
        main { almacen.clear() }
        io { scope.coroutineContext[Job]!!.cancelAndJoin() }
        db.close()
        archivo.delete()
    }
}
