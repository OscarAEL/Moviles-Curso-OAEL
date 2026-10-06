package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.saludplus.citas.ui.components.PantallaEnConstruccion

@Composable
fun FechaHoraScreen(
    medicoId: Int
) {
    PantallaEnConstruccion(
        titulo = "Fecha y hora - Médico $medicoId"
    )
}