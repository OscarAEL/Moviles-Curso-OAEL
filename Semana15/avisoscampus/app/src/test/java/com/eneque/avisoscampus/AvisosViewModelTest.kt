package com.eneque.avisoscampus

import com.eneque.avisoscampus.data.AvisosRepositoryMemoria
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import com.eneque.avisoscampus.viewmodel.AvisosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AvisosViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun preparar() { Dispatchers.setMain(dispatcher) }
    @After fun terminar() { Dispatchers.resetMain() }

    @Test fun recordatorioCuentaDiezSegundosYNoSeDuplica() = runTest(dispatcher) {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        val eventos = mutableListOf<Aviso>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.eventos.collect { eventos.add(it) } }
        vm.recordarEn()
        runCurrent()
        assertEquals(10, vm.faltan.value)
        vm.recordarEn()
        advanceTimeBy(9000)
        runCurrent()
        assertEquals(1, vm.faltan.value)
        assertTrue(vm.avisos.value.isEmpty())
        advanceTimeBy(1000)
        runCurrent()
        assertNull(vm.faltan.value)
        assertEquals(1, eventos.size)
        assertEquals(Origen.RECORDATORIO, eventos.single().origen)
        assertEquals(1, vm.avisos.value.size)
    }

    @Test fun cancelarNoRegistraNiEmiteYPermiteReiniciar() = runTest(dispatcher) {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        val eventos = mutableListOf<Aviso>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.eventos.collect { eventos.add(it) } }
        vm.recordarEn()
        advanceTimeBy(5000)
        vm.cancelarRecordatorio()
        runCurrent()
        assertNull(vm.faltan.value)
        advanceTimeBy(10000)
        assertTrue(vm.avisos.value.isEmpty())
        assertTrue(eventos.isEmpty())
        vm.recordarEn(1)
        advanceUntilIdle()
        assertEquals(1, vm.avisos.value.size)
    }

    @Test fun permisoDenegadoConservaHistorialYSeLimpiaAlConceder() = runTest(dispatcher) {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        vm.permisoRespondido(false)
        vm.avisarAhora()
        runCurrent()
        vm.noSePudoMostrar()
        assertEquals(1, vm.avisos.value.size)
        assertNotNull(vm.mensaje.value)
        vm.permisoRespondido(true)
        assertNull(vm.mensaje.value)
    }

    @Test fun modoAvionSoloRegistraCuandoSeEscucha() = runTest(dispatcher) {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        vm.alCambiarModoAvion(true)
        runCurrent()
        assertTrue(vm.avisos.value.isEmpty())
        vm.cambiarEscucha(true)
        vm.alCambiarModoAvion(true)
        vm.alCambiarModoAvion(false)
        runCurrent()
        assertEquals(listOf("Desactivado", "Activado"), vm.avisos.value.map { it.texto })
        vm.cambiarEscucha(false)
        vm.alCambiarModoAvion(true)
        runCurrent()
        assertEquals(2, vm.avisos.value.size)
    }

    @Test fun falloDeGuardadoNoEmiteNiSeConfundeConUnPermiso() = runTest(dispatcher) {
        val repo = object : com.eneque.avisoscampus.data.AvisosRepository {
            override val avisos = kotlinx.coroutines.flow.MutableStateFlow<List<Aviso>>(emptyList())
            override suspend fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
                throw java.io.IOException("Fallo simulado")
            }
        }
        val vm = AvisosViewModel(repo)
        val eventos = mutableListOf<Aviso>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.eventos.collect { eventos.add(it) } }
        vm.avisarAhora()
        runCurrent()
        assertTrue(eventos.isEmpty())
        assertTrue(vm.avisos.value.isEmpty())
        assertEquals("No se pudo guardar el aviso. Inténtalo de nuevo.", vm.mensaje.value)
        assertFalse(vm.necesitaAjustes.value)
        vm.permisoRespondido(true)
        assertNotNull(vm.mensaje.value)
    }

    @Test fun botonGeneraIdsDistintosYOrdenRecientePrimero() = runTest(dispatcher) {
        val vm = AvisosViewModel(AvisosRepositoryMemoria())
        vm.avisarAhora()
        vm.avisarAhora()
        runCurrent()
        assertEquals(listOf(2, 1), vm.avisos.value.map { it.id })
        assertTrue(vm.avisos.value.all { it.origen == Origen.BOTON })
    }
}
