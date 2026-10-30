package com.tecsup.lavacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable

enum class TabAdmin {
    PANEL,
    PEDIDOS,
    SERVICIOS,
    CLIENTES,
    AJUSTES,
}

@Composable
fun BarraNavegacionAdmin(
    tabSeleccionada: TabAdmin,
    onPanelClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onServiciosClick: () -> Unit,
    onClientesClick: () -> Unit,
    onAjustesClick: () -> Unit,
) {
    PildoraNavegacion(
        tabSeleccionada.ordinal,
        listOf("Panel", "Pedidos", "Servicios", "Clientes", "Ajustes"),
        listOf(
            Icons.Outlined.Dashboard,
            Icons.AutoMirrored.Outlined.ListAlt,
            Icons.Outlined.LocalLaundryService,
            Icons.Outlined.People,
            Icons.Outlined.Tune,
        ),
        listOf(onPanelClick, onPedidosClick, onServiciosClick, onClientesClick, onAjustesClick),
    )
}
