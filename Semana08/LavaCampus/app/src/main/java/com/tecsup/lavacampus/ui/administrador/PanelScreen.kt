package com.tecsup.lavacampus.ui.administrador

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionAdmin
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.TabAdmin
import com.tecsup.lavacampus.ui.components.graficos.GraficoBarras
import com.tecsup.lavacampus.ui.components.graficos.GraficoDona
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onVerPedidosListo: () -> Unit,
    onPedidosClick: () -> Unit,
    onServiciosClick: () -> Unit,
    onClientesClick: () -> Unit,
    onAjustesClick: () -> Unit
) {
    val pedidosHoy by viewModel.pedidosHoy.collectAsState()
    val enProceso by viewModel.enProceso.collectAsState()
    val porRecoger by viewModel.porRecoger.collectAsState()
    val ingresosSemana by viewModel.ingresosSemana.collectAsState()
    val pedidosPagadosSemana by viewModel.pedidosPagadosSemana.collectAsState()
    val porEstado by viewModel.porEstado.collectAsState()
    val porServicio by viewModel.porServicio.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    val fechaHoyFormateada = remember { formatearFechaEncabezado() }

    LavaScaffold(
        rol = Rol.ADMIN,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = "Panel de la lavandería",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = fechaHoyFormateada,
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
            BarraNavegacionAdmin(
                tabSeleccionada = TabAdmin.PANEL,
                onPanelClick = { },
                onPedidosClick = onPedidosClick,
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
        ) {
            if (cargando && porEstado.isEmpty()) {
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
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // 3 Stat Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaContadorMetrica(
                            valor = pedidosHoy.toString(),
                            etiqueta = "Pedidos hoy",
                            colorFondo = Color(0xFFF3E8FF),
                            colorTexto = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaContadorMetrica(
                            valor = enProceso.toString(),
                            etiqueta = "En proceso",
                            colorFondo = Color(0xFFEFF6FF),
                            colorTexto = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaContadorMetrica(
                            valor = porRecoger.toString(),
                            etiqueta = "Por recoger",
                            colorFondo = Color(0xFFECFDF5),
                            colorTexto = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Violet Weekly Income Banner Card
                    TarjetaIngresosSemana(
                        ingresosTotal = ingresosSemana,
                        cantidadPedidos = pedidosPagadosSemana
                    )

                    // Section: Pedidos por estado (Donut Chart)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Pedidos por estado",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            GraficoDona(datos = porEstado)
                        }
                    }

                    // Section: Ingresos por servicio (Bar Chart)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Ingresos por servicio (S/)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            GraficoBarras(
                                datos = porServicio,
                                colorBarra = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Alert Box for orders ready for pickup
                    if (porRecoger > 0) {
                        TarjetaAlertaRecojo(
                            porRecogerCount = porRecoger,
                            onVerClick = onVerPedidosListo
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaContadorMetrica(
    valor: String,
    etiqueta: String,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp)),
        color = colorFondo
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = valor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = etiqueta,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = colorTexto
            )
        }
    }
}

@Composable
private fun TarjetaIngresosSemana(
    ingresosTotal: Double,
    cantidadPedidos: Int
) {
    val totalFormateado = String.format(Locale.US, "S/ %,.2f", ingresosTotal)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Ingresos de la semana",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = totalFormateado,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "$cantidadPedidos pedidos",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun TarjetaAlertaRecojo(
    porRecogerCount: Int,
    onVerClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFBBF7D0),
                shape = RoundedCornerShape(20.dp)
            ),
        color = Color(0xFFDCFCE7)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF86EFAC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = Color(0xFF15803D),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "$porRecogerCount pedidos esperan recojo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF14532D)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "avisa a los estudiantes",
                    fontSize = 12.sp,
                    color = Color(0xFF166534)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onVerClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF16A34A)
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(
                    text = "Ver",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

private fun formatearFechaEncabezado(): String {
    val date = LocalDate.now()
    val localeEs = Locale.forLanguageTag("es-ES")
    val nombreDia = date.format(DateTimeFormatter.ofPattern("EEE", localeEs))
        .replace(".", "")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeEs) else it.toString() }
    val numeroDia = date.dayOfMonth
    val nombreMes = date.format(DateTimeFormatter.ofPattern("MMM", localeEs))
        .replace(".", "")
        .lowercase()

    return "Hoy · $nombreDia $numeroDia $nombreMes"
}
