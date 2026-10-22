package com.tecsup.lavacampus.ui.estudiante

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionEstudiante
import com.tecsup.lavacampus.ui.components.BotonPrincipal
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.PedidoCard
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.components.TabEstudiante
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import java.util.Locale

@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onNuevoPedidoClick: () -> Unit,
    onMisPedidosClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onPedidoClick: (Int) -> Unit
) {
    val pedidoActivo by viewModel.pedidoActivo.collectAsState()
    val recientes by viewModel.recientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    val primerNombre = viewModel.nombreEstudiante.split(" ").firstOrNull() ?: viewModel.nombreEstudiante

    LavaScaffold(
        rol = Rol.CLIENTE,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = "Hola, $primerNombre",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${viewModel.codigoEstudiante} · Tecsup",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = { /* Notificaciones */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        bottomBar = {
            BarraNavegacionEstudiante(
                tabSeleccionada = TabEstudiante.INICIO,
                onInicioClick = { },
                onPedidosClick = onMisPedidosClick,
                onTarifasClick = onTarifasClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            if (cargando && pedidoActivo == null && recientes.isEmpty()) {
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
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Active Order Card
                    TarjetaPedidoActivo(
                        pedidoActivo = pedidoActivo,
                        onPedidoClick = onPedidoClick
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // "Nuevo pedido" Button
                    BotonPrincipal(
                        texto = "Nuevo pedido",
                        onClick = onNuevoPedidoClick
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quick Access Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccesoRapidoCard(
                            titulo = "Mis pedidos",
                            subtitulo = "Ver estado",
                            iconoVector = Icons.AutoMirrored.Outlined.ListAlt,
                            onClick = onMisPedidosClick,
                            modifier = Modifier.weight(1f)
                        )

                        AccesoRapidoCard(
                            titulo = "Tarifas",
                            subtitulo = "Precios por kg",
                            iconoVector = Icons.Outlined.LocalOffer,
                            onClick = onTarifasClick,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // "Últimos pedidos" Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Últimos pedidos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )

                        Text(
                            text = "Ver todos",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onMisPedidosClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recent Orders List
                    if (recientes.isEmpty()) {
                        Text(
                            text = "No tienes pedidos registrados",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            recientes.forEach { item ->
                                PedidoCard(
                                    pedidoConDetalle = item,
                                    onClick = { onPedidoClick(item.pedido.id) }
                                )
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
private fun TarjetaPedidoActivo(
    pedidoActivo: PedidoConDetalle?,
    onPedidoClick: (Int) -> Unit
) {
    if (pedidoActivo == null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Tu pedido activo",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No tienes pedidos activos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Haz tu primer pedido y sigue su estado aquí.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    } else {
        val pedido = pedidoActivo.pedido
        val numeroPedido = String.format(Locale.US, "%03d", pedido.id)
        val estadoTexto = when (pedido.estado) {
            EstadoPedido.RECIBIDO.name -> "Recibido"
            EstadoPedido.EN_PROCESO.name -> "En proceso"
            EstadoPedido.LISTO.name -> "Listo"
            else -> pedido.estado
        }
        val fechaEntregaFormateada = FechaUtils.formatearFechaCorta(pedido.fechaEntrega)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPedidoClick(pedido.id) },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Top Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu pedido activo",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "#$numeroPedido",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = estadoTexto,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Estimate / Locker Subtitle
                if (pedido.estado == EstadoPedido.LISTO.name && !pedido.casillero.isNullOrEmpty()) {
                    Text(
                        text = "Recógelo en el casillero ${pedido.casillero}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCFFAFE)
                    )
                } else {
                    Text(
                        text = "Entrega estimada: $fechaEntregaFormateada",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stepper Container
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White
                ) {
                    Box(modifier = Modifier.padding(12.dp)) {
                        StepperEstado(estadoStr = pedido.estado)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccesoRapidoCard(
    titulo: String,
    subtitulo: String,
    iconoVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoVector,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}
