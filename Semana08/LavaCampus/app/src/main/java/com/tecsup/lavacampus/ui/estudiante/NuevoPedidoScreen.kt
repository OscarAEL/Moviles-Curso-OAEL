package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BotonPrincipal
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.SelectorCantidad
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.LineaResumenItem
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.ui.components.DesgloseDescuento
import java.util.Locale

@Composable
fun NuevoPedidoScreen(
    viewModel: NuevoPedidoViewModel,
    onBackClick: () -> Unit,
    onPedidoCreadoExito: () -> Unit
) {
    val serviciosActivos by viewModel.serviciosActivos.collectAsState()
    val cantidades by viewModel.cantidades.collectAsState()
    val nota by viewModel.nota.collectAsState()
    val lineasResumen by viewModel.lineasResumen.collectAsState()
    val totalCalculado by viewModel.totalCalculado.collectAsState()
    val totales by viewModel.totales.collectAsState()
    val fechaEstimadaIso by viewModel.fechaEstimadaIso.collectAsState()
    val recibirPedidos by viewModel.recibirPedidos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val cargandoRepeticion by viewModel.cargandoRepeticion.collectAsState()
    val avisoRepeticion by viewModel.avisoRepeticion.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val error by viewModel.error.collectAsState()
    val pedidoCreadoExito by viewModel.pedidoCreadoExito.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    LaunchedEffect(pedidoCreadoExito) {
        if (pedidoCreadoExito) {
            onPedidoCreadoExito()
        }
    }

    LavaScaffold(
        rol = Rol.CLIENTE,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    enabled = !guardando && !pedidoCreadoExito,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Nuevo pedido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            if (cargando || cargandoRepeticion) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "¿Qué necesitas?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    avisoRepeticion?.let { aviso ->
                        Surface(color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(16.dp)) {
                            Text(aviso, modifier = Modifier.padding(16.dp), fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Active Services List Cards
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        serviciosActivos.forEach { servicio ->
                            val cantidad = cantidades[servicio.id] ?: 0.0
                            TarjetaServicioSeleccionable(
                                servicio = servicio,
                                cantidad = cantidad,
                                guardando = guardando || pedidoCreadoExito,
                                onIncrementar = {
                                    val delta = if (servicio.unidad == "KG") {
                                        if (cantidad == 0.0) 1.0 else 0.5
                                    } else {
                                        1.0
                                    }
                                    viewModel.cambiarCantidad(servicio.id, delta)
                                },
                                onDecrementar = {
                                    val delta = if (servicio.unidad == "KG") {
                                        if (cantidad == 1.0) -1.0 else -0.5
                                    } else {
                                        -1.0
                                    }
                                    viewModel.cambiarCantidad(servicio.id, delta)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Optional Note Input
                    LavaTextField(
                        value = nota,
                        onValueChange = viewModel::actualizarNota,
                        label = "Nota (opcional)",
                        enabled = !guardando && !pedidoCreadoExito
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Summary Box
                    TarjetaResumen(lineas = lineasResumen, total = totalCalculado, subtotal = totales.subtotal, descuento = totales.descuento)

                    // Estimated Delivery Date Text
                    fechaEstimadaIso?.let { isoDate ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Entrega estimada: ${FechaUtils.formatearFechaCorta(isoDate)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    if (recibirPedidos == false) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "La lavandería no está recibiendo pedidos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Confirm Order Button
                    if (guardando) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        BotonPrincipal(
                            texto = "Confirmar pedido",
                            onClick = { viewModel.confirmar() },
                            enabled = totalCalculado > 0.0 && recibirPedidos == true && !pedidoCreadoExito
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicioSeleccionable(
    servicio: Servicio,
    cantidad: Double,
    guardando: Boolean,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit
) {
    val estaSeleccionado = cantidad > 0.0
    val anchoBorde = if (estaSeleccionado) 1.5.dp else 1.dp
    val colorBorde = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFFF1F0F5)

    val precioFormateado = String.format(Locale.US, "%.2f", servicio.precio)
    val unidadTexto = servicio.unidad.lowercase()

    val cantidadTexto = when {
        cantidad == 0.0 -> "0"
        servicio.unidad == "KG" -> "$cantidad kg"
        else -> "${cantidad.toInt()}"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = anchoBorde,
                color = colorBorde,
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    IconoServicio(
                        icono = servicio.icono,
                        modifier = Modifier.size(26.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = servicio.nombre,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "S/ $precioFormateado por $unidadTexto · ${servicio.horasEntrega} h",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quantity Selector on bottom right
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                SelectorCantidad(
                    cantidadTexto = cantidadTexto,
                    onDecrementar = onDecrementar,
                    onIncrementar = onIncrementar,
                    puedeDecrementar = cantidad > 0.0,
                    puedeIncrementar = if (servicio.unidad == "KG") cantidad < 10.0 else cantidad < 30.0,
                    enabled = !guardando
                )
            }
        }
    }
}

@Composable
private fun TarjetaResumen(
    lineas: List<LineaResumenItem>,
    total: Double,
    subtotal: Double = total,
    descuento: Double = 0.0
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Resumen",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (lineas.isEmpty()) {
                Text(
                    text = "No has seleccionado ningún servicio",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lineas.forEach { item ->
                        val cantStr = if (item.servicio.unidad == "KG") {
                            "${item.cantidad} kg"
                        } else {
                            "${item.cantidad.toInt()} ${if (item.cantidad == 1.0) item.servicio.unidad.lowercase() else if (item.servicio.unidad == "UNIDAD") "unidades" else "prendas"}"
                        }
                        val subtotalStr = String.format(Locale.US, "%.2f", item.subtotal)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.servicio.nombre}  $cantStr",
                                modifier = Modifier.weight(1f).padding(end = 12.dp),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "S/ $subtotalStr",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.25f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    DesgloseDescuento(subtotal, descuento)
                    val totalStr = String.format(Locale.US, "%.2f", total)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "S/ $totalStr",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
