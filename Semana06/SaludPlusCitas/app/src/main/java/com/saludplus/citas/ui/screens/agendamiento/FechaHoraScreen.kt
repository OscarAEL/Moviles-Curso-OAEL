package com.saludplus.citas.ui.screens.agendamiento

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onContinuarClick: (String, String) -> Unit
) {

    val medico = Repositorio.obtenerMedico(medicoId)

    // En la Fase 1 los días son fijos.
    val dias = listOf(
        "2026-09-15",
        "2026-09-16",
        "2026-09-17",
        "2026-09-18",
        "2026-09-19"
    )

    var fechaSeleccionada by remember {
        mutableStateOf("")
    }

    var horaSeleccionada by remember {
        mutableStateOf("")
    }

    val horariosDisponibles =
        if (fechaSeleccionada.isNotEmpty()) {
            Repositorio.horariosDisponibles(
                medicoId = medicoId,
                fecha = fechaSeleccionada
            )
        } else {
            emptyList()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Seleccionar fecha y hora",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = medico?.nombre ?: "Médico"
        )

        Text(
            text = medico?.especialidad ?: ""
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Septiembre 2026",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(dias) { fecha ->

                val seleccionado =
                    fechaSeleccionada == fecha

                Card(
                    modifier = Modifier
                        .clickable {
                            fechaSeleccionada = fecha

                            // Al cambiar de día se limpia
                            // la hora seleccionada.
                            horaSeleccionada = ""
                        },
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (seleccionado) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = fecha.takeLast(2),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Horarios disponibles",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (fechaSeleccionada.isEmpty()) {

            Text(
                text = "Selecciona primero un día"
            )

        } else if (horariosDisponibles.isEmpty()) {

            Text(
                text = "No hay horarios disponibles"
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(horariosDisponibles) { hora ->

                    val seleccionado =
                        horaSeleccionada == hora

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                horaSeleccionada = hora
                            },
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (seleccionado) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                        )
                    ) {

                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = hora,
                                fontWeight =
                                    if (seleccionado) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = {
                onContinuarClick(
                    fechaSeleccionada,
                    horaSeleccionada
                )
            },
            enabled =
                fechaSeleccionada.isNotEmpty() &&
                        horaSeleccionada.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}