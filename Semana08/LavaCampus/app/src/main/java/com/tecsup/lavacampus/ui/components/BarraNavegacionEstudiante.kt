package com.tecsup.lavacampus.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable

enum class TabEstudiante {
    INICIO,
    PEDIDOS,
    TARIFAS,
    PERFIL,
}

@Composable
fun BarraNavegacionEstudiante(
    tabSeleccionada: TabEstudiante,
    onInicioClick: () -> Unit,
    onPedidosClick: () -> Unit,
    onTarifasClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {
    PildoraNavegacion(
        tabSeleccionada.ordinal,
        listOf("Inicio", "Pedidos", "Tarifas", "Perfil"),
        listOf(
            Icons.Outlined.Home,
            Icons.AutoMirrored.Outlined.ListAlt,
            Icons.Outlined.LocalOffer,
            Icons.Outlined.Person,
        ),
        listOf(onInicioClick, onPedidosClick, onTarifasClick, onPerfilClick),
    )
}
