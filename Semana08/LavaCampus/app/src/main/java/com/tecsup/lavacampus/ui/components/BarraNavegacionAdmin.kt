package com.tecsup.lavacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class TabAdmin {
    PANEL, PEDIDOS, SERVICIOS, CLIENTES, AJUSTES
}

@Composable
fun BarraNavegacionAdmin(
    tabSeleccionada: TabAdmin,
    onPanelClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onServiciosClick: () -> Unit,
    onClientesClick: () -> Unit,
    onAjustesClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = tabSeleccionada == TabAdmin.PANEL,
            onClick = onPanelClick,
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = "Panel") },
            label = { Text("Panel", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabAdmin.PANEL) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsAdmin()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabAdmin.PEDIDOS,
            onClick = onPedidosClick,
            icon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, contentDescription = "Pedidos") },
            label = { Text("Pedidos", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabAdmin.PEDIDOS) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsAdmin()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabAdmin.SERVICIOS,
            onClick = onServiciosClick,
            icon = { Icon(Icons.Outlined.LocalOffer, contentDescription = "Servicios") },
            label = { Text("Servicios", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabAdmin.SERVICIOS) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsAdmin()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabAdmin.CLIENTES,
            onClick = onClientesClick,
            icon = { Icon(Icons.Outlined.People, contentDescription = "Clientes") },
            label = { Text("Clientes", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabAdmin.CLIENTES) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsAdmin()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabAdmin.AJUSTES,
            onClick = onAjustesClick,
            icon = { Icon(Icons.Outlined.Settings, contentDescription = "Ajustes") },
            label = { Text("Ajustes", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabAdmin.AJUSTES) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsAdmin()
        )
    }
}

@Composable
private fun itemColorsAdmin() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = Color(0xFF9CA3AF),
    unselectedTextColor = Color(0xFF9CA3AF),
    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
)
