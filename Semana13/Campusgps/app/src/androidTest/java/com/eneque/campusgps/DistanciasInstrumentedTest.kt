package com.eneque.campusgps

import android.util.Log
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.campusgps.data.LugaresRepository
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.model.Lugar
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.util.distanciaMetros
import com.eneque.campusgps.viewmodel.MapaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DistanciasInstrumentedTest {
    @Test
    fun distanciaCeroYReferenciaGeodesicaEnElEcuador() {
        val origen = Posicion(0.0, 0.0)
        val mismoPunto = Lugar(1, "Origen", "Prueba", 0.0, 0.0)
        val unGradoEste = Lugar(2, "Este", "Prueba", 0.0, 1.0)

        assertEquals(0f, distanciaMetros(origen, mismoPunto), 0.001f)
        // Un grado de longitud sobre el ecuador WGS84 mide 111319.49 m.
        assertEquals(111319.49f, distanciaMetros(origen, unGradoEste), 1f)
    }

    @Test
    fun recalculaYOrdenaSoloTrasObtenerUnaPosicionCorrecta() {
        // Datos controlados solo en androidTest; no se consulta Fused Location.
        val base = Posicion(-12.0464, -77.0428)
        val estacionamiento = Posicion(-12.0440, -77.0430)
        var respuesta: Result<Posicion> = Result.success(base)
        val ubicacionRepo = object : UbicacionRepository {
            override suspend fun obtenerPosicion(): Result<Posicion> = respuesta
        }
        var lecturas = 0
        val lugaresRepo = object : LugaresRepository {
            override fun obtenerLugares(): List<Lugar> {
                lecturas++
                // Entrada invertida para verificar que el ViewModel realmente ordena.
                return LugaresRepositoryLocal().obtenerLugares().reversed()
            }
        }

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val almacen = ViewModelStore()
            val vm = MapaViewModel(ubicacionRepo, lugaresRepo)
            almacen.put("mapa", vm)
            try {
                assertTrue(vm.lugares.value.all { it.metros == null })
                assertEquals(listOf(5, 4, 3, 2, 1), vm.lugares.value.map { it.lugar.id })

                // La respuesta de este fake no suspende: buscar termina en Main.immediate.
                vm.buscar()
                assertEquals(UbicacionUi.Lista(base), vm.estado.value)
                val primeraBusqueda = vm.lugares.value
                assertEquals(listOf(1, 2, 3, 4, 5), primeraBusqueda.map { it.lugar.id })
                val metrosEsperados = listOf(88f, 110f, 173f, 220f, 266f)
                primeraBusqueda.zip(metrosEsperados).forEach { (item, esperado) ->
                    assertEquals(esperado, requireNotNull(item.metros), 1f)
                }
                assertEquals(2, lecturas)
                Log.i("DistanciasPaso5", "Base: ${primeraBusqueda.map { it.lugar.id to it.metros }}")

                respuesta = Result.failure(IllegalStateException("Fallo controlado"))
                vm.buscar()
                assertTrue(vm.estado.value is UbicacionUi.Error)
                assertEquals(primeraBusqueda, vm.lugares.value)
                assertEquals(2, lecturas)

                respuesta = Result.success(estacionamiento)
                vm.buscar()
                assertEquals(UbicacionUi.Lista(estacionamiento), vm.estado.value)
                assertEquals(listOf(5, 1, 3, 2, 4), vm.lugares.value.map { it.lugar.id })
                assertEquals(0f, requireNotNull(vm.lugares.value.first().metros), 0.001f)
                assertEquals(3, lecturas)
                Log.i("DistanciasPaso5", "Estacionamiento: ${vm.lugares.value.map { it.lugar.id to it.metros }}")
            } finally {
                almacen.clear()
            }
        }
    }
}
