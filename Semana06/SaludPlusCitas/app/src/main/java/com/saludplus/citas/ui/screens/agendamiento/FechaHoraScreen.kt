package com.saludplus.citas.ui.screens.agendamiento

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onContinuarClick: (String, String) -> Unit,
    onAtrasClick: () -> Unit,
) {

    val medico = Repositorio.obtenerMedico(medicoId)

    val hoy = remember { LocalDate.now() }
    var semanaOffset by remember { mutableIntStateOf(0) }

    val localeEs = remember { Locale.forLanguageTag("es-ES") }

    val lunesBase = remember(hoy) {
        if ((hoy.dayOfWeek == DayOfWeek.SATURDAY) || (hoy.dayOfWeek == DayOfWeek.SUNDAY)) {
            hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        } else {
            hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }
    }

    val dias = remember(semanaOffset, hoy, lunesBase) {
        val inicio = if (semanaOffset == 0) {
            if ((hoy.dayOfWeek == DayOfWeek.SATURDAY) || (hoy.dayOfWeek == DayOfWeek.SUNDAY)) {
                lunesBase
            } else {
                hoy
            }
        } else {
            lunesBase.plusWeeks(semanaOffset.toLong())
        }
        obtenerProximosDiasHabiles(inicio)
    }

    val mesAno = remember(dias, localeEs) {
        if (dias.isNotEmpty()) {
            val primerDia = dias.first()
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", localeEs)
            primerDia.format(formatter).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(localeEs) else it.toString()
            }
        } else {
            ""
        }
    }

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

        EncabezadoConAtras(
            titulo = "Seleccionar fecha y hora",
            onAtrasClick = onAtrasClick
        )

        Text(
            text = medico?.nombre ?: "Médico"
        )

        Text(
            text = medico?.especialidad ?: ""
        )

        Spacer(
            modifier = Modifier.height(24.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mesAno,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Row {
                IconButton(
                    onClick = {
                        if (semanaOffset > 0) {
                            semanaOffset--
                        }
                    },
                    enabled = semanaOffset > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Semana anterior"
                    )
                }

                IconButton(
                    onClick = {
                        semanaOffset++
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Semana siguiente"
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp),
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(dias) { fecha ->

                val fechaString = fecha.toString()
                val seleccionado = fechaSeleccionada == fechaString

                val nombreDia = fecha.format(DateTimeFormatter.ofPattern("EEE", localeEs))
                    .replace(".", "")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeEs) else it.toString() }
                val numeroDia = fecha.format(DateTimeFormatter.ofPattern("dd"))

                Card(
                    modifier = Modifier
                        .clickable {
                            fechaSeleccionada = fechaString
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
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text(
                            text = nombreDia,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = numeroDia,
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
                                },
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continuar")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun obtenerProximosDiasHabiles(inicio: LocalDate, cantidad: Int = 5): List<LocalDate> {
    val dias = mutableListOf<LocalDate>()
    var actual = inicio
    while (dias.size < cantidad) {
        if ((actual.dayOfWeek != DayOfWeek.SATURDAY) && (actual.dayOfWeek != DayOfWeek.SUNDAY)) {
            dias.add(actual)
        }
        actual = actual.plusDays(1)
    }
    return dias
}
