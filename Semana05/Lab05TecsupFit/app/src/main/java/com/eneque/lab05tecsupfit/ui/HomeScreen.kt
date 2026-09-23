package com.eneque.lab05tecsupfit.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05tecsupfit.model.ClaseFit
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.GreenLightIconBg
import com.eneque.lab05tecsupfit.ui.theme.GreenSelectedFilter
import com.eneque.lab05tecsupfit.ui.theme.LightGreyCardBg
import com.eneque.lab05tecsupfit.ui.theme.TextDark
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

@Composable
fun HomeScreen() {
    // Estado simple para el filtro seleccionado ("Hoy" por defecto)
    var filtroSeleccionado by remember { mutableStateOf("Hoy") }

    // Lista de clases solicitadas
    val listaClases = remember {
        listOf(
            ClaseFit(id = 1, nombre = "Yoga funcional", horario = "7:00 am · Sala 2", filtro = "Hoy"),
            ClaseFit(id = 2, nombre = "Cross Training", horario = "6:00 pm · Sala 1", filtro = "Hoy"),
            ClaseFit(id = 3, nombre = "Spinning", horario = "7:30 pm · Sala 3", filtro = "Hoy")
        )
    }

    // Filtrar clases: "Hoy" muestra las 3 clases, "Esta semana" muestra las clases filtradas
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
            // Cabecera superior compacta de color verde oscuro (#0F7A5C)
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

            // Sección de filtros horizontales compactos
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
                    ClaseCard(clase = clase)
                }
            }
        }
    }
}

@Composable
fun ClaseCard(clase: ClaseFit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = LightGreyCardBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recuadro verde claro (#D9F3EA) con símbolo de gimnasio
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(GreenLightIconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏋",
                    fontSize = 18.sp
                )
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
                    text = clase.horario,
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
