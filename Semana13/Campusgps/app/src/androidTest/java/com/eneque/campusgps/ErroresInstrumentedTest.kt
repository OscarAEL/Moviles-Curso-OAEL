package com.eneque.campusgps

import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.PlayServicesNoDisponibleException
import com.eneque.campusgps.data.UbicacionNoDisponibleException
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.model.TipoError
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.viewmodel.MapaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErroresInstrumentedTest {
    @Test
    fun sinPlayServicesNoConsultaUbicacionYPermiteRecuperarse() = conViewModel { repo, vm ->
        repo.disponible = false
        vm.buscar()
        assertEquals(TipoError.SIN_PLAY_SERVICES, (vm.estado.value as UbicacionUi.Error).tipo)
        assertEquals(0, repo.consultas)
        assertTrue(vm.lugares.value.all { it.metros == null })

        repo.disponible = true
        vm.buscar()
        assertTrue(vm.estado.value is UbicacionUi.Lista)
        assertEquals(1, repo.consultas)
    }

    @Test
    fun permisoDenegadoNoConsultaUbicacion() = conViewModel { repo, vm ->
        vm.permisoDenegado()
        assertEquals(TipoError.SIN_PERMISO, (vm.estado.value as UbicacionUi.Error).tipo)
        assertEquals(0, repo.consultas)
    }

    @Test
    fun clasificaFallosYReintentaSinPerderDistancias() = conViewModel { repo, vm ->
        vm.buscar()
        val anteriores = vm.lugares.value
        val casos = listOf(
            SecurityException() to TipoError.SIN_PERMISO,
            UbicacionNoDisponibleException() to TipoError.GPS_APAGADO,
            PlayServicesNoDisponibleException() to TipoError.SIN_PLAY_SERVICES,
            IllegalStateException("Detalle técnico") to TipoError.OTRO
        )
        casos.forEach { (fallo, tipo) ->
            repo.respuesta = Result.failure(fallo)
            vm.buscar()
            assertEquals(tipo, (vm.estado.value as UbicacionUi.Error).tipo)
            assertEquals(anteriores, vm.lugares.value)
        }

        repo.respuesta = Result.success(Posicion(-12.0440, -77.0430))
        vm.buscar()
        assertTrue(vm.estado.value is UbicacionUi.Lista)
        assertEquals(5, vm.lugares.value.first().lugar.id)
    }

    private fun conViewModel(prueba: (RepositorioPrueba, MapaViewModel) -> Unit) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val repo = RepositorioPrueba()
            val almacen = ViewModelStore()
            val vm = MapaViewModel(repo, LugaresRepositoryLocal())
            almacen.put("mapa", vm)
            try {
                prueba(repo, vm)
            } finally {
                almacen.clear()
            }
        }
    }

    // Respuestas controladas solo en androidTest; no modifica permisos ni GPS del emulador.
    private class RepositorioPrueba : UbicacionRepository {
        var disponible = true
        var consultas = 0
        var respuesta: Result<Posicion> = Result.success(Posicion(-12.0464, -77.0428))

        override fun playServicesDisponible(): Boolean = disponible

        override suspend fun obtenerPosicion(): Result<Posicion> {
            consultas++
            return respuesta
        }
    }
}
