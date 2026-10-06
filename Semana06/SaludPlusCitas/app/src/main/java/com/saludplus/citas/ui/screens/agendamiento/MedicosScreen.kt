package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.saludplus.citas.ui.components.PantallaEnConstruccion

@Composable
fun MedicosScreen(
    especialidadId: Int
) {
    PantallaEnConstruccion(
        titulo = "Médicos - Especialidad $especialidadId"
    )
}