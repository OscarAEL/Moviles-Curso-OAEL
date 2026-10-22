package com.tecsup.lavacampus

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Usa una BD en memoria y un archivo de preferencias independiente de la app. */
@RunWith(AndroidJUnit4::class)
class AdministracionPersistenciaTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun editarTarifaConservaImportesDelPedidoYDesactivarLaOculta() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, LavaDatabase::class.java).build()
        try {
            val repo = LavaRepositoryRoom(db)
            db.usuarioDao().insertarTodos(listOf(Usuario(1, "Estudiante", "U001", "test@example.test", "test", "CLIENTE")))
            val servicio = Servicio(1, "Lavado", "", "KG", 8.0, 24, true, "wash")
            db.servicioDao().insertar(servicio)
            val id = repo.crearPedido(1, listOf(LineaPedido(1, 2.0)), "2026-10-22", "").toInt()
            repo.guardarServicio(servicio.copy(precio = 12.0))
            val antiguo = withTimeout(5000) { repo.observarPedido(id).first { it != null } }!!
            assertEquals(16.0, antiguo.pedido.total, 0.001)
            assertEquals(8.0, antiguo.detalles.single().detalle.precioUnitario, 0.001)
            assertEquals(12.0, antiguo.detalles.single().servicio.precio, 0.001)
            assertTrue(repo.servicioTieneDetalles(1))
            repo.cambiarActivo(1, false)
            assertTrue(withTimeout(5000) { repo.observarServiciosActivos().first() }.isEmpty())
            assertEquals(1, withTimeout(5000) { repo.observarServicios().first() }.size)
            var rechazo = false
            try { repo.eliminarServicio(servicio) } catch (_: android.database.sqlite.SQLiteConstraintException) { rechazo = true }
            assertTrue("No se debe borrar un servicio con historial", rechazo)
            assertNotNull(repo.observarPedido(id).first())
        } finally { db.close() }
    }

    @Test
    fun servicioSinPedidosSePuedeCrearEditarYEliminar() = runBlocking {
        val db = Room.inMemoryDatabaseBuilder(context, LavaDatabase::class.java).build()
        try {
            val repo = LavaRepositoryRoom(db)
            repo.guardarServicio(Servicio(nombre = "Nuevo", descripcion = "", unidad = "UNIDAD", precio = 20.0, horasEntrega = 48, icono = "shirt"))
            val creado = repo.observarServicios().first().single()
            assertTrue(creado.id > 0)
            repo.guardarServicio(creado.copy(nombre = "Editado", precio = 25.0))
            assertEquals("Editado", repo.observarServicios().first().single().nombre)
            assertFalse(repo.servicioTieneDetalles(creado.id))
            repo.eliminarServicio(creado)
            assertTrue(repo.observarServicios().first().isEmpty())
        } finally { db.close() }
    }

    @Test
    fun preferenciasPersistenAlReabrirYSalirNoBorraAjustes() = runBlocking {
        val archivo = File(context.cacheDir, "preferencias-test-${UUID.randomUUID()}.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var store = PreferenciasStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { archivo }))
            store.guardarSesion(Rol.CLIENTE, 3)
            store.guardarSesion(Rol.ADMIN, 1)
            store.guardarModoOscuro(true)
            store.guardarNotificaciones(false)
            store.guardarRecibirPedidos(false)
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            store = PreferenciasStore(PreferenceDataStoreFactory.create(scope = scope, produceFile = { archivo }))
            assertTrue(store.modoOscuro.first())
            assertFalse(store.notificaciones.first())
            assertFalse(store.recibirPedidos.first())
            assertEquals(1, store.sesionGuardada.first().usuarioId)
            assertEquals(3, store.ultimoUsuario(Rol.CLIENTE).first())
            store.cerrarSesion()
            assertEquals(0, store.sesionGuardada.first().usuarioId)
            assertTrue(store.modoOscuro.first())
            assertFalse(store.recibirPedidos.first())
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
            archivo.delete()
        }
    }
}
