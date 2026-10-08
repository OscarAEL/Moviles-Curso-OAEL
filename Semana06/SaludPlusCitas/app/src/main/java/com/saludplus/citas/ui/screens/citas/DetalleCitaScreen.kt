package com.saludplus.citas.ui.screens.citas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Pantalla para consultar la información detallada de una cita específica.
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetalleCitaScreen(
    citaId: Int, // Recibe el ID de la cita a consultar.
    onAtrasClick: () -> Unit = {},
) {
    // Consulta la cita y su médico correspondiente mediante el Repositorio
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // 1. ENCABEZADO
        EncabezadoConAtras(
            titulo = "Detalle de cita",
            onAtrasClick = onAtrasClick,
        )

        if (cita == null) {
            Text(
                text = "No se encontró la cita solicitada.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
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
                        if (!medico?.cmp.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = medico.cmp,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. DETALLES DE LA CITA
            val fechaFormateada = formatearFecha(cita.fecha)
            // Si el usuario no escribió motivo, se muestra "Sin especificar"
            val motivoTexto = cita.motivo.ifBlank { "Sin especificar" }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {

                    // Fecha en formato largo
                    FilaDetalleCita(
                        icono = Icons.Default.DateRange,
                        titulo = "Fecha",
                        subtitulo = fechaFormateada,
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Hora de la cita
                    FilaDetalleCita(
                        icono = Icons.Default.Schedule,
                        titulo = "Hora",
                        subtitulo = cita.hora,
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Motivo de consulta personalizado o "Sin especificar"
                    FilaDetalleCita(
                        icono = Icons.Default.Description,
                        titulo = "Motivo de consulta",
                        subtitulo = motivoTexto,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaDetalleCita(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
