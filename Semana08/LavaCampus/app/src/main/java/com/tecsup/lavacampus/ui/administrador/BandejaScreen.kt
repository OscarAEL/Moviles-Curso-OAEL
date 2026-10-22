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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionAdmin
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.TabAdmin
import com.tecsup.lavacampus.ui.components.formatearResumenDetalles
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.FiltroBandeja
import java.util.Locale

@Composable
fun BandejaScreen(
    viewModel: BandejaViewModel,
    filtroInicialStr: String? = null,
    onBackClick: () -> Unit = {},
    onGestionarPedido: (Int) -> Unit,
    onPanelClick: () -> Unit = {},
    onServiciosClick: () -> Unit = {},
    onClientesClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {}
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val filtroSeleccionado by viewModel.filtroSeleccionado.collectAsState()
    val pedidos by viewModel.pedidos.collectAsState()
    val atrasados by viewModel.pedidosAtrasados.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(filtroInicialStr) {
        viewModel.aplicarFiltroNavegacion(filtroInicialStr)
    }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
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
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Pedidos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            BarraNavegacionAdmin(
                tabSeleccionada = TabAdmin.PEDIDOS,
                onPanelClick = onPanelClick,
                onPedidosClick = { },
                onServiciosClick = onServiciosClick,
                onClientesClick = onClientesClick,
                onAjustesClick = onAjustesClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Search Input
                LavaTextField(
                    value = busqueda,
                    onValueChange = viewModel::actualizarBusqueda,
                    label = "Buscar por estudiante o número",
                    leadingIcon = Icons.Default.Search
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(FiltroBandeja.entries.toTypedArray()) { filtro ->
                        val estaSeleccionado = filtro == filtroSeleccionado
                        val bg = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color.White
                        val textColor = if (estaSeleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        val borderColor = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB)

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = bg,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                                .clickable { viewModel.actualizarFiltro(filtro) }
                        ) {
                            Text(
                                text = filtro.etiqueta,
                                fontSize = 13.sp,
                                fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Medium,
                                color = textColor,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Orders List
                if (cargando && pedidos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (pedidos.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron pedidos",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = pedidos,
                            key = { it.pedido.id }
                        ) { item ->
                            TarjetaPedidoAdmin(
                                atrasado = item.pedido.id in atrasados,
                                pedidoConDetalle = item,
                                onClick = { onGestionarPedido(item.pedido.id) },
                                onAccionClick = { onGestionarPedido(item.pedido.id) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPedidoAdmin(
    atrasado: Boolean,
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit,
    onAccionClick: () -> Unit
) {
    val pedido = pedidoConDetalle.pedido
    val usuario = pedidoConDetalle.usuario
    val detalles = pedidoConDetalle.detalles

    val resumenServicios = formatearResumenDetalles(detalles)
    val fechaFormateada = FechaUtils.formatearFechaCorta(pedido.fechaRegistro)
    val numPedidoStr = String.format(Locale.US, "%03d", pedido.id)
    val precioStr = String.format(Locale.US, "%.2f", pedido.total)
    val primerNombreEstudiante = usuario.nombre.split(" ").firstOrNull() ?: usuario.nombre

    val (bgContainer, iconColor) = when (pedido.estado) {
        EstadoPedido.RECIBIDO.name -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        EstadoPedido.EN_PROCESO.name -> Color(0xFFDBEAFE) to Color(0xFF2563EB)
        EstadoPedido.LISTO.name -> Color(0xFFD1FAE5) to Color(0xFF16A34A)
        EstadoPedido.ENTREGADO.name -> Color(0xFFF1F5F9) to Color(0xFF475569)
        else -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    }

    val textoAccion = when (pedido.estado) {
        EstadoPedido.RECIBIDO.name -> "Iniciar"
        EstadoPedido.EN_PROCESO.name -> "Marcar listo"
        EstadoPedido.LISTO.name -> "Entregar"
        else -> null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = if (atrasado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Service Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgContainer),
                    contentAlignment = Alignment.Center
                ) {
                    IconoServicio(
                        icono = "wash",
                        modifier = Modifier.size(26.dp),
                        color = iconColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pedido #$numPedidoStr",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$primerNombreEstudiante · $resumenServicios",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (atrasado) {
                        Text(
                            "Atrasado · entrega ${FechaUtils.formatearFechaCorta(pedido.fechaEntrega)}",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (fechaFormateada.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = fechaFormateada,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "S/ $precioStr",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChip(estadoStr = pedido.estado)

                if (textoAccion != null) {
                    Button(
                        onClick = onAccionClick,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Text(
                            text = textoAccion,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
