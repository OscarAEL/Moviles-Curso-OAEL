package com.tecsup.lavacampus

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MejoraAtrasadosTest : MejorasTestBase() {
    @Test fun soloActivosAnterioresAHoyEstanAtrasados() {
        val ayer = LocalDate.now().minusDays(1).toString()
        val esperados = listOf("RECIBIDO", "EN_PROCESO", "LISTO").map { pedido(it, ayer) }
        pedido("ENTREGADO", ayer, true)
        pedido("CANCELADO", ayer)
        pedido("RECIBIDO")
        pedido("EN_PROCESO", LocalDate.now().plusDays(1).toString())
        assertEquals(esperados, io { repo.observarPedidosAtrasados(LocalDate.now().toString()).first() })
    }
    @Test fun bandejaYPanelSeActualizanAlFinalizar() {
        val id = pedido("EN_PROCESO", LocalDate.now().minusDays(1).toString())
        val bandeja = vm(BandejaViewModel::class.java) { BandejaViewModel(repo) }
        val panel = vm(PanelViewModel::class.java) { PanelViewModel(repo) }
        observar(bandeja.pedidosAtrasados, panel.atrasados)
        esperar(bandeja.pedidosAtrasados) { id in it }
        esperar(panel.atrasados) { it == 1 }
        io { repo.cambiarEstado(id, EstadoPedido.CANCELADO, null) }
        esperar(bandeja.pedidosAtrasados) { it.isEmpty() }
        esperar(panel.atrasados) { it == 0 }
    }
}
