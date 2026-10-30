package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel,
    onBackClick: () -> Unit,
    onNuevoPedidoClick: () -> Unit,
    onInicioClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onPedidoClick: (Int) -> Unit,
    pestanaInicial: Int = 0,
    onRepetirPedido: (Int) -> Unit = {},
) {
    val activos by viewModel.activos.collectAsState()
    val historial by viewModel.historial.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val cancelando by viewModel.cancelando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    var tab by rememberSaveable { mutableIntStateOf(pestanaInicial) }
    var cancelar by remember {
        mutableStateOf<com.tecsup.lavacampus.data.local.relation.PedidoConDetalle?>(null)
    }
    ConfirmarCancelacion(
        cancelar != null,
        cancelando,
        { cancelar = null },
        {
            cancelar?.let(viewModel::cancelar)
            cancelar = null
        },
    )
    LavaScaffold(
        topBar = {
            Cabecera(
                "Mis pedidos",
                accion = { Button(onClick = onNuevoPedidoClick) { Text("+ Nuevo") } },
            )
        },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionEstudiante(
                TabEstudiante.PEDIDOS,
                onInicioClick,
                {},
                onTarifasClick,
                onPerfilClick,
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            SecondaryTabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
                Tab(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    text = { Text("Activos · ${activos.size}") },
                )
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Historial") })
            }
            val pedidos = if (tab == 0) activos else historial
            if (cargando && pedidos.isEmpty()) CircularProgressIndicator(Modifier.padding(24.dp))
            else if (pedidos.isEmpty())
                EstadoVacio(
                    if (tab == 0) "No tienes pedidos activos" else "Tu historial está vacío",
                    "Cuando hagas un pedido, aquí verás su seguimiento y el casillero para recogerlo.",
                    "Hacer mi primer pedido",
                    onNuevoPedidoClick,
                )
            else
                Column(
                    Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    pedidos.forEach { item ->
                        PedidoCard(
                            item,
                            { onPedidoClick(item.pedido.id) },
                            mostrarAccionCancelar = viewModel.puedeCancelar(item) && !cancelando,
                            onCancelarClick = { cancelar = item },
                            onRepetirClick =
                                if (tab == 1) ({ onRepetirPedido(item.pedido.id) }) else null,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
        }
    }
}
