package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun IconoServicio(
    icono: String,
    modifier: Modifier = Modifier.size(32.dp),
    color: Color = Color.Unspecified
) {
    val strokeWidth = 2.5f

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val drawColor = if (color == Color.Unspecified) Color(0xFF0891B2) else color
        val stroke = Stroke(width = strokeWidth * (w / 32f))

        when (icono.lowercase()) {
            "wash" -> {
                // Outer body
                drawRoundRect(
                    color = drawColor,
                    topLeft = Offset(w * 0.15f, h * 0.1f),
                    size = Size(w * 0.7f, h * 0.8f),
                    cornerRadius = CornerRadius(w * 0.1f, h * 0.1f),
                    style = stroke
                )
                // Top line for control panel
                drawLine(
                    color = drawColor,
                    start = Offset(w * 0.15f, h * 0.3f),
                    end = Offset(w * 0.85f, h * 0.3f),
                    strokeWidth = stroke.width
                )
                // Dial / button
                drawCircle(
                    color = drawColor,
                    radius = w * 0.05f,
                    center = Offset(w * 0.35f, h * 0.2f),
                    style = stroke
                )
                drawCircle(
                    color = drawColor,
                    radius = w * 0.03f,
                    center = Offset(w * 0.65f, h * 0.2f),
                    style = stroke
                )
                // Door outer
                drawCircle(
                    color = drawColor,
                    radius = w * 0.22f,
                    center = Offset(w * 0.5f, h * 0.6f),
                    style = stroke
                )
                // Inner circle
                drawCircle(
                    color = drawColor,
                    radius = w * 0.14f,
                    center = Offset(w * 0.5f, h * 0.6f),
                    style = stroke
                )
            }
            "iron" -> {
                val path = Path().apply {
                    moveTo(w * 0.25f, h * 0.3f)
                    lineTo(w * 0.7f, h * 0.3f)
                    lineTo(w * 0.7f, h * 0.5f)
                    lineTo(w * 0.85f, h * 0.5f)
                    lineTo(w * 0.85f, h * 0.75f)
                    lineTo(w * 0.15f, h * 0.75f)
                    quadraticTo(w * 0.25f, h * 0.55f, w * 0.5f, h * 0.5f)
                    lineTo(w * 0.25f, h * 0.5f)
                    close()
                }
                drawPath(path = path, color = drawColor, style = stroke)
                drawLine(
                    color = drawColor,
                    start = Offset(w * 0.1f, h * 0.82f),
                    end = Offset(w * 0.9f, h * 0.82f),
                    strokeWidth = stroke.width
                )
            }
            "shirt", "t-shirt" -> {
                val path = Path().apply {
                    moveTo(w * 0.35f, h * 0.15f)
                    lineTo(w * 0.5f, h * 0.28f)
                    lineTo(w * 0.65f, h * 0.15f)
                    lineTo(w * 0.85f, h * 0.25f)
                    lineTo(w * 0.75f, h * 0.42f)
                    lineTo(w * 0.68f, h * 0.38f)
                    lineTo(w * 0.68f, h * 0.85f)
                    lineTo(w * 0.32f, h * 0.85f)
                    lineTo(w * 0.32f, h * 0.38f)
                    lineTo(w * 0.25f, h * 0.42f)
                    lineTo(w * 0.15f, h * 0.25f)
                    close()
                }
                drawPath(path = path, color = drawColor, style = stroke)
            }
            else -> {
                drawCircle(
                    color = drawColor,
                    radius = w * 0.35f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = stroke
                )
            }
        }
    }
}
