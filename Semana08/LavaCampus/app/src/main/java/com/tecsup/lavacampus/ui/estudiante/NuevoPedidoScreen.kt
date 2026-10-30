package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun NuevoPedidoScreen(
    viewModel: NuevoPedidoViewModel,
    onBackClick: () -> Unit,
    onPedidoCreadoExito: () -> Unit,
) {
    val servicios by viewModel.serviciosActivos.collectAsState()
    val cantidades by viewModel.cantidades.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val lineas by viewModel.lineasResumen.collectAsState()
    val totales by viewModel.totales.collectAsState()
    val totalCalculado by viewModel.totalCalculado.collectAsState()
    val fecha by viewModel.fechaEstimadaIso.collectAsState()
    val recibir by viewModel.recibirPedidos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val repitiendo by viewModel.cargandoRepeticion.collectAsState()
    val aviso by viewModel.avisoRepeticion.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val creado by viewModel.pedidoCreadoExito.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LaunchedEffect(creado) { if (creado) onPedidoCreadoExito() }
    val bloqueado = guardando || creado || cargando || repitiendo
    LavaScaffold(
        modifier = Modifier.imePadding(),
        topBar = { Cabecera("Nuevo pedido", if (!guardando && !creado) onBackClick else null) },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 12.dp) {
                Row(
                    Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Secundario("Total")
                        Text(
                            dinero(totalCalculado),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                    Button(
                        onClick = viewModel::confirmar,
                        enabled = !bloqueado && recibir == true && lineas.isNotEmpty(),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.heightIn(min = 50.dp),
                    ) {
                        Text(
                            if (guardando) "Guardando…" else "Confirmar pedido",
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        },
    ) { padding ->
        if (cargando || repitiendo)
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        else
            Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                aviso?.let { Box(Modifier.padding(20.dp)) { Aviso(it) } }
                if (recibir == false)
                    Box(Modifier.padding(20.dp)) {
                        Aviso("La lavandería no está recibiendo pedidos", true)
                    }
                if (servicios.isEmpty())
                    EstadoVacio("No hay servicios disponibles", "Vuelve a consultar más tarde.")
                servicios.forEach { servicio ->
                    val cant = cantidades[servicio.id] ?: 0.0
                    Row(
                        Modifier.fillMaxWidth()
                            .background(
                                if (cant > 0) MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(44.dp)
                                .background(
                                    if (cant > 0) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceContainerHighest,
                                    RoundedCornerShape(14.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            IconoServicio(
                                servicio.icono,
                                Modifier.size(25.dp),
                                if (cant > 0) Color.White else MaterialTheme.colorScheme.primary,
                            )
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                            Text(servicio.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Secundario(
                                "${dinero(servicio.precio)} por ${servicio.unidad.lowercase()} · ${servicio.horasEntrega} h"
                            )
                        }
                        if (cant == 0.0)
                            OutlinedButton(
                                onClick = { viewModel.cambiarCantidad(servicio.id, 1.0) },
                                enabled = !bloqueado,
                                contentPadding = PaddingValues(horizontal = 12.dp),
                            ) {
                                Text("Agregar", fontSize = 12.sp)
                            }
                        else
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedIconButton(
                                    onClick = {
                                        viewModel.cambiarCantidad(
                                            servicio.id,
                                            if (servicio.unidad == "KG" && cant != 1.0) -.5
                                            else -1.0,
                                        )
                                    },
                                    enabled = !bloqueado,
                                    modifier = Modifier.size(42.dp),
                                ) {
                                    Icon(Icons.Outlined.Remove, "Disminuir ${servicio.nombre}")
                                }
                                Text(
                                    if (servicio.unidad == "KG") "$cant kg"
                                    else cant.toInt().toString(),
                                    Modifier.padding(horizontal = 6.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                OutlinedIconButton(
                                    onClick = {
                                        viewModel.cambiarCantidad(
                                            servicio.id,
                                            if (servicio.unidad == "KG") .5 else 1.0,
                                        )
                                    },
                                    enabled = !bloqueado,
                                    modifier = Modifier.size(42.dp),
                                ) {
                                    Icon(Icons.Outlined.Add, "Aumentar ${servicio.nombre}")
                                }
                            }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    LavaTextField(
                        nota,
                        viewModel::actualizarNota,
                        "Nota (opcional)",
                        enabled = !bloqueado,
                    )
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Resumen", fontWeight = FontWeight.Bold)
                            lineas.forEach {
                                FilaImporte(
                                    "${it.servicio.nombre} · ${it.cantidad} ${it.servicio.unidad.lowercase()}",
                                    dinero(it.subtotal),
                                )
                            }
                            DesgloseDescuento(totales.subtotal, totales.descuento)
                            fecha?.let {
                                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                                FilaImporte("Entrega estimada", FechaUtils.formatearFechaCorta(it))
                            }
                        }
                    }
                }
            }
    }
}
