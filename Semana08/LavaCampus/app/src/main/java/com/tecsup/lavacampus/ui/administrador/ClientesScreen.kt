package com.tecsup.lavacampus.ui.administrador

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onBackClick: () -> Unit,
    onPanelClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onServiciosClick: () -> Unit,
    onAjustesClick: () -> Unit,
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(
        rol = Rol.ADMIN,
        topBar = { Cabecera("Clientes") },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionAdmin(
                TabAdmin.CLIENTES,
                onPanelClick,
                onPedidosClick,
                onServiciosClick,
                {},
                onAjustesClick,
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            LavaTextField(
                busqueda,
                viewModel::actualizarBusqueda,
                "Buscar estudiante",
                leadingIcon = Icons.Outlined.Search,
            )
            if (cargando && clientes.isEmpty()) CircularProgressIndicator()
            else if (clientes.isEmpty())
                EstadoVacio("No se encontraron clientes", "Prueba otra búsqueda.")
            else
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    clientes.forEach { c ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(c.nombre)
                            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                                Text(c.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Secundario("Código ${c.codigo}")
                                Secundario("${c.cantidadPedidos} pedidos")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    dinero(c.totalGastado),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                                Secundario("gastado")
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    Secundario(
                        "Pedidos sin contar cancelados · gasto solo en pedidos cobrados",
                        Modifier.padding(vertical = 20.dp),
                    )
                }
        }
    }
}
