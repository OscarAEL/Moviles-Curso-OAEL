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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Schedule
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
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionEstudiante
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.TabEstudiante
import com.tecsup.lavacampus.ui.util.textoPrecio
import com.tecsup.lavacampus.viewmodel.TarifasViewModel

@Composable
fun TarifasScreen(
    viewModel: TarifasViewModel,
    onBackClick: () -> Unit,
    onInicioClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    val serviciosActivos by viewModel.serviciosActivos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
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
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Tarifas y servicios",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            BarraNavegacionEstudiante(
                tabSeleccionada = TabEstudiante.TARIFAS,
                onInicioClick = onInicioClick,
                onPedidosClick = onPedidosClick,
                onTarifasClick = { },
                onPerfilClick = onPerfilClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (cargando && serviciosActivos.isEmpty()) {
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

                    if (serviciosActivos.isEmpty()) {
                        Text(
                            text = "No hay servicios disponibles en este momento",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            serviciosActivos.forEach { servicio ->
                                TarjetaTarifaServicio(servicio = servicio)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Schedule and location card
                    TarjetaHorarioAtencion()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rules card (yellow/beige highlight)
                    TarjetaReglasServicio()

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TarjetaTarifaServicio(servicio: Servicio) {
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
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                IconoServicio(
                    icono = servicio.icono,
                    modifier = Modifier.size(30.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = servicio.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = servicio.textoPrecio(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Entrega en ${servicio.horasEntrega} h",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TarjetaHorarioAtencion() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Horario de atención",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Lunes a sábado · 8:00 a 18:00",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Recojo en casilleros del pabellón A",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TarjetaReglasServicio() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mínimo 1 kg por servicio de lavado.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Máximo 3 pedidos activos por estudiante.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF92400E)
            )
        }
    }
}
