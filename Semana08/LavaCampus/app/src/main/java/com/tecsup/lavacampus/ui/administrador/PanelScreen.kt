package com.tecsup.lavacampus.ui.administrador

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.ui.components.graficos.*
import com.tecsup.lavacampus.viewmodel.*
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
    onAjustesClick: () -> Unit,
) {
    val hoy by viewModel.pedidosHoy.collectAsState()
    val proceso by viewModel.enProceso.collectAsState()
    val recoger by viewModel.porRecoger.collectAsState()
    val atrasados by viewModel.atrasados.collectAsState()
    val ingresos by viewModel.ingresosSemana.collectAsState()
    val pagados by viewModel.pedidosPagadosSemana.collectAsState()
    val estados by viewModel.porEstado.collectAsState()
    val servicios by viewModel.porServicio.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(
        rol = Rol.ADMIN,
        topBar = { Cabecera("Panel de la lavandería") },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionAdmin(
                TabAdmin.PANEL,
                {},
                onPedidosClick,
                onServiciosClick,
                onClientesClick,
                onAjustesClick,
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Secundario(
                LocalDate.now()
                    .format(
                        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es"))
                    )
            )
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "INGRESOS DE LA SEMANA",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(dinero(ingresos), fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                    Text("$pagados pedidos cobrados desde el lunes", fontSize = 12.sp)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(hoy to "Pedidos hoy", proceso to "En proceso", recoger to "Por recoger")
                    .forEach { (valor, nombre) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                valor.toString(),
                                fontSize = 27.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                            Secundario(nombre)
                        }
                    }
            }
            Surface(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onVerPedidosListo),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$recoger pedidos listos esperan recojo",
                        Modifier.weight(1f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text("Ver", fontWeight = FontWeight.Bold)
                }
            }
            Surface(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onPedidosClick),
                color =
                    if (atrasados > 0) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Pedidos atrasados: $atrasados",
                        Modifier.weight(1f),
                        fontSize = 12.sp,
                        color =
                            if (atrasados > 0) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text("Ver", fontWeight = FontWeight.Bold)
                }
            }
            Seccion("Pedidos por estado")
            GraficoDona(estados)
            Seccion("Ingresos por servicio")
            GraficoBarras(servicios)
            Spacer(Modifier.height(16.dp))
        }
    }
}
