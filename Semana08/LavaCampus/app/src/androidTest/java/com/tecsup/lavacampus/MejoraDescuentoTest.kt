package com.tecsup.lavacampus

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MejoraDescuentoTest : MejorasTestBase() {
    @Test fun umbralEstrictoYDescuentoSoloAlLavado() {
        assertEquals(0.0, CalculoPedido.calcular(listOf(LineaCalculo("KG", 5.0, 8.0))).descuento, 0.001)
        val calculado = CalculoPedido.calcular(listOf(LineaCalculo("KG", 5.5, 8.0), LineaCalculo("PRENDA", 5.0, 2.5)))
        assertEquals(56.5, calculado.subtotal, 0.001)
        assertEquals(4.4, calculado.descuento, 0.001)
        assertEquals(52.1, calculado.total, 0.001)
        assertEquals(listOf(4.4, 0.0), calculado.descuentosPorLinea)
    }
    @Test fun resumenGuardadoYGraficosUsanMismoImporteNeto() {
        val vm = vm(NuevoPedidoViewModel::class.java) { NuevoPedidoViewModel(repo, prefs, alumno) }
        observar(vm.serviciosActivos, vm.totales, vm.totalCalculado, vm.lineasResumen, vm.recibirPedidos)
        esperar(vm.serviciosActivos) { it.size == 2 }
        esperar(vm.recibirPedidos) { it == true }
        main {
            vm.cambiarCantidad(1, 1.0)
            repeat(9) { vm.cambiarCantidad(1, 0.5) }
            repeat(5) { vm.cambiarCantidad(2, 1.0) }
        }
        esperar(vm.totalCalculado) { it == 52.1 }
        main { vm.confirmar() }
        esperar(vm.pedidoCreadoExito) { it }
        val pedido = io { repo.observarPedidosDeUsuario(1).first().single() }
        assertEquals(vm.totales.value.descuento, pedido.pedido.descuento, 0.001)
        assertEquals(56.5, pedido.pedido.subtotal, 0.001)
        assertEquals(4.4, pedido.detalles.sumOf { it.detalle.descuento }, 0.001)
        io { repo.marcarPagado(pedido.pedido.id, true) }
        val ingresos = io { repo.observarIngresosPorServicio(LocalDate.now().toString()).first() }
        assertEquals(pedido.pedido.total, ingresos.sumOf { it.total }, 0.001)
        io { repo.guardarServicio(lavado.copy(precio = 20.0)) }
        val historico = io { repo.observarPedido(pedido.pedido.id).first()!! }
        assertEquals(52.1, historico.pedido.total, 0.001)
        assertEquals(8.0, historico.detalles.first { it.detalle.servicioId == 1 }.detalle.precioUnitario, 0.001)
    }
}
