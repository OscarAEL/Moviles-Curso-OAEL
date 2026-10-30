package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun BandejaScreen(
    viewModel: BandejaViewModel,
    filtroInicialStr: String? = null,
    onBackClick: () -> Unit = {},
    onGestionarPedido: (Int) -> Unit,
    onPanelClick: () -> Unit = {},
    onServiciosClick: () -> Unit = {},
    onClientesClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {},
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val filtro by viewModel.filtroSeleccionado.collectAsState()
    val pedidos by viewModel.pedidos.collectAsState()
    val atrasados by viewModel.pedidosAtrasados.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LaunchedEffect(filtroInicialStr) { viewModel.aplicarFiltroNavegacion(filtroInicialStr) }
    LavaScaffold(
        rol = Rol.ADMIN,
        topBar = { Cabecera("Pedidos") },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionAdmin(
                TabAdmin.PEDIDOS,
                onPanelClick,
                {},
                onServiciosClick,
                onClientesClick,
                onAjustesClick,
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LavaTextField(
                busqueda,
                viewModel::actualizarBusqueda,
                "Buscar por estudiante o número",
                leadingIcon = Icons.Outlined.Search,
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FiltroBandeja.entries.toList()) { f ->
                    FilterChip(
                        shape = RoundedCornerShape(24.dp),
                        selected = filtro == f,
                        onClick = { viewModel.actualizarFiltro(f) },
                        label = {
                            Text(
                                when (f.name) {
                                    "TODOS" -> "Todos"
                                    "RECIBIDO" -> "Recibido"
                                    "EN_PROCESO" -> "En proceso"
                                    "LISTO" -> "Listo"
                                    else -> f.name.lowercase()
                                }
                            )
                        },
                    )
                }
            }
            if (cargando && pedidos.isEmpty()) CircularProgressIndicator()
            else if (pedidos.isEmpty())
                EstadoVacio("No hay pedidos", "Prueba otro filtro o búsqueda.")
            else
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    pedidos.forEach { item ->
                        val p = item.pedido
                        Column(
                            Modifier.fillMaxWidth()
                                .clickable { onGestionarPedido(p.id) }
                                .padding(vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(
                                        "Pedido ${numero(p.id)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                    )
                                    Text(
                                        item.usuario.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                    )
                                }
                                EstadoPedidoChip(p.estado)
                            }
                            Secundario(formatearResumenDetalles(item.detalles))
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f).padding(end = 8.dp)) {
                                    Text(dinero(p.total), fontWeight = FontWeight.Bold)
                                    if (p.id in atrasados)
                                        Text(
                                            "Atrasado · entrega ${FechaUtils.formatearFechaCorta(p.fechaEntrega)}",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                        )
                                    else
                                        Secundario(
                                            "Entrega ${FechaUtils.formatearFechaCorta(p.fechaEntrega)}"
                                        )
                                    if (p.estado == "LISTO")
                                        Secundario(
                                            if (p.pagado) "Pago registrado" else "Sin cobrar"
                                        )
                                }
                                val accion =
                                    when (p.estado) {
                                        "RECIBIDO" -> "Iniciar"
                                        "EN_PROCESO" -> "Marcar listo"
                                        "LISTO" -> "Entregar"
                                        else -> null
                                    }
                                accion?.let {
                                    Button(
                                        onClick = { onGestionarPedido(p.id) },
                                        contentPadding =
                                            PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                    ) {
                                        Text(it, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (p.estado == "LISTO" && p.casillero != null)
                                Secundario("Casillero ${p.casillero}")
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
        }
    }
}
