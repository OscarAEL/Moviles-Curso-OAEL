package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionEstudiante
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.PedidoCard
import com.tecsup.lavacampus.ui.components.TabEstudiante
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import java.util.Locale

@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel,
    onBackClick: () -> Unit,
    onNuevoPedidoClick: () -> Unit,
    onInicioClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onPedidoClick: (Int) -> Unit,
    pestanaInicial: Int = 0
) {
    val activos by viewModel.activos.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val cancelando by viewModel.cancelando.collectAsState()
    val error by viewModel.error.collectAsState()

    var pestanaSeleccionada by remember { mutableIntStateOf(pestanaInicial) } // 0 = Activos, 1 = Historial
    var pedidoACancelar by remember { mutableStateOf<PedidoConDetalle?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    if (pedidoACancelar != null) {
        val p = pedidoACancelar!!
        val numPed = String.format(Locale.US, "%03d", p.pedido.id)
        AlertDialog(
            onDismissRequest = { if (!cancelando) pedidoACancelar = null },
            title = {
                Text(
                    text = "¿Cancelar pedido?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cancelar el pedido #$numPed? Esta acción no se puede deshacer.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val item = pedidoACancelar
                        pedidoACancelar = null
                        if (item != null) {
                            viewModel.cancelar(item)
                        }
                    },
                    enabled = !cancelando
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
                    onClick = { pedidoACancelar = null },
                    enabled = !cancelando
                ) {
                    Text(text = "Volver")
                }
            }
        )
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
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Mis pedidos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            BarraNavegacionEstudiante(
                tabSeleccionada = TabEstudiante.PEDIDOS,
                onInicioClick = onInicioClick,
                onPedidosClick = { },
                onTarifasClick = onTarifasClick,
                onPerfilClick = onPerfilClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tabs: Activos / Historial
                PestanasPedidos(
                    pestanaSeleccionada = pestanaSeleccionada,
                    onPestanaSeleccionada = { pestanaSeleccionada = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                val listaActual = if (pestanaSeleccionada == 0) activos else historial

                if (cargando && listaActual.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (listaActual.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (pestanaSeleccionada == 0) "No tienes pedidos activos" else "No tienes pedidos en el historial",
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listaActual.forEach { item ->
                            PedidoCard(
                                pedidoConDetalle = item,
                                onClick = { onPedidoClick(item.pedido.id) },
                                mostrarAccionCancelar = viewModel.puedeCancelar(item),
                                onCancelarClick = { pedidoACancelar = item }
                            )
                        }

                        // Aviso card if there is a LISTO order in Activos
                        if (pestanaSeleccionada == 0) {
                            val pedidoListo = activos.firstOrNull {
                                it.pedido.estado == EstadoPedido.LISTO.name && !it.pedido.casillero.isNullOrEmpty()
                            }
                            if (pedidoListo != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                TarjetaAvisoCasillero(pedido = pedidoListo)
                            }
                        }

                        Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
                    }
                }
            }

            // Floating Action Button (+)
            FloatingActionButton(
                onClick = onNuevoPedidoClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 20.dp, end = 20.dp)
                    .size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nuevo pedido",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun PestanasPedidos(
    pestanaSeleccionada: Int,
    onPestanaSeleccionada: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF3F4F6)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab Activos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (pestanaSeleccionada == 0) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onPestanaSeleccionada(0) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Activos",
                    fontSize = 14.sp,
                    fontWeight = if (pestanaSeleccionada == 0) FontWeight.Bold else FontWeight.Medium,
                    color = if (pestanaSeleccionada == 0) Color.White else Color(0xFF6B7280)
                )
            }

            // Tab Historial
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (pestanaSeleccionada == 1) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onPestanaSeleccionada(1) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Historial",
                    fontSize = 14.sp,
                    fontWeight = if (pestanaSeleccionada == 1) FontWeight.Bold else FontWeight.Medium,
                    color = if (pestanaSeleccionada == 1) Color.White else Color(0xFF6B7280)
                )
            }
        }
    }
}

@Composable
private fun TarjetaAvisoCasillero(
    pedido: PedidoConDetalle
) {
    val numPedido = String.format(Locale.US, "%03d", pedido.pedido.id)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFECFEFF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aviso",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu pedido #$numPedido está listo: recógelo en el casillero indicado.",
                fontSize = 13.sp,
                color = Color(0xFF374151)
            )
        }
    }
}
