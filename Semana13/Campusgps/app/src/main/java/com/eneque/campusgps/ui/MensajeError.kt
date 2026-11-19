package com.eneque.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.eneque.campusgps.model.TipoError
import com.eneque.campusgps.model.UbicacionUi

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit,
    onAbrirAjustes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(error.mensaje, color = MaterialTheme.colorScheme.error)
        when (error.tipo) {
            TipoError.SIN_PERMISO -> Button(onClick = onAbrirAjustes) {
                Text("Abrir ajustes")
            }
            TipoError.GPS_APAGADO, TipoError.OTRO -> Button(onClick = onReintentar) {
                Text("Reintentar")
            }
            TipoError.SIN_PLAY_SERVICES -> Unit
        }
    }
}
