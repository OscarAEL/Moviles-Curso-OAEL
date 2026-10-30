package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*
import java.time.LocalDate

@Composable
fun GestionPedidoScreen(viewModel: GestionPedidoViewModel, onBackClick: () -> Unit) {
    val item by viewModel.pedido.collectAsState()
    val ocupados by viewModel.casillerosOcupados.collectAsState()
    val elegido by viewModel.casilleroElegido.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val operando by viewModel.operando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    var cancelar by remember { mutableStateOf(false) }
    ConfirmarCancelacion(
        cancelar,
        operando,
        { cancelar = false },
        {
            cancelar = false
            viewModel.cancelar()
        },
    )
    LavaScaffold(
        rol = Rol.ADMIN,
        topBar = {
            Cabecera(
                item?.let { "Pedido ${numero(it.pedido.id)}" } ?: "Gestionar pedido",
                if (!operando) onBackClick else null,
                accion = { item?.let { EstadoPedidoChip(it.pedido.estado) } },
            )
        },
        snackbarHost = { SnackbarHost(host) },
    ) { padding ->
        if (cargando) CircularProgressIndicator(Modifier.padding(padding).padding(24.dp))
        else if (item == null)
            Box(Modifier.padding(padding)) {
                EstadoVacio("Pedido no disponible", "Vuelve a la bandeja.")
            }
        else
            item?.let { detalle ->
                val p = detalle.pedido
                val esFinal = p.estado in setOf("ENTREGADO", "CANCELADO")
                Column(
                    Modifier.fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(detalle.usuario.nombre)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(detalle.usuario.nombre, fontWeight = FontWeight.Bold)
                            Secundario("Código ${detalle.usuario.codigo}")
                        }
                    }
                    StepperEstado(p.estado)
                    if (!esFinal && p.fechaEntrega < LocalDate.now().toString())
                        Aviso(
                            "Atrasado: debía entregarse el ${FechaUtils.formatearFechaCorta(p.fechaEntrega)}",
                            true,
                        )
                    Seccion("Casillero para el recojo")
                    if (p.estado == "LISTO" || esFinal) {
                        if (p.casillero != null) CasilleroDestacado(p.casillero!!)
                        else Secundario("Sin casillero asignado")
                    } else {
                        Casilleros.todos.chunked(6).forEach { fila ->
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                fila.forEach { c ->
                                    val ocupado = c in ocupados && c != p.casillero
                                    val seleccionado = c == elegido
                                    Surface(
                                        modifier =
                                            Modifier.weight(1f)
                                                .heightIn(min = 46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .semantics {
                                                    contentDescription = "Casillero $c"
                                                    if (ocupado || operando) disabled()
                                                }
                                                .then(
                                                    if (!ocupado && !operando)
                                                        Modifier.clickable {
                                                            viewModel.elegirCasillero(c)
                                                        }
                                                    else Modifier
                                                ),
                                        shape = RoundedCornerShape(12.dp),
                                        border =
                                            BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant,
                                            ),
                                        color =
                                            if (seleccionado) MaterialTheme.colorScheme.primary
                                            else if (ocupado)
                                                MaterialTheme.colorScheme.surfaceContainerHighest
                                            else MaterialTheme.colorScheme.surface,
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                c,
                                                fontSize = 11.sp,
                                                color =
                                                    if (seleccionado) Color.White
                                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight =
                                                    if (seleccionado) FontWeight.Bold
                                                    else FontWeight.Normal,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Secundario(
                            "Los casilleros en gris ya están ocupados. ${elegido?.let { "$it elegido" } ?: "Selecciona uno disponible"}"
                        )
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Secundario("Total")
                            Text(
                                dinero(p.total),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                        Text("Cobrado", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Switch(
                            checked = p.pagado,
                            enabled = !esFinal && !operando,
                            onCheckedChange = viewModel::marcarPagado,
                        )
                    }
                    Secundario(
                        when {
                            p.estado == "CANCELADO" && !p.pagado ->
                                "Pedido cancelado: no requiere pago ni recojo"
                            p.estado == "CANCELADO" -> "Pago registrado · pedido cancelado"
                            p.estado == "ENTREGADO" && p.pagado ->
                                "Pago registrado · pedido entregado"
                            p.estado == "ENTREGADO" -> "Pedido entregado sin cobro"
                            p.pagado -> "Pago registrado"
                            else -> "Se cobra al entregar"
                        }
                    )
                    Aviso(
                        "Para entregar, el pedido debe estar cobrado. Para pasar a Listo, debe tener casillero."
                    )
                    Text("Detalle del pedido", fontWeight = FontWeight.Bold)
                    detalle.detalles.forEach {
                        FilaImporte(
                            "${it.servicio.nombre} · ${it.detalle.cantidad} ${it.servicio.unidad.lowercase()} × ${dinero(it.detalle.precioUnitario)}",
                            dinero(it.detalle.subtotal),
                        )
                    }
                    DesgloseDescuento(p.subtotal, p.descuento)
                    if (p.nota.isNotBlank()) Aviso("Nota: ${p.nota}")
                    val accion =
                        when (p.estado) {
                            "RECIBIDO" -> "Iniciar pedido"
                            "EN_PROCESO" -> "Marcar como Listo"
                            "LISTO" -> "Entregar pedido"
                            else -> null
                        }
                    accion?.let {
                        BotonPrincipal(
                            if (operando) "Guardando…" else it,
                            viewModel::avanzar,
                            enabled = !operando,
                        )
                    }
                    if (p.estado in setOf("RECIBIDO", "EN_PROCESO"))
                        OutlinedButton(
                            onClick = { cancelar = true },
                            enabled = !operando,
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text("Cancelar pedido")
                        }
                }
            }
    }
}
