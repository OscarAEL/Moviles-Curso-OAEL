package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    val colores = MaterialTheme.colorScheme
    Card(
        modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, colores.error.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = colores.errorContainer)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconoCampus(IconoCampus.Aviso, color = colores.error)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(when (error.tipo) {
                        TipoError.SIN_PERMISO -> "Permiso de ubicación"
                        TipoError.GPS_APAGADO -> "GPS desactivado"
                        TipoError.SIN_PLAY_SERVICES -> "Servicios no disponibles"
                        TipoError.OTRO -> "Ubicación no disponible"
                    }, style = MaterialTheme.typography.titleSmall, color = colores.error)
                    Text(error.mensaje, style = MaterialTheme.typography.bodySmall,
                        color = colores.onErrorContainer)
                }
            }
            val accion = when (error.tipo) {
                TipoError.SIN_PERMISO -> onAbrirAjustes
                TipoError.GPS_APAGADO, TipoError.OTRO -> onReintentar
                TipoError.SIN_PLAY_SERVICES -> null
            }
            if (accion != null) Button(
                onClick = accion,
                colors = ButtonDefaults.buttonColors(containerColor = colores.error,
                    contentColor = colores.onError),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (error.tipo == TipoError.SIN_PERMISO) "Abrir ajustes" else "Reintentar")
            }
        }
    }
}
