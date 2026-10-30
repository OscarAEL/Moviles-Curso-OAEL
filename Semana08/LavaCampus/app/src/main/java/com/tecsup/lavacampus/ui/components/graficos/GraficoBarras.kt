package com.tecsup.lavacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.DatoGrafico
import com.tecsup.lavacampus.ui.components.dinero

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    colorBarra: Color = MaterialTheme.colorScheme.primary,
) {
    val maximo = datos.maxOfOrNull { it.valor }?.takeIf { it > 0 } ?: 1.0
    val fondo = MaterialTheme.colorScheme.primaryContainer
    Column(
        modifier.fillMaxWidth().semantics {
            contentDescription =
                "Gráfico de ingresos por servicio. " +
                    datos.joinToString { "${it.etiqueta}: ${dinero(it.valor)}" }
        },
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (datos.isEmpty())
            Text(
                "No hay datos de ingresos por servicio",
                style = MaterialTheme.typography.bodySmall,
            )
        datos.forEach { dato ->
            Row(Modifier.fillMaxWidth()) {
                Text(dato.etiqueta, Modifier.weight(1f), fontSize = 12.sp)
                Text(dinero(dato.valor), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Canvas(Modifier.fillMaxWidth().height(8.dp)) {
                drawRoundRect(fondo, cornerRadius = CornerRadius(6.dp.toPx()))
                if (dato.valor > 0)
                    drawRoundRect(
                        colorBarra,
                        size = size.copy(width = (size.width * dato.valor / maximo).toFloat()),
                        cornerRadius = CornerRadius(6.dp.toPx()),
                    )
            }
        }
    }
}
