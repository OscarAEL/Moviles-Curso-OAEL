package com.eneque.campusgps

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.SeleccionRepository
import com.eneque.campusgps.data.SeleccionRepositoryDataStore
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.viewmodel.MapaViewModel
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeleccionInstrumentedTest {
    @Test
    fun dataStoreRecuperaSeleccionAlRecrearAlmacenYViewModelSinConsultarGps() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val archivo = File(contexto.cacheDir, "seleccion-${UUID.randomUUID()}.preferences_pb")
        val primerTrabajo = SupervisorJob()
        val segundoTrabajo = SupervisorJob()
        val primerAlmacen = ViewModelStore()
        val segundoAlmacen = ViewModelStore()
        val ubicacion = UbicacionPrueba()
        try {
            val primero = SeleccionRepositoryDataStore(
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(Dispatchers.IO + primerTrabajo),
                    produceFile = { archivo }
                )
            )
            assertNull(esperarValor(primero.ultimoLugarId) { true })
            val vm = crearVm(primero, ubicacion, primerAlmacen)
            enPrincipal { vm.seleccionar(2) }
            esperarValor(primero.ultimoLugarId) { it == 2 }
            enPrincipal { primerAlmacen.clear() }
            runBlocking { primerTrabajo.cancelAndJoin() }

            // Nueva instancia: obliga a leer el archivo, no el caché de la primera.
            val segundo = SeleccionRepositoryDataStore(
                PreferenceDataStoreFactory.create(
                    scope = CoroutineScope(Dispatchers.IO + segundoTrabajo),
                    produceFile = { archivo }
                )
            )
            val restaurado = crearVm(segundo, ubicacion, segundoAlmacen)
            esperarValor(restaurado.seleccionado) { it == 2 }
            assertEquals(UbicacionUi.Inicial, restaurado.estado.value)
            assertTrue(restaurado.lugares.value.all { it.metros == null })
            assertNull(restaurado.categoriaSeleccionada.value)
            assertEquals(0, ubicacion.consultas)
        } finally {
            enPrincipal {
                primerAlmacen.clear()
                segundoAlmacen.clear()
            }
            runBlocking {
                primerTrabajo.cancelAndJoin()
                segundoTrabajo.cancelAndJoin()
            }
            archivo.delete()
        }
    }

    @Test
    fun ausenciaOIdDesconocidoNoRestauraUnaSeleccion() {
        listOf(null, 999).forEach { id ->
            val repo = SeleccionPrueba(id)
            conVm(repo) { vm, ubicacion ->
                esperarLectura(repo)
                assertNull(vm.seleccionado.value)
                assertEquals(0, ubicacion.consultas)
                assertTrue(repo.escrituras.value.isEmpty())
            }
        }
    }

    @Test
    fun lecturaTardiaNoSobrescribeUnaSeleccionNueva() {
        val repo = SeleccionPrueba(1, retrasarLectura = true)
        conVm(repo) { vm, _ ->
            enPrincipal { vm.seleccionar(2) }
            repo.permitirLectura.complete(Unit)
            esperarLectura(repo)
            assertEquals(2, vm.seleccionado.value)
            esperarValor(repo.escrituras) { it == listOf(2) }
        }
    }

    @Test
    fun conservaOrdenDeEscriturasYElFiltroNoBorraLoGuardado() {
        val repo = SeleccionPrueba(null)
        conVm(repo) { vm, ubicacion ->
            esperarLectura(repo)
            enPrincipal {
                vm.seleccionar(3)
                vm.seleccionar(2)
                vm.seleccionar(4)
                vm.filtrarPorCategoria("Comida")
            }
            esperarValor(repo.escrituras) { it.size == 3 }
            assertEquals(listOf(3, 2, 4), repo.escrituras.value)
            assertNull(vm.seleccionado.value)
            assertEquals(4, repo.escrituras.value.last())
            assertEquals(0, ubicacion.consultas)
        }
    }

    @Test
    fun noRestauraLugarOcultoNiGuardaIdsInvalidos() {
        val repo = SeleccionPrueba(1, retrasarLectura = true)
        conVm(repo) { vm, _ ->
            enPrincipal {
                vm.filtrarPorCategoria("Comida")
                vm.seleccionar(999)
                vm.seleccionar(1)
            }
            repo.permitirLectura.complete(Unit)
            esperarLectura(repo)
            assertNull(vm.seleccionado.value)
            assertTrue(repo.escrituras.value.isEmpty())
            enPrincipal { vm.seleccionar(2) }
            esperarValor(repo.escrituras) { it == listOf(2) }
        }
    }

    private fun crearVm(
        repo: SeleccionRepository,
        ubicacion: UbicacionPrueba,
        almacen: ViewModelStore
    ): MapaViewModel {
        lateinit var vm: MapaViewModel
        enPrincipal {
            vm = MapaViewModel(ubicacion, LugaresRepositoryLocal(), repo)
            almacen.put("mapa", vm)
        }
        return vm
    }

    private fun conVm(repo: SeleccionPrueba, prueba: (MapaViewModel, UbicacionPrueba) -> Unit) {
        val almacen = ViewModelStore()
        val ubicacion = UbicacionPrueba()
        val vm = crearVm(repo, ubicacion, almacen)
        try {
            prueba(vm, ubicacion)
        } finally {
            enPrincipal { almacen.clear() }
        }
    }

    private fun esperarLectura(repo: SeleccionPrueba) {
        runBlocking { withTimeout(5000) { repo.lecturaTerminada.await() } }
        enPrincipal { }
    }

    private fun enPrincipal(accion: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(accion)
    }

    private fun <T> esperarValor(flujo: Flow<T>, condicion: (T) -> Boolean): T =
        runBlocking { withTimeout(5000) { flujo.first { condicion(it) } } }

    private class SeleccionPrueba(id: Int?, retrasarLectura: Boolean = false) : SeleccionRepository {
        val permitirLectura = CompletableDeferred<Unit>().apply {
            if (!retrasarLectura) complete(Unit)
        }
        val lecturaTerminada = CompletableDeferred<Unit>()
        val escrituras = MutableStateFlow<List<Int>>(emptyList())
        override val ultimoLugarId: Flow<Int?> = flow {
            permitirLectura.await()
            try {
                emit(id)
            } finally {
                lecturaTerminada.complete(Unit)
            }
        }

        override suspend fun guardarLugar(id: Int) {
            delay(if (id == 3) 50L else 1L)
            escrituras.value = escrituras.value + id
        }
    }

    // Esta prueba de persistencia nunca debe consultar ubicación.
    private class UbicacionPrueba : UbicacionRepository {
        var consultas = 0
        override fun playServicesDisponible(): Boolean = true
        override suspend fun obtenerPosicion(): Result<Posicion> {
            consultas++
            return Result.failure(IllegalStateException("GPS no esperado"))
        }
    }
}
