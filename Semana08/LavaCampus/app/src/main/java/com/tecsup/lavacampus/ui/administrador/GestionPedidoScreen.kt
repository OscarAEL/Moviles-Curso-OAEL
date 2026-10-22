package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.DetalleConServicio
import com.tecsup.lavacampus.model.Casilleros
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.components.StepperOrientacion
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import java.util.Locale

@Composable
fun GestionPedidoScreen(
    viewModel: GestionPedidoViewModel,
    onBackClick: () -> Unit
) {
    val itemConDetalle by viewModel.pedido.collectAsState()
    val casillerosOcupados by viewModel.casillerosOcupados.collectAsState()
    val casilleroElegido by viewModel.casilleroElegido.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val operando by viewModel.operando.collectAsState()
    val error by viewModel.error.collectAsState()

    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    val numPedidoStr = String.format(Locale.US, "%03d", viewModel.pedidoId)

    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { if (!operando) mostrarDialogoCancelar = false },
            title = {
                Text(
                    text = "¿Cancelar pedido?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cancelar el pedido #$numPedidoStr? Esta acción no se puede deshacer.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoCancelar = false
                        viewModel.cancelar()
                    },
                    enabled = !operando
                ) {
                    Text(
                        text = "Cancelar pedido",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoCancelar = false },
                    enabled = !operando
                ) {
                    Text(text = "Volver")
                }
            }
        )
    }

    LavaScaffold(
        rol = Rol.ADMIN,
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
                    enabled = !operando,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Gestionar pedido #$numPedidoStr",
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
        ) {
            if (cargando || itemConDetalle == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val item = itemConDetalle!!
                val pedido = item.pedido
                val usuario = item.usuario
                val detalles = item.detalles

                val esFinal = pedido.estado == EstadoPedido.ENTREGADO.name || pedido.estado == EstadoPedido.CANCELADO.name
                val puedeCancelar = pedido.estado == EstadoPedido.RECIBIDO.name || pedido.estado == EstadoPedido.EN_PROCESO.name

                val textoBotonAvanzar = when (pedido.estado) {
                    EstadoPedido.RECIBIDO.name -> "Iniciar pedido"
                    EstadoPedido.EN_PROCESO.name -> "Marcar como Listo"
                    EstadoPedido.LISTO.name -> "Entregar pedido"
                    else -> null
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Student Info Card (Avatar, Name, Code, Status Chip)
                    TarjetaInfoEstudiante(usuario = usuario, estadoStr = pedido.estado)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Vertical Stepper (using Admin Primary Color)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp
                    ) {
                        Box(modifier = Modifier.padding(18.dp)) {
                            StepperEstado(
                                estadoStr = pedido.estado,
                                orientacion = StepperOrientacion.VERTICAL,
                                fechaRegistro = pedido.fechaRegistro,
                                colorCompletado = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Locker Section
                    Text(
                        text = "Casillero para el recojo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (pedido.estado == EstadoPedido.LISTO.name || esFinal) {
                        if (!pedido.casillero.isNullOrEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "Casillero ${pedido.casillero}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "Sin casillero asignado",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val lazyListState = rememberLazyListState()

                        LaunchedEffect(casilleroElegido, pedido.casillero) {
                            val casilleroTarget = casilleroElegido ?: pedido.casillero
                            if (!casilleroTarget.isNullOrBlank()) {
                                val idx = Casilleros.todos.indexOf(casilleroTarget)
                                if (idx >= 0) {
                                    lazyListState.animateScrollToItem(idx)
                                }
                            }
                        }

                        LazyRow(
                            state = lazyListState,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(Casilleros.todos) { c ->
                                val estaOcupado = casillerosOcupados.contains(c) && c != pedido.casillero
                                val estaElegido = c == casilleroElegido
                                val esEditable = !estaOcupado

                                val bg = when {
                                    estaElegido -> MaterialTheme.colorScheme.primary
                                    estaOcupado -> MaterialTheme.colorScheme.surfaceContainerHighest
                                    else -> MaterialTheme.colorScheme.surface
                                }

                                val txtColor = when {
                                    estaElegido -> MaterialTheme.colorScheme.onPrimary
                                    estaOcupado -> Color(0xFF9CA3AF)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                val borderColor = when {
                                    estaElegido -> MaterialTheme.colorScheme.primary
                                    estaOcupado -> Color(0xFFE5E7EB)
                                    else -> Color(0xFFD1D5DB)
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = bg,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .then(
                                            if (esEditable && !operando) {
                                                Modifier.clickable { viewModel.elegirCasillero(c) }
                                            } else {
                                                Modifier
                                            }
                                        )
                                ) {
                                    Text(
                                        text = c,
                                        fontSize = 13.sp,
                                        fontWeight = if (estaElegido) FontWeight.Bold else FontWeight.Medium,
                                        color = txtColor,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Los casilleros en gris ya están ocupados",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Payment Row Card
                    TarjetaMontoYCobro(
                        total = pedido.total,
                        pagado = pedido.pagado,
                        estadoStr = pedido.estado,
                        operando = operando,
                        esFinal = esFinal,
                        onPagadoChange = { viewModel.marcarPagado(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rules Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAF5FF)
                        )
                    ) {
                        Text(
                            text = "Regla: no se entrega un pedido sin cobrar, ni se pasa a Listo sin casillero.",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B21A8),
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Historical Order Items Detail
                    TarjetaDetalleLineas(detalles = detalles)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    if (operando) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else if (!esFinal) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (textoBotonAvanzar != null) {
                                Button(
                                    onClick = { viewModel.avanzar() },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = textoBotonAvanzar,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            if (puedeCancelar) {
                                OutlinedButton(
                                    onClick = { mostrarDialogoCancelar = true },
                                    shape = RoundedCornerShape(18.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFEF4444)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Text(
                                        text = "Cancelar pedido",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaInfoEstudiante(
    usuario: Usuario,
    estadoStr: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle
            val iniciales = usuario.nombre.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .uppercase()

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = usuario.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (usuario.codigo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = usuario.codigo,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            EstadoPedidoChip(estadoStr = estadoStr)
        }
    }
}

private fun obtenerTextoSubtituloCobro(estadoStr: String, pagado: Boolean): String {
    return when (estadoStr) {
        EstadoPedido.CANCELADO.name -> {
            if (pagado) "Pago registrado · pedido cancelado"
            else "Pedido cancelado: no requiere pago ni recojo"
        }
        EstadoPedido.ENTREGADO.name -> {
            if (pagado) "Pago registrado · pedido entregado"
            else "Pedido entregado sin cobro"
        }
        else -> {
            if (pagado) "Pago registrado"
            else "Se cobra al entregar"
        }
    }
}

@Composable
private fun TarjetaMontoYCobro(
    total: Double,
    pagado: Boolean,
    estadoStr: String,
    operando: Boolean,
    esFinal: Boolean,
    onPagadoChange: (Boolean) -> Unit
) {
    val totalStr = String.format(Locale.US, "%.2f", total)
    val subtituloPago = obtenerTextoSubtituloCobro(estadoStr, pagado)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Total S/ $totalStr",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtituloPago,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cobrado",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (pagado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = pagado,
                    onCheckedChange = onPagadoChange,
                    enabled = !operando && !esFinal,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun TarjetaDetalleLineas(
    detalles: List<DetalleConServicio>
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Detalle del pedido",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                detalles.forEach { item ->
                    val d = item.detalle
                    val s = item.servicio
                    val cantStr = if (s.unidad == "KG") "${d.cantidad} kg" else "${d.cantidad.toInt()} ${if (d.cantidad == 1.0) s.unidad.lowercase() else if (s.unidad == "UNIDAD") "unidades" else "prendas"}"
                    val precioUnitStr = String.format(Locale.US, "%.2f", d.precioUnitario)
                    val subtotalStr = String.format(Locale.US, "%.2f", d.subtotal)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = s.nombre,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$cantStr · S/ $precioUnitStr c/u",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "S/ $subtotalStr",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
