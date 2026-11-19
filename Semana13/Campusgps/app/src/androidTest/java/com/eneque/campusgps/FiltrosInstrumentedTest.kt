package com.eneque.campusgps

import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.viewmodel.MapaViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FiltrosInstrumentedTest {
    @Test
    fun filtraLasCuatroCategoriasSinSolicitarUbicacion() = conViewModel { vm, repo ->
        assertEquals(listOf("Estudio", "Comida", "Eventos", "Servicios"), vm.categorias)
        val casos = listOf(
            "Estudio" to listOf(1, 3),
            "Comida" to listOf(2),
            "Eventos" to listOf(4),
            "Servicios" to listOf(5),
            null to listOf(1, 2, 3, 4, 5)
        )
        casos.forEach { (categoria, ids) ->
            enPrincipal { vm.filtrarPorCategoria(categoria) }
            val visibles = esperar(vm.lugaresFiltrados) { lista ->
                lista.map { it.lugar.id } == ids
            }
            assertTrue(visibles.all { it.metros == null })
        }
        assertEquals(0, repo.consultas)
        assertEquals(5, vm.lugares.value.size)
        assertNull(vm.masCercano.value)
    }

    @Test
    fun conservaOrdenYActualizaElMasCercanoDelFiltro() = conViewModel { vm, repo ->
        enPrincipal {
            vm.filtrarPorCategoria("Estudio")
            vm.buscar()
        }
        esperar(vm.lugaresFiltrados) { lista ->
            lista.map { it.lugar.id } == listOf(1, 3) && lista.all { it.metros != null }
        }
        esperar(vm.masCercano) { it?.id == 1 }

        enPrincipal {
            repo.posicion = Posicion(-12.0455, -77.0415)
            vm.buscar()
        }
        val recalculados = esperar(vm.lugaresFiltrados) { lista ->
            lista.map { it.lugar.id } == listOf(3, 1)
        }
        assertEquals(0f, requireNotNull(recalculados.first().metros), 0.001f)
        esperar(vm.masCercano) { it?.id == 3 }
        assertEquals("Estudio", vm.categoriaSeleccionada.value)

        enPrincipal { vm.filtrarPorCategoria("Comida") }
        esperar(vm.lugaresFiltrados) { lista -> lista.map { it.lugar.id } == listOf(2) }
        esperar(vm.masCercano) { it?.id == 2 }

        enPrincipal { vm.filtrarPorCategoria(null) }
        val todos = esperar(vm.lugaresFiltrados) { it.size == 5 }
        assertTrue(todos.all { it.metros != null })
        assertEquals(3, todos.first().lugar.id)
        assertEquals(2, repo.consultas)
    }

    @Test
    fun limpiaSeleccionOcultaYRechazaCategoriasDesconocidas() = conViewModel { vm, repo ->
        enPrincipal {
            vm.seleccionar(1)
            vm.filtrarPorCategoria("Estudio")
        }
        assertEquals(1, vm.seleccionado.value)
        enPrincipal { vm.filtrarPorCategoria("Comida") }
        assertNull(vm.seleccionado.value)
        enPrincipal {
            vm.seleccionar(2)
            vm.filtrarPorCategoria(null)
            vm.filtrarPorCategoria("No existe")
        }
        assertEquals(2, vm.seleccionado.value)
        assertNull(vm.categoriaSeleccionada.value)
        assertEquals(0, repo.consultas)
    }

    private fun enPrincipal(accion: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(accion)
    }

    private fun <T> esperar(flujo: StateFlow<T>, condicion: (T) -> Boolean): T =
        runBlocking {
            withTimeout(5000) { flujo.first { condicion(it) } }
        }

    private fun conViewModel(prueba: (MapaViewModel, UbicacionPrueba) -> Unit) {
        val almacen = ViewModelStore()
        val repo = UbicacionPrueba()
        lateinit var vm: MapaViewModel
        enPrincipal {
            vm = MapaViewModel(repo, LugaresRepositoryLocal())
            almacen.put("mapa", vm)
        }
        try {
            prueba(vm, repo)
        } finally {
            enPrincipal { almacen.clear() }
        }
    }

    // La simulación solo existe en androidTest.
    private class UbicacionPrueba : UbicacionRepository {
        var posicion = Posicion(-12.0464, -77.0428)
        var consultas = 0

        override fun playServicesDisponible(): Boolean = true

        override suspend fun obtenerPosicion(): Result<Posicion> {
            consultas++
            return Result.success(posicion)
        }
    }
}
