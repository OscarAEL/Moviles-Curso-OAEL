package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun HomeScreen(
    onAgendarCitaClick: () -> Unit,
    onMisCitasClick: () -> Unit,
    onEspecialidadClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {

    val usuario = Repositorio.usuarioActual
    val especialidadesDestacadas = Repositorio.especialidadesDestacadas()

    Scaffold(
        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = true,
                    onClick = onInicioClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Inicio"
                        )
                    },
                    label = {
                        Text("Inicio")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onCitasClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Citas"
                        )
                    },
                    label = {
                        Text("Citas")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onResultadosClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Resultados"
                        )
                    },
                    label = {
                        Text("Resultados")
                    }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onPerfilClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    },
                    label = {
                        Text("Perfil")
                    }
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "¡Hola, ${usuario?.nombre ?: "Paciente"}!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "¿Qué deseas hacer hoy?",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onAgendarCitaClick()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Agendar cita",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Reserva una atención médica"
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onMisCitasClick()
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Mis citas",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Revisa tus citas"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Especialidades destacadas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(especialidadesDestacadas) { especialidad ->

                    Card(
                        modifier = Modifier.clickable {
                            onEspecialidadClick(especialidad.id)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = especialidad.nombre,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = especialidad.descripcion,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}