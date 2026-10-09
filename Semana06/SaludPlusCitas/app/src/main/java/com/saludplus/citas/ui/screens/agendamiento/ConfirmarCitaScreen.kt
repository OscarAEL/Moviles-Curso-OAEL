package com.saludplus.citas.ui.screens.agendamiento

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
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Pantalla final del flujo de agendamiento para revisar detalles y registrar la cita.
@Composable
fun ConfirmarCitaScreen(
    medicoId: Int, // ID del médico seleccionado.
    fecha: String,   // Fecha seleccionada en formato "YYYY-MM-DD".
    hora: String,    // Hora seleccionada.
    onCitaConfirmada: (Int) -> Unit, // Callback ejecutado tras guardar la cita con éxito.
    onAtrasClick: () -> Unit,
) {

    val medico = Repositorio.obtenerMedico(medicoId)
    val usuario = Repositorio.usuarioActual

    // Estado reactivo para ingresar opcionalmente el motivo de consulta.
    var motivoConsulta by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    // Formatea la fecha recibida a formato largo en español (ej: "Lunes 12 de Octubre 2026").
    val fechaFormateada = remember(fecha) {
        try {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {

        // 1. ENCABEZADO
        EncabezadoConAtras(
            titulo = "Confirmar cita",
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medico?.cmp ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. RESUMEN DE LA CITA
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

                // Fila 1: Fecha
                FilaDetalleResumen(
                    icono = Icons.Default.DateRange,
                    titulo = "Fecha",
                    subtitulo = fechaFormateada,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Fila 2: Hora
                FilaDetalleResumen(
                    icono = Icons.Default.Schedule,
                    titulo = "Hora",
                    subtitulo = hora,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Fila 3: Tipo de atención
                FilaDetalleResumen(
                    icono = Icons.Default.LocalHospital,
                    titulo = "Tipo de atención",
                    subtitulo = "Consulta presencial",
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Fila 4: Dirección
                FilaDetalleResumen(
                    icono = Icons.Default.LocationOn,
                    titulo = "Dirección",
                    subtitulo = "Av. Los Olivos 123",
                    detalleExtra = "Lima",
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. MOTIVO DE CONSULTA (OPCIONAL)
        Text(
            text = "Motivo de consulta (opcional)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Campo para ingresar el motivo de consulta; se limpia con trim() antes de guardar.
        OutlinedTextField(
            value = motivoConsulta,
            onValueChange = { motivoConsulta = it },
            placeholder = {
                Text("Consulta de rutina")
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. BOTÓN CONFIRMAR: Construye el objeto Cita y lo guarda en el Repositorio.
        Button(
            onClick = {
                if (usuario != null) {
                    val nuevoId = (Repositorio.citas.maxOfOrNull { it.id } ?: 0) + 1
                    val nuevaCita = Cita(
                        id = nuevoId,
                        usuarioId = usuario.id,
                        medicoId = medicoId,
                        fecha = fecha,
                        hora = hora,
                        motivo = motivoConsulta.trim(), // Se aplica trim() para no guardar espacios sobrantes.
                    )

                    val agendada = Repositorio.agendarCita(nuevaCita)
                    if (agendada) {
                        onCitaConfirmada(nuevaCita.id) // Navega al comprobante de éxito enviando el citaId
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = "Confirmar cita",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FilaDetalleResumen(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    detalleExtra: String? = null,
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
            detalleExtra?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
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
