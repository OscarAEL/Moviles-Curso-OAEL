package com.eneque.campusgps.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp

enum class IconoCampus { Ubicacion, Estudio, Comida, Eventos, Servicios, Ruta, Check, Info, Aviso }

fun iconoCategoria(categoria: String): IconoCampus = when (categoria) {
    "Estudio" -> IconoCampus.Estudio
    "Comida" -> IconoCampus.Comida
    "Eventos" -> IconoCampus.Eventos
    else -> IconoCampus.Servicios
}

/** Iconos vectoriales locales; la etiqueta accesible la proporciona el control que los contiene. */
@Composable
fun IconoCampus(
    tipo: IconoCampus,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current
) {
    Canvas(modifier.size(20.dp)) {
        scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) {
            val trazo = Stroke(1.8f, cap = StrokeCap.Round)
            fun linea(x1: Float, y1: Float, x2: Float, y2: Float) =
                drawLine(color, Offset(x1, y1), Offset(x2, y2), 1.8f, StrokeCap.Round)
            when (tipo) {
                IconoCampus.Ubicacion -> {
                    drawCircle(color, 7f, Offset(12f, 12f), style = trazo)
                    drawCircle(color, 3f, Offset(12f, 12f), style = trazo)
                    linea(12f, 1f, 12f, 5f); linea(12f, 19f, 12f, 23f)
                    linea(1f, 12f, 5f, 12f); linea(19f, 12f, 23f, 12f)
                }
                IconoCampus.Estudio -> {
                    val libro = Path().apply {
                        moveTo(12f, 5f); quadraticTo(7f, 2f, 2f, 5f)
                        lineTo(2f, 20f); quadraticTo(7f, 17f, 12f, 20f)
                        quadraticTo(17f, 17f, 22f, 20f)
                        lineTo(22f, 5f); quadraticTo(17f, 2f, 12f, 5f); close()
                    }
                    drawPath(libro, color, style = trazo); linea(12f, 5f, 12f, 20f)
                }
                IconoCampus.Comida -> {
                    linea(5f, 3f, 5f, 21f); linea(2f, 3f, 2f, 9f)
                    linea(8f, 3f, 8f, 9f); linea(2f, 9f, 8f, 9f)
                    val cuchillo = Path().apply {
                        moveTo(18f, 3f); quadraticTo(12f, 7f, 15f, 13f)
                        lineTo(19f, 13f); moveTo(19f, 3f); lineTo(19f, 21f)
                    }
                    drawPath(cuchillo, color, style = trazo)
                }
                IconoCampus.Eventos -> {
                    val calendario = Path().apply {
                        moveTo(3f, 5f); lineTo(21f, 5f); lineTo(21f, 21f)
                        lineTo(3f, 21f); close()
                    }
                    drawPath(calendario, color, style = trazo)
                    linea(3f, 10f, 21f, 10f); linea(8f, 2f, 8f, 7f)
                    linea(16f, 2f, 16f, 7f); linea(8f, 15f, 10f, 15f)
                    linea(14f, 15f, 16f, 15f)
                }
                IconoCampus.Servicios -> {
                    val parking = Path().apply {
                        moveTo(7f, 21f); lineTo(7f, 3f); lineTo(14f, 3f)
                        cubicTo(22f, 3f, 22f, 13f, 14f, 13f); lineTo(7f, 13f)
                    }
                    drawPath(parking, color, style = trazo)
                }
                IconoCampus.Ruta -> {
                    drawCircle(color, 2.5f, Offset(6f, 18f), style = trazo)
                    drawCircle(color, 2.5f, Offset(18f, 6f), style = trazo)
                    val ruta = Path().apply {
                        moveTo(9f, 18f); lineTo(16f, 18f)
                        cubicTo(23f, 18f, 22f, 12f, 16f, 12f)
                        lineTo(8f, 12f); cubicTo(2f, 12f, 2f, 6f, 8f, 6f)
                        lineTo(15f, 6f)
                    }
                    drawPath(ruta, color, style = trazo)
                }
                IconoCampus.Check -> { linea(4f, 12f, 9f, 17f); linea(9f, 17f, 21f, 5f) }
                IconoCampus.Info -> {
                    drawCircle(color, 9f, Offset(12f, 12f), style = trazo)
                    drawCircle(color, 1f, Offset(12f, 7f))
                    linea(12f, 11f, 12f, 17f)
                }
                IconoCampus.Aviso -> {
                    val aviso = Path().apply {
                        moveTo(12f, 2f); lineTo(23f, 21f); lineTo(1f, 21f); close()
                    }
                    drawPath(aviso, color, style = trazo)
                    linea(12f, 8f, 12f, 13f)
                    drawCircle(color, 1f, Offset(12f, 17f))
                }
            }
        }
    }
}
