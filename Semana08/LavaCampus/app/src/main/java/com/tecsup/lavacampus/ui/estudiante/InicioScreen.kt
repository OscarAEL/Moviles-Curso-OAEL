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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onNuevoPedidoClick: () -> Unit,
    onMisPedidosClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onPedidoClick: (Int) -> Unit,
) {
    val activo by viewModel.pedidoActivo.collectAsState()
    val recientes by viewModel.recientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionEstudiante(
                TabEstudiante.INICIO,
                {},
                onMisPedidosClick,
                onTarifasClick,
                onPerfilClick,
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Hola, ${viewModel.nombreEstudiante.substringBefore(' ')}",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Secundario("Código ${viewModel.codigoEstudiante} · Estudiante")
                }
                Box(Modifier.clip(CircleShape).clickable(onClick = onPerfilClick)) {
                    Avatar(viewModel.nombreEstudiante)
                }
            }
            if (cargando && activo == null) CircularProgressIndicator()
            else {
                val item = activo
                if (item != null) {
                    val p = item.pedido
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Text(
                                "PEDIDO ${numero(p.id)} · ${p.estado.replace('_',' ')}",
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                when (p.estado) {
                                    "RECIBIDO" -> "Recibimos tu pedido"
                                    "EN_PROCESO" -> "Tu ropa se está lavando"
                                    "LISTO" -> "Tu ropa está lista"
                                    else -> "Tu pedido"
                                },
                                fontSize = 25.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            StepperEstado(
                                p.estado,
                                colorCompletado = MaterialTheme.colorScheme.secondaryContainer,
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = .15f))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        "Entrega estimada",
                                        color = Color(0xFFAFE8EF),
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        FechaUtils.formatearFechaCorta(p.fechaEntrega),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                    )
                                }
                                Button(
                                    onClick = { onPedidoClick(p.id) },
                                    colors =
                                        ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = MaterialTheme.colorScheme.secondary,
                                        ),
                                ) {
                                    Text("Ver detalle")
                                }
                            }
                            if (p.estado == "LISTO" && p.casillero != null)
                                Text(
                                    "Casillero ${p.casillero}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                )
                        }
                    }
                } else
                    EstadoVacio(
                        "Tu próxima colada empieza aquí",
                        "Crea un pedido para ver su seguimiento.",
                    )
            }
            BotonPrincipal("Nuevo pedido", onNuevoPedidoClick, icono = Icons.Outlined.Add)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Recientes",
                    Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                )
                TextButton(onClick = onMisPedidosClick) { Text("Ver todos") }
            }
            recientes.forEach { item -> PedidoCard(item, { onPedidoClick(item.pedido.id) }) }
        }
    }
}
