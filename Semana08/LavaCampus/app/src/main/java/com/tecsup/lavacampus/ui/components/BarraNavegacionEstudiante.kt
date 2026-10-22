package com.tecsup.lavacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
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

enum class TabEstudiante {
    INICIO, PEDIDOS, TARIFAS, PERFIL
}

@Composable
fun BarraNavegacionEstudiante(
    tabSeleccionada: TabEstudiante,
    onInicioClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = tabSeleccionada == TabEstudiante.INICIO,
            onClick = onInicioClick,
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabEstudiante.INICIO) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsEstudiante()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabEstudiante.PEDIDOS,
            onClick = onPedidosClick,
            icon = { Icon(Icons.AutoMirrored.Outlined.ListAlt, contentDescription = "Pedidos") },
            label = { Text("Pedidos", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabEstudiante.PEDIDOS) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsEstudiante()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabEstudiante.TARIFAS,
            onClick = onTarifasClick,
            icon = { Icon(Icons.Outlined.LocalOffer, contentDescription = "Tarifas") },
            label = { Text("Tarifas", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabEstudiante.TARIFAS) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsEstudiante()
        )
        NavigationBarItem(
            selected = tabSeleccionada == TabEstudiante.PERFIL,
            onClick = onPerfilClick,
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp, fontWeight = if (tabSeleccionada == TabEstudiante.PERFIL) FontWeight.Bold else FontWeight.Normal) },
            colors = itemColorsEstudiante()
        )
    }
}

@Composable
private fun itemColorsEstudiante() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = Color(0xFF9CA3AF),
    unselectedTextColor = Color(0xFF9CA3AF),
    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
)
