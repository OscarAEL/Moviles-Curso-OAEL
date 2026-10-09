package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// Pantalla para la selección dinámica de fecha (días hábiles) y horarios libres para un médico.
@Composable
fun FechaHoraScreen(
    medicoId: Int, // Recibe el ID del médico seleccionado.
    onContinuarClick: (String, String) -> Unit, // Envía la fecha y hora elegidas a la pantalla de confirmación.
    onAtrasClick: () -> Unit,
) {

    // Obtiene la entidad del médico desde el Repositorio
    val medico = Repositorio.obtenerMedico(medicoId)

    // LocalDate.now(): Obtiene la fecha actual del sistema
    val hoy = remember { LocalDate.now() }

    // semanaOffset: Contador reactivo para navegar entre semanas futuras (0 = semana actual).
    var semanaOffset by remember { mutableIntStateOf(0) }

    val localeEs = remember { Locale.forLanguageTag("es-ES") }

    // Calcula el lunes de la semana base; si hoy es fin de semana, avanza al lunes siguiente.
    val lunesBase = remember(hoy) {
        if ((hoy.dayOfWeek == DayOfWeek.SATURDAY) || (hoy.dayOfWeek == DayOfWeek.SUNDAY)) {
            hoy.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        } else {
            hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }
    }

    // Calcula la lista de los próximos 5 días hábiles a mostrar según semanaOffset.
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

    // Formatea dinámicamente el mes y año en español según los días renderizados.
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

    // Estados reactivos que almacenan la fecha y la hora seleccionadas por el usuario.
    var fechaSeleccionada by remember {
        mutableStateOf("")
    }

    var horaSeleccionada by remember {
        mutableStateOf("")
    }

    // Consulta los horarios libres en el Repositorio para el médico y fecha seleccionados.
    val horariosDisponibles =
        if (fechaSeleccionada.isNotEmpty()) {
            Repositorio.horariosDisponibles(
                medicoId = medicoId,
                fecha = fechaSeleccionada,
            )
        } else {
            emptyList()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {

        EncabezadoConAtras(
            titulo = "Seleccionar fecha y hora",
            onAtrasClick = onAtrasClick,
        )

        // 2. INFORMACIÓN DEL MÉDICO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = obtenerImagenMedico(medico?.nombre)),
                    contentDescription = medico?.nombre ?: "Médico",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = medico?.nombre ?: "Médico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medico?.especialidad ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp),
        )

        // 3. NAVEGACIÓN DEL MES (Controles para cambiar de semana)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Deshabilitado cuando semanaOffset == 0 para evitar navegar a fechas pasadas.
            IconButton(
                onClick = {
                    if (semanaOffset > 0) {
                        semanaOffset--
                    }
                },
                enabled = semanaOffset > 0,
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Semana anterior",
                )
            }

            Text(
                text = mesAno,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.primary,
            )

            IconButton(
                onClick = {
                    semanaOffset++ // Avanza a la siguiente semana
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Semana siguiente",
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp),
        )

        // 4. DÍAS DISPONIBLES (Carrusel horizontal con LazyRow)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            horaSeleccionada = "" // Limpia la hora previa al cambiar de día.
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color(0xFFE3F2FD)
                        },
                        contentColor = if (seleccionado) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    ),
                ) {

                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Text(
                            text = nombreDia,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = numeroDia,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp),
        )

        // 5. HORARIOS DISPONIBLES (Cuadrícula de 3 columnas con LazyVerticalGrid)
        Text(
            text = "Horarios disponibles",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )

        Spacer(
            modifier = Modifier.height(10.dp),
        )

        if (fechaSeleccionada.isEmpty()) {

            Text(
                text = "Selecciona primero un día",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        } else if (horariosDisponibles.isEmpty()) {

            Text(
                text = "No hay horarios disponibles",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        } else {

            // LazyVerticalGrid: Renderiza los botones de horario en una cuadrícula ordenada.
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                items(horariosDisponibles) { hora ->

                    val seleccionado = horaSeleccionada == hora

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                horaSeleccionada = hora // Almacena la hora elegida.
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                            contentColor = if (seleccionado) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        ),
                    ) {

                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = hora,
                                fontWeight = if (seleccionado) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                color = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        // 6. BOTÓN CONTINUAR (Se habilita solo cuando se ha seleccionado fecha y hora)
        Button(
            onClick = {
                onContinuarClick(
                    fechaSeleccionada,
                    horaSeleccionada,
                )
            },
            enabled = fechaSeleccionada.isNotEmpty() && horaSeleccionada.isNotEmpty(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// Genera una lista de N días hábiles consecutivos excluyendo Sábados y Domingos.
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

private fun obtenerImagenMedico(nombre: String?): Int {
    return when (nombre) {
        "Dra. Ana Torres" -> R.drawable.doctora_1
        "Dra. Mariana Soto" -> R.drawable.doctora_2
        "Dr. Carlos Rojas" -> R.drawable.doctor_1
        "Dr. Luis Ramírez" -> R.drawable.doctor_2
        else -> R.drawable.doctor_1
    }
}
