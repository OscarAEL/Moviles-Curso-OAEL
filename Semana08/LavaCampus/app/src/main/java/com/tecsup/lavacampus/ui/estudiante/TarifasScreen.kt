package com.tecsup.lavacampus.ui.estudiante

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun TarifasScreen(
    viewModel: TarifasViewModel,
    onBackClick: () -> Unit,
    onInicioClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {
    val servicios by viewModel.serviciosActivos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(
        topBar = { Cabecera("Tarifas y servicios") },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionEstudiante(
                TabEstudiante.TARIFAS,
                onInicioClick,
                onPedidosClick,
                {},
                onPerfilClick,
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (cargando) CircularProgressIndicator()
            if (!cargando && servicios.isEmpty())
                EstadoVacio("No hay servicios disponibles", "Vuelve a consultar más tarde.")
            servicios.forEach { s ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(14.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconoServicio(
                            s.icono,
                            Modifier.size(26.dp),
                            MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(s.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Secundario("Entrega en ${s.horasEntrega} h")
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(dinero(s.precio), fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                        Secundario("por ${s.unidad.lowercase()}")
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "REGLAS",
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Lavado: mínimo 1 kg, máximo 10 kg.\nMás de 5 kg de lavado: 10 % de descuento.\nMáximo 3 pedidos activos a la vez.",
                        fontSize = 13.sp,
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = .2f))
                    Text("Si la entrega cae en domingo, pasa al lunes.", fontSize = 13.sp)
                }
            }
        }
    }
}
