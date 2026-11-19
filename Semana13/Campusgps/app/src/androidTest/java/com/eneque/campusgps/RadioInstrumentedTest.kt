package com.eneque.campusgps

import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.model.Lugar
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.util.distanciaMetros
import com.eneque.campusgps.viewmodel.MapaViewModel
import kotlin.math.abs
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RadioInstrumentedTest {
    @Test
    fun selectorNoSolicitaUbicacionNiAdmiteRadiosDesconocidos() = conViewModel { vm, repo ->
        assertEquals(listOf(100, 200, 500), vm.radiosDisponibles)
        assertEquals(100, vm.radioMetros.value)
        enPrincipal { vm.cambiarRadio(200) }
        assertEquals(200, vm.radioMetros.value)
        enPrincipal { vm.cambiarRadio(150) }
        assertEquals(200, vm.radioMetros.value)
        enPrincipal { vm.cambiarRadio(500) }
        assertEquals(500, vm.radioMetros.value)
        assertTrue(vm.contornoRadio.value.isEmpty())
        assertTrue(vm.lugaresDentroDelRadio.value.isEmpty())
        assertEquals(0, repo.consultas)
    }

    @Test
    fun marcaLugaresSinOcultarlosYRespetaLaCategoria() = conViewModel { vm, repo ->
        enPrincipal { vm.buscar() }
        esperar(vm.lugaresDentroDelRadio) { it == setOf(1) }
        enPrincipal { vm.cambiarRadio(200) }
        esperar(vm.lugaresDentroDelRadio) { it == setOf(1, 2, 3) }
        enPrincipal { vm.cambiarRadio(500) }
        esperar(vm.lugaresDentroDelRadio) { it == setOf(1, 2, 3, 4, 5) }
        assertEquals(5, vm.lugaresFiltrados.value.size)
        assertEquals(1, repo.consultas)

        enPrincipal {
            vm.filtrarPorCategoria("Comida")
            vm.cambiarRadio(100)
        }
        esperar(vm.lugaresFiltrados) { it.map { item -> item.lugar.id } == listOf(2) }
        esperar(vm.lugaresDentroDelRadio) { it.isEmpty() }
        // La cafetería sigue visible y puede ser la más cercana de la categoría, aunque esté fuera.
        esperar(vm.masCercano) { it?.id == 2 }
        enPrincipal { vm.cambiarRadio(200) }
        esperar(vm.lugaresDentroDelRadio) { it == setOf(2) }

        enPrincipal {
            repo.posicion = Posicion(-12.0440, -77.0430)
            vm.buscar()
        }
        esperar(vm.lugaresDentroDelRadio) { it.isEmpty() }
        assertEquals("Comida", vm.categoriaSeleccionada.value)
        assertEquals(200, vm.radioMetros.value)
        assertEquals(2, repo.consultas)

        enPrincipal {
            vm.filtrarPorCategoria(null)
            vm.cambiarRadio(100)
        }
        esperar(vm.lugaresDentroDelRadio) { it == setOf(5) }
    }

    @Test
    fun circuloCerradoMideElRadioYSeActualizaConLaUbicacion() = conViewModel { vm, repo ->
        enPrincipal { vm.buscar() }
        for (radio in vm.radiosDisponibles) {
            enPrincipal { vm.cambiarRadio(radio) }
            val contorno = esperar(vm.contornoRadio) { alRadio(it, repo.posicion, radio) }
            assertEquals(contorno.first(), contorno.last())
            contorno.forEach { punto ->
                assertEquals(radio.toFloat(), distancia(repo.posicion, punto), radio * 0.006f)
            }
        }
        assertEquals(1, repo.consultas)

        enPrincipal {
            repo.posicion = Posicion(-12.0440, -77.0430)
            vm.buscar()
        }
        esperar(vm.contornoRadio) { alRadio(it, repo.posicion, 500) }
        enPrincipal { vm.permisoDenegado() }
        esperar(vm.contornoRadio) { it.isEmpty() }
        assertEquals(2, repo.consultas)
    }

    private fun alRadio(contorno: List<Posicion>, centro: Posicion, radio: Int): Boolean =
        contorno.size == 65 && abs(distancia(centro, contorno.first()) - radio) < radio * 0.006f

    private fun distancia(centro: Posicion, punto: Posicion): Float =
        distanciaMetros(centro, Lugar(0, "Contorno", "Prueba", punto.latitud, punto.longitud))

    private fun enPrincipal(accion: () -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(accion)
    }

    private fun <T> esperar(flujo: StateFlow<T>, condicion: (T) -> Boolean): T =
        runBlocking { withTimeout(5000) { flujo.first { condicion(it) } } }

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

    // Posiciones controladas solo en androidTest.
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
