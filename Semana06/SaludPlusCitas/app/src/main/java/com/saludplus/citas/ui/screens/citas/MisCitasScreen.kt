package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraNavegacion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Pantalla para consultar el listado de citas agendadas por el usuario logueado.
@Composable
fun MisCitasScreen(
    onCitaClick: (Int) -> Unit, // Callback para navegar al detalle enviando el ID de la cita seleccionada.
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {

    val usuario = Repositorio.usuarioActual

    // Obtiene únicamente las citas asociadas al usuario con sesión activa
    val citas =
        if (usuario != null) {
            Repositorio.citasDelUsuario(usuario.id)
        } else {
            emptyList()
        }

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                seccionActual = "Citas",
                onInicioClick = onInicioClick,
                onCitasClick = onCitasClick,
                onResultadosClick = onResultadosClick,
                onPerfilClick = onPerfilClick,
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {

            Text(
                text = "Mis citas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(
                modifier = Modifier.height(16.dp),
            )

            // Si el usuario no registra citas, muestra un estado informativo vacío
            if (citas.isEmpty()) {

                // ESTADO VACÍO
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.EventNote,
                                contentDescription = "Sin citas",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(42.dp),
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Aún no tienes citas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Tus próximas citas aparecerán aquí",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

            } else {

                // LazyColumn para renderizar las citas agendadas por el usuario
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {

                    items(citas) { cita ->

                        // Obtiene los datos del médico asignado a esta cita
                        val medico = Repositorio.obtenerMedico(cita.medicoId)
                        val fechaFormateada = formatearFecha(cita.fecha)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Navega al detalle de la cita usando citaId
                                    onCitaClick(cita.id)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            ),
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                // IZQUIERDA: Foto del médico
                                Image(
                                    painter = painterResource(id = obtenerImagenMedico(medico?.nombre)),
                                    contentDescription = medico?.nombre ?: "Médico",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape),
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // CENTRO: Detalles de la cita
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {

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

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "$fechaFormateada - ${cita.hora}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = "Programada",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2E7D32),
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // DERECHA: Flecha indicador
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Ver detalle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {
    return try {
        val localeEs = Locale.forLanguageTag("es-ES")
        val localDate = LocalDate.parse(fecha)
        val formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM yyyy", localeEs)
        localDate.format(formatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(localeEs) else it.toString()
        }
    } catch (_: Exception) {
        fecha
    }
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
