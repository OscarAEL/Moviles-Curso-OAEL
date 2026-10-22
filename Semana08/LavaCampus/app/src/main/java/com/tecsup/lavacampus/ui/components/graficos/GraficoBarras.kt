package com.tecsup.lavacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.DatoGrafico
import java.util.Locale

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    colorBarra: Color = MaterialTheme.colorScheme.primary
) {
    val maxValor = datos.maxOfOrNull { it.valor }?.takeIf { it > 0.0 } ?: 1.0

    val descripcionAccesible = "Gráfico de ingresos por servicio. " +
            datos.joinToString(", ") { "${it.etiqueta}: S/ ${String.format(Locale.US, "%.2f", it.valor)}" }

    if (datos.isEmpty()) {
        Text(
            text = "No hay datos de ingresos por servicio",
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            modifier = modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = descripcionAccesible },
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEachIndexed { index, item ->
            val proporcion = (item.valor / maxValor).coerceIn(0.0, 1.0)
            val alturaCanvas = 100.dp
            val valorFormateado = if (item.valor >= 1000) {
                String.format(Locale.US, "%.0f", item.valor)
            } else if (item.valor % 1.0 == 0.0) {
                String.format(Locale.US, "%.0f", item.valor)
            } else {
                String.format(Locale.US, "%.1f", item.valor)
            }

            val etiquetaBreve = when (item.etiqueta.trim()) {
                "Lavado y secado" -> "Lavado"
                "Lavado en seco" -> "En seco"
                "Edredón / frazada" -> "Edredón"
                "Planchado" -> "Planchado"
                else -> item.etiqueta
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Top value label
                Text(
                    text = valorFormateado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Canvas Bar
                Canvas(
                    modifier = Modifier
                        .width(48.dp)
                        .height(alturaCanvas)
                ) {
                    val w = size.width
                    val h = size.height
                    val barHeight = (h * proporcion).toFloat().coerceAtLeast(4f)
                    val colorVariacion = when (index % 3) {
                        0 -> colorBarra
                        1 -> colorBarra.copy(alpha = 0.65f)
                        else -> colorBarra.copy(alpha = 0.45f)
                    }

                    drawRoundRect(
                        color = colorVariacion,
                        topLeft = Offset(0f, h - barHeight),
                        size = Size(w, barHeight),
                        cornerRadius = CornerRadius(w * 0.2f, w * 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Service Name Label below
                Text(
                    text = etiquetaBreve,
                    fontSize = 11.5.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
