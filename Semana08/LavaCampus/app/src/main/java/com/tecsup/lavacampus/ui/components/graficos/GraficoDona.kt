package com.tecsup.lavacampus.ui.components.graficos

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.DatoGrafico
import com.tecsup.lavacampus.ui.theme.EstadoCancelado
import com.tecsup.lavacampus.ui.theme.EstadoEntregado
import com.tecsup.lavacampus.ui.theme.EstadoEnProceso
import com.tecsup.lavacampus.ui.theme.EstadoListo
import com.tecsup.lavacampus.ui.theme.EstadoRecibido
import java.util.Locale

@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    colores: List<Color> = listOf(
        EstadoRecibido,
        EstadoEnProceso,
        EstadoListo,
        EstadoEntregado,
        EstadoCancelado
    )
) {
    val total = datos.sumOf { it.valor }
    val totalInt = total.toInt()

    val descripcionAccesible = "Gráfico de pedidos por estado. Total: $totalInt pedidos. " +
            datos.joinToString(", ") { "${it.etiqueta}: ${it.valor.toInt()}" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = descripcionAccesible },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Donut Canvas + Center Text Box
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(130.dp)) {
                val strokeWidth = 22.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                if (total <= 0.0) {
                    drawArc(
                        color = Color(0xFFE2E8F0),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    var currentStartAngle = -90f
                    datos.forEachIndexed { index, item ->
                        if (item.valor > 0.0) {
                            val sweepAngle = ((item.valor / total) * 360f).toFloat()
                            val color = colores.getOrElse(index) { Color.Gray }

                            drawArc(
                                color = color,
                                startAngle = currentStartAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeftOffset,
                                size = arcSize,
                                style = Stroke(width = strokeWidth)
                            )
                            currentStartAngle += sweepAngle
                        }
                    }
                }
            }

            // Center Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalInt.toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "pedidos",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Legend on Right
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            datos.forEachIndexed { index, item ->
                val color = colores.getOrElse(index) { Color.Gray }
                val cantidadStr = String.format(Locale.US, "%.0f", item.valor)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.etiqueta,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = cantidadStr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
