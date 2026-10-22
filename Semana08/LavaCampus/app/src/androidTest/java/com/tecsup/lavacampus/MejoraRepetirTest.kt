package com.tecsup.lavacampus

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MejoraRepetirTest : MejorasTestBase() {
    private fun original(): Int = io {
        val id = repo.crearPedido(1, listOf(LineaPedido(1, 2.0), LineaPedido(2, 3.0)), LocalDate.now().minusDays(7).toString(), "Delicado").toInt()
        db.pedidoDao().actualizarEstado(id, "ENTREGADO", null)
        id
    }
    private fun cargar(id: Int): NuevoPedidoViewModel {
        val modelo = vm(NuevoPedidoViewModel::class.java) { NuevoPedidoViewModel(repo, prefs, alumno, id) }
        observar(modelo.serviciosActivos, modelo.lineasResumen, modelo.totalCalculado, modelo.recibirPedidos, modelo.fechaEstimadaIso)
        esperar(modelo.cargandoRepeticion) { !it }
        return modelo
    }
    @Test fun repiteConPreciosYFechaActualesSinCrearHastaConfirmar() {
        val id = original()
        io { repo.guardarServicio(lavado.copy(precio = 10.0)); repo.guardarServicio(planchado.copy(precio = 3.0)) }
        val modelo = cargar(id)
        esperar(modelo.totalCalculado) { it == 29.0 }
        esperar(modelo.recibirPedidos) { it == true }
        esperar(modelo.fechaEstimadaIso) { it != null }
        assertEquals(mapOf(1 to 2.0, 2 to 3.0), modelo.cantidades.value)
        assertEquals("Delicado", modelo.nota.value)
        assertEquals(1, io { repo.observarPedidosDeUsuario(1).first().size })
        assertTrue(LocalDate.parse(modelo.fechaEstimadaIso.value).isAfter(LocalDate.now()))
        main { modelo.cambiarCantidad(2, 1.0) }
        esperar(modelo.totalCalculado) { it == 32.0 }
        main { modelo.confirmar() }
        esperar(modelo.pedidoCreadoExito) { it }
        val pedidos = io { repo.observarPedidosDeUsuario(1).first() }
        assertEquals(2, pedidos.size)
        assertEquals(23.5, pedidos.first { it.pedido.id == id }.pedido.total, 0.001)
        val nuevo = pedidos.first { it.pedido.id != id }
        assertEquals("RECIBIDO", nuevo.pedido.estado)
        assertEquals(32.0, nuevo.pedido.total, 0.001)
        assertFalse(nuevo.pedido.pagado)
        assertNull(nuevo.pedido.casillero)
    }
    @Test fun omiteInactivosConAvisoYNoCreaAutomaticamente() {
        val id = original()
        io { repo.guardarServicio(planchado.copy(activo = false)) }
        val modelo = cargar(id)
        esperar(modelo.totalCalculado) { it == 16.0 }
        assertEquals(mapOf(1 to 2.0), modelo.cantidades.value)
        assertTrue(modelo.avisoRepeticion.value!!.contains("Se omitieron 1"))
        assertEquals(1, io { repo.observarPedidosDeUsuario(1).first().size })
    }
    @Test fun rechazaPedidosDeOtroUsuario() {
        val id = io { repo.crearPedido(2, listOf(LineaPedido(1, 2.0)), LocalDate.now().toString(), "Privado").toInt() }
        io { db.pedidoDao().actualizarEstado(id, "ENTREGADO", null) }
        val modelo = cargar(id)
        assertTrue(modelo.cantidades.value.isEmpty())
        assertEquals("", modelo.nota.value)
        assertNotNull(modelo.error.value)
    }
    @Test fun repetirRespetaLimiteDeTresActivos() {
        val id = original()
        repeat(3) { pedido("RECIBIDO") }
        val modelo = cargar(id)
        esperar(modelo.totalCalculado) { it > 0 }
        esperar(modelo.recibirPedidos) { it == true }
        main { modelo.confirmar() }
        esperar(modelo.error) { it?.contains("3 pedidos activos") == true }
        assertEquals(4, io { repo.observarPedidosDeUsuario(1).first().size })
        assertFalse(modelo.pedidoCreadoExito.value)
    }
}
