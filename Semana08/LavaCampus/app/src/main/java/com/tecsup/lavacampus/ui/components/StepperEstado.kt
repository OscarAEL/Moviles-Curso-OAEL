package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.util.FechaUtils
import com.tecsup.lavacampus.viewmodel.*

enum class StepperOrientacion {
    HORIZONTAL,
    VERTICAL,
}

@Composable
fun StepperEstado(
    estadoStr: String,
    modifier: Modifier = Modifier,
    orientacion: StepperOrientacion = StepperOrientacion.HORIZONTAL,
    fechaRegistro: String? = null,
    colorCompletado: Color = Color.Unspecified,
) {
    val etapas = listOf("Recibido", "En proceso", "Listo", "Entregado")
    val actual = listOf("RECIBIDO", "EN_PROCESO", "LISTO", "ENTREGADO").indexOf(estadoStr)
    val color =
        if (colorCompletado == Color.Unspecified) MaterialTheme.colorScheme.primary
        else colorCompletado
    if (orientacion == StepperOrientacion.HORIZONTAL) {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            etapas.forEachIndexed { i, etapa ->
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Box(
                        Modifier.fillMaxWidth()
                            .height(6.dp)
                            .background(
                                if (i <= actual) color else color.copy(alpha = .23f),
                                RoundedCornerShape(5.dp),
                            )
                    )
                    Text(
                        etapa,
                        fontSize = 9.sp,
                        maxLines = 1,
                        color =
                            if (colorCompletado == Color.Unspecified)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else color,
                        fontWeight = if (i == actual) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    } else
        Column(modifier.fillMaxWidth()) {
            etapas.forEachIndexed { i, etapa ->
                val cancelado = estadoStr == "CANCELADO"
                val completado = i <= actual
                Row(Modifier.fillMaxWidth().heightIn(min = 57.dp)) {
                    Box(Modifier.width(32.dp).height(57.dp)) {
                        if (i < 3)
                            Box(
                                Modifier.width(2.dp)
                                    .height(38.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        if (i < actual) color
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        Surface(
                            Modifier.size(24.dp).align(Alignment.TopCenter),
                            shape = CircleShape,
                            color =
                                if (cancelado && i == 0) MaterialTheme.colorScheme.error
                                else if (completado) color
                                else MaterialTheme.colorScheme.surfaceContainerHighest,
                        ) {
                            if (completado || (cancelado && i == 0))
                                Icon(
                                    if (cancelado) Icons.Default.Close else Icons.Default.Check,
                                    null,
                                    Modifier.padding(4.dp),
                                    tint = Color.White,
                                )
                        }
                    }
                    Column(Modifier.padding(start = 10.dp, bottom = 12.dp)) {
                        Text(
                            if (cancelado && i == 0) "Cancelado" else etapa,
                            fontSize = 14.sp,
                            fontWeight = if (i == actual) FontWeight.Bold else FontWeight.Medium,
                        )
                        Secundario(
                            when {
                                cancelado -> "No completado"
                                i == 0 && fechaRegistro != null ->
                                    FechaUtils.formatearFechaCorta(fechaRegistro)
                                i < actual -> "Completado"
                                i == actual -> "Etapa actual"
                                else -> "Pendiente"
                            }
                        )
                    }
                }
            }
        }
}
