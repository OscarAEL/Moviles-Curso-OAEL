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
fun ServiciosScreen(
    viewModel: ServiciosViewModel,
    onNuevoServicio: () -> Unit,
    onEditarServicio: (Int) -> Unit,
    onBackClick: () -> Unit = {},
    onPanelClick: () -> Unit = {},
    onPedidosClick: () -> Unit = {},
    onClientesClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {},
) {
    val servicios by viewModel.servicios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(
        rol = Rol.ADMIN,
        topBar = {
            Cabecera(
                "Servicios",
                accion = { Button(onClick = onNuevoServicio) { Text("+ Nuevo") } },
            )
        },
        snackbarHost = { SnackbarHost(host) },
        bottomBar = {
            BarraNavegacionAdmin(
                TabAdmin.SERVICIOS,
                onPanelClick,
                onPedidosClick,
                {},
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (cargando) CircularProgressIndicator()
            if (!cargando && servicios.isEmpty())
                EstadoVacio("Todavía no hay servicios", "Usa + Nuevo para crear uno.")
            servicios.forEach { s ->
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconoServicio(
                            s.icono,
                            Modifier.size(24.dp),
                            MaterialTheme.colorScheme.primary,
                        )
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                        Text(s.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Secundario(
                            "${dinero(s.precio)} por ${s.unidad.lowercase()} · ${s.horasEntrega} h"
                        )
                    }
                    IconButton(
                        onClick = { onEditarServicio(s.id) },
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            "Editar servicio",
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Switch(
                        checked = s.activo,
                        onCheckedChange = { viewModel.alternar(s) },
                        enabled = !guardando,
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Aviso(
                "Un servicio inactivo no aparece al crear pedidos. Si ya está en algún pedido, solo se puede desactivar, no eliminar."
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
