package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun DetallePedidoScreen(viewModel: DetallePedidoViewModel, onBackClick: () -> Unit) {
    val item by viewModel.pedido.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val cancelando by viewModel.cancelando.collectAsState()
    val puedeCancelar by viewModel.puedeCancelar.collectAsState()
    val noDisponible by viewModel.pedidoNoDisponible.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    var confirmar by remember { mutableStateOf(false) }
    ConfirmarCancelacion(
        confirmar,
        cancelando,
        { confirmar = false },
        {
            confirmar = false
            viewModel.cancelar()
        },
    )
    LavaScaffold(
        topBar = {
            Cabecera(
                item?.let { "Pedido ${numero(it.pedido.id)}" } ?: "Detalle del pedido",
                onBackClick,
                accion = { item?.let { EstadoPedidoChip(it.pedido.estado) } },
            )
        },
        snackbarHost = { SnackbarHost(host) },
    ) { padding ->
        if (cargando) CircularProgressIndicator(Modifier.padding(padding).padding(24.dp))
        else if (noDisponible || item == null)
            Box(Modifier.padding(padding)) {
                EstadoVacio("Pedido no disponible", "Vuelve a tus pedidos para continuar.")
            }
        else
            item?.let { detalle ->
                val p = detalle.pedido
                Column(
                    Modifier.fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    if (p.estado == "LISTO" && !p.casillero.isNullOrBlank())
                        CasilleroDestacado(p.casillero!!)
                    Secundario(
                        "Registrado el ${FechaUtils.formatearFechaCorta(p.fechaRegistro)} · Entrega estimada ${FechaUtils.formatearFechaCorta(p.fechaEntrega)}"
                    )
                    Text("Seguimiento", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    StepperEstado(
                        p.estado,
                        orientacion = StepperOrientacion.VERTICAL,
                        fechaRegistro = p.fechaRegistro,
                    )
                    HorizontalDivider()
                    Text("Detalle", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    detalle.detalles.forEach {
                        FilaImporte(
                            "${it.servicio.nombre} · ${it.detalle.cantidad} ${it.servicio.unidad.lowercase()} × ${dinero(it.detalle.precioUnitario)}",
                            dinero(it.detalle.subtotal),
                        )
                    }
                    DesgloseDescuento(p.subtotal, p.descuento)
                    FilaImporte("Total", dinero(p.total), true)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (p.pagado) Color(0xFFDEF6E8) else Color(0xFFFFF2C7),
                    ) {
                        Text(
                            when {
                                p.estado == "CANCELADO" && !p.pagado -> "Pedido cancelado"
                                p.pagado -> "Pago registrado"
                                else -> "Pago pendiente · se paga al recoger"
                            },
                            Modifier.fillMaxWidth().padding(16.dp),
                            color = Color(0xFF795119),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                        )
                    }
                    if (p.nota.isNotBlank()) Aviso("Nota: ${p.nota}")
                    OutlinedButton(
                        onClick = { confirmar = true },
                        enabled = puedeCancelar && !cancelando,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(if (cancelando) "Cancelando…" else "Cancelar pedido")
                    }
                    if (!puedeCancelar) Secundario("Solo se cancela antes de empezar el lavado")
                }
            }
    }
}
