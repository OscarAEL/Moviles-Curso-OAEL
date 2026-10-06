package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.runtime.Composable
import com.saludplus.citas.ui.components.PantallaEnConstruccion

@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String
) {
    PantallaEnConstruccion(
        titulo = "Confirmar cita\nMédico: $medicoId\nFecha: $fecha\nHora: $hora"
    )
}