package com.saludplus.citas.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Componente reutilizable para el encabezado superior con botón para retroceder.
@Composable
fun EncabezadoConAtras(
    titulo: String, // Título de la pantalla activa.
    onAtrasClick: () -> Unit // Callback ejecutado al presionar la flecha Atrás (navController.popBackStack()).
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Botón con icono de flecha hacia atrás
        IconButton(
            onClick = onAtrasClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver"
            )
        }

        // Título principal del encabezado
        Text(
            text = titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
