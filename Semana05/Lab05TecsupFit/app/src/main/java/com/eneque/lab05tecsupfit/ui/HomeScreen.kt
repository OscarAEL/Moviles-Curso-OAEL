package com.eneque.lab05tecsupfit.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.eneque.lab05tecsupfit.model.ClaseFit
import com.eneque.lab05tecsupfit.model.ClaseRepository
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.GreenLightIconBg
import com.eneque.lab05tecsupfit.ui.theme.GreenSelectedFilter
import com.eneque.lab05tecsupfit.ui.theme.LightGreyCardBg
import com.eneque.lab05tecsupfit.ui.theme.TextDark
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onClaseClick: (Int) -> Unit = {}
) {
    // Estado simple para el filtro seleccionado ("Hoy" por defecto)
    var filtroSeleccionado by remember { mutableStateOf("Hoy") }

    // Obtener las clases del repositorio
    val listaClases = remember { ClaseRepository.listaClases }

    // Filtrar clases según la opción seleccionada
    val clasesFiltradas = if (filtroSeleccionado == "Hoy") {
        listaClases
    } else {
        listOf(listaClases[1], listaClases[2])
    }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera superior compacta de color verde oscuro
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenDarkHeader)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Text(
                        text = "TECSUP Fit",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Hola, Oscar",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección de filtros horizontales
            val filtros = listOf("Hoy", "Esta semana")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtros) { filtro ->
                    val isSelected = (filtro == filtroSeleccionado)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { filtroSeleccionado = filtro },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) GreenSelectedFilter else LightGreyCardBg
                    ) {
                        Text(
                            text = filtro,
                            color = if (isSelected) Color.White else TextDark,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Título "Clases disponibles"
            Text(
                text = "Clases disponibles",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista compacta de clases
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(clasesFiltradas) { clase ->
                    ClaseCard(
                        clase = clase,
                        onClick = { onClaseClick(clase.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClaseCard(
    clase: ClaseFit,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LightGreyCardBg,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recuadro verde claro (#D9F3EA) con icono de mancuerna/pesa dibujado
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(GreenLightIconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                DumbbellIcon()
            }

            // Nombre y horario con sala
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = clase.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${clase.horario} · ${clase.sala}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Indicador visual en el lado derecho
            Text(
                text = ">",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun DumbbellIcon(
    modifier: Modifier = Modifier,
    color: Color = GreenDarkHeader
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height

        // Barra central de la mancuerna
        drawLine(
            color = color,
            start = Offset(w * 0.2f, h * 0.5f),
            end = Offset(w * 0.8f, h * 0.5f),
            strokeWidth = h * 0.16f,
            cap = StrokeCap.Round
        )

        // Disco interno izquierdo
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.18f, h * 0.22f),
            size = Size(w * 0.1f, h * 0.56f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Disco externo izquierdo
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.08f, h * 0.3f),
            size = Size(w * 0.08f, h * 0.4f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Disco interno derecho
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.72f, h * 0.22f),
            size = Size(w * 0.1f, h * 0.56f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Disco externo derecho
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.84f, h * 0.3f),
            size = Size(w * 0.08f, h * 0.4f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
