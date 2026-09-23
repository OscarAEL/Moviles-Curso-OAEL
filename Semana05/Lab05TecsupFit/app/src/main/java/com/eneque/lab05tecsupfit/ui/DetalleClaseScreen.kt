package com.eneque.lab05tecsupfit.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05tecsupfit.model.ClaseRepository
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.GreenLightIconBg
import com.eneque.lab05tecsupfit.ui.theme.TextDark
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

@Composable
fun DetalleClaseScreen(
    claseId: Int,
    onBackClick: () -> Unit = {},
    onReservarClick: () -> Unit = {}
) {
    val clase = ClaseRepository.getClaseById(claseId)

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            // Botón verde oscuro cerca de la parte inferior con padding de la barra de navegación del sistema
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = onReservarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenDarkHeader,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Reservar cupo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Encabezado blanco con flecha de regreso y título
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBackClick() }
                        .padding(end = 12.dp)
                )
                Text(
                    text = "Detalle de clase",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (clase != null) {
                // Recuadro grande con esquinas redondeadas y fondo verde claro (#D9F3EA)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GreenLightIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    DumbbellLargeIcon()
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nombre de la clase
                Text(
                    text = clase.nombre,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Horario · Sala · Duración en una sola línea
                Text(
                    text = "${clase.horario} · ${clase.sala} · ${clase.duracion}",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = clase.descripcion,
                    fontSize = 14.sp,
                    color = TextDark,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cupos disponibles
                Text(
                    text = "${clase.cuposDisponibles} cupos disponibles",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
            } else {
                Text(
                    text = "Clase no encontrada",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun DumbbellLargeIcon(
    modifier: Modifier = Modifier,
    color: Color = GreenDarkHeader
) {
    Canvas(modifier = modifier.size(80.dp)) {
        val w = size.width
        val h = size.height

        // Barra central de la mancuerna
        drawLine(
            color = color,
            start = Offset(w * 0.18f, h * 0.5f),
            end = Offset(w * 0.82f, h * 0.5f),
            strokeWidth = h * 0.14f,
            cap = StrokeCap.Round
        )

        // Disco interno izquierdo
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.18f, h * 0.22f),
            size = Size(w * 0.1f, h * 0.56f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Disco externo izquierdo
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.08f, h * 0.28f),
            size = Size(w * 0.08f, h * 0.44f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // Disco interno derecho
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.72f, h * 0.22f),
            size = Size(w * 0.1f, h * 0.56f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        // Disco externo derecho
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.84f, h * 0.28f),
            size = Size(w * 0.08f, h * 0.44f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleClaseScreenPreview() {
    DetalleClaseScreen(claseId = 2)
}
