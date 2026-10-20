package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido

enum class StepperOrientacion {
    HORIZONTAL,
    VERTICAL
}

@Composable
fun StepperEstado(
    estadoStr: String,
    modifier: Modifier = Modifier,
    orientacion: StepperOrientacion = StepperOrientacion.HORIZONTAL
) {
    val etapas = listOf("Recibido", "En proceso", "Listo", "Entregado")
    val indiceActual = when (estadoStr) {
        EstadoPedido.RECIBIDO.name -> 0
        EstadoPedido.EN_PROCESO.name -> 1
        EstadoPedido.LISTO.name -> 2
        EstadoPedido.ENTREGADO.name -> 3
        else -> -1
    }

    val colorCompletado = Color(0xFF0891B2)
    val colorInactivo = Color(0xFFD1D5DB)
    val colorTextoInactivo = Color(0xFF6B7280)

    if (orientacion == StepperOrientacion.HORIZONTAL) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                contentAlignment = Alignment.Center
            ) {
                // Connecting lines
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    val w = size.width
                    val cy = size.height / 2f
                    val stepWidth = w / (etapas.size - 1)

                    for (i in 0 until etapas.size - 1) {
                        val x1 = i * stepWidth
                        val x2 = (i + 1) * stepWidth
                        val esCancelado = (estadoStr == EstadoPedido.CANCELADO.name)
                        val lineAlcanzada = !esCancelado && (i < indiceActual)
                        val lineColor = if (lineAlcanzada) colorCompletado else colorInactivo

                        drawLine(
                            color = lineColor,
                            start = Offset(x1, cy),
                            end = Offset(x2, cy),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }

                // Step Circle Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    etapas.forEachIndexed { index, _ ->
                        val esCancelado = (estadoStr == EstadoPedido.CANCELADO.name)
                        val etapaAlcanzada = !esCancelado && (index <= indiceActual)
                        val esActual = !esCancelado && (index == indiceActual)

                        Box(
                            modifier = Modifier.size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (esCancelado && index == 0) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color(0xFFDC2626),
                                        radius = size.minDimension / 2f
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancelado",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else if (etapaAlcanzada) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = colorCompletado,
                                        radius = size.minDimension / 2f
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Alcanzado",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.White,
                                        radius = size.minDimension / 2f
                                    )
                                    drawCircle(
                                        color = colorInactivo,
                                        radius = size.minDimension / 2f,
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                etapas.forEachIndexed { index, titulo ->
                    val esActual = (index == indiceActual)
                    Text(
                        text = if (estadoStr == EstadoPedido.CANCELADO.name && index == 0) "Cancelado" else titulo,
                        fontSize = 10.sp,
                        fontWeight = if (esActual) FontWeight.Bold else FontWeight.Medium,
                        color = if (esActual) Color(0xFF1E1B4B) else colorTextoInactivo,
                        textAlign = when (index) {
                            0 -> TextAlign.Start
                            etapas.lastIndex -> TextAlign.End
                            else -> TextAlign.Center
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
