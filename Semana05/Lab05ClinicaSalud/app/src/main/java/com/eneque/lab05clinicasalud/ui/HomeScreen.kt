package com.eneque.lab05clinicasalud.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05clinicasalud.model.Medico
import com.eneque.lab05clinicasalud.model.listaMedicos
import com.eneque.lab05clinicasalud.ui.components.AppDrawerContent
import com.eneque.lab05clinicasalud.ui.theme.GraySubtitle
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleCardBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleCircleIconBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import com.eneque.lab05clinicasalud.ui.theme.PurpleLightChipBg
import com.eneque.lab05clinicasalud.ui.theme.StarYellow
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onMedicoClick: (Int) -> Unit = {},
    onMisCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {}
) {
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val medicosFiltrados = remember(filtroSeleccionado) {
        if (filtroSeleccionado == "Todos") {
            listaMedicos
        } else {
            listaMedicos.filter { it.especialidad == filtroSeleccionado }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                opcionSeleccionada = "Inicio",
                onInicioClick = {
                    scope.launch { drawerState.close() }
                },
                onMisCitasClick = {
                    scope.launch { drawerState.close() }
                    onMisCitasClick()
                },
                onHistorialClick = {
                    scope.launch { drawerState.close() }
                    onHistorialClick()
                },
                onPerfilClick = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // Cabecera rectangular morado oscuro (#6A2996) con botón hamburguesa ☰
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PurpleDarkHeader)
                        .padding(
                            top = innerPadding.calculateTopPadding() + 16.dp,
                            bottom = 20.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Clínica Salud+",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hola, Oscar",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 15.sp
                            )
                        }

                        // Icono hamburguesa ☰ para abrir el drawer
                        Text(
                            text = "☰",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    scope.launch { drawerState.open() }
                                }
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtros de especialidad
                val opcionesFiltro = listOf("Todos", "Cardiología", "Pediatría", "Dermatología")

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(opcionesFiltro) { opcion ->
                        val esSeleccionado = opcion == filtroSeleccionado
                        Surface(
                            onClick = { filtroSeleccionado = opcion },
                            shape = RoundedCornerShape(20.dp),
                            color = if (esSeleccionado) PurpleDarkHeader else PurpleLightChipBg,
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = opcion,
                                    color = if (esSeleccionado) Color.White else Color(0xFF333333),
                                    fontSize = 14.sp,
                                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título "Médicos disponibles"
                Text(
                    text = "Médicos disponibles",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista de médicos
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = medicosFiltrados,
                        key = { it.id }
                    ) { medico ->
                        MedicoItem(
                            medico = medico,
                            onClick = { onMedicoClick(medico.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicoItem(
    medico: Medico,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleCardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Círculo lila claro con símbolo "+" grande de color morado en el centro
            Surface(
                shape = CircleShape,
                color = PurpleCircleIconBg,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = PurpleDarkHeader,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Parte central: Nombre del médico y especialidad
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = medico.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = medico.especialidad,
                    fontSize = 13.sp,
                    color = GraySubtitle
                )
            }

            // Lado derecho: Estrella amarilla y calificación
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "★",
                    color = StarYellow,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = medico.calificacion.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Lab05ClinicaSaludTheme {
        HomeScreen()
    }
}
