package com.eneque.avisoscampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import com.eneque.avisoscampus.ui.theme.*

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    TarjetaFuncion("Historial") {
        if (avisos.isEmpty()) {
            Text(
                "Sin avisos todavía",
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                avisos.take(20).forEach { aviso ->
                    TarjetaItemAviso(aviso)
                }
            }
        }
    }
}

@Composable
private fun TarjetaItemAviso(aviso: Aviso) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SubtarjetaAviso,
        border = BorderStroke(1.dp, MicroBorde)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipOrigen(aviso.origen)
                Text(
                    text = aviso.hora,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )
            }
            Text(
                text = aviso.titulo,
                style = MaterialTheme.typography.titleSmall,
                color = TextoPrincipal,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = aviso.texto,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun ChipOrigen(origen: Origen) {
    val (fondo, texto, dot) = when (origen) {
        Origen.BOTON -> Triple(ChipBotonFondo, ChipBotonTexto, ChipBotonDot)
        Origen.RECORDATORIO -> Triple(ChipRecordatorioFondo, ChipRecordatorioTexto, ChipRecordatorioDot)
        Origen.MODO_AVION -> Triple(ChipModoAvionFondo, ChipModoAvionTexto, ChipModoAvionDot)
    }

    Surface(
        color = fondo,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, dot.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dot)
            )
            Text(
                text = origen.texto,
                style = MaterialTheme.typography.labelMedium,
                color = texto,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
