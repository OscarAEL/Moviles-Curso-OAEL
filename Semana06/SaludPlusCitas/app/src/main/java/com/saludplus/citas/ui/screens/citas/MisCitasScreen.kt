package com.saludplus.citas.ui.screens.citas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraNavegacion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MisCitasScreen(
    onCitaClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {

    val usuario = Repositorio.usuarioActual

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
                .padding(20.dp),
        ) {

            Text(
                text = "Mis citas",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(
                modifier = Modifier.height(20.dp),
            )

            if (citas.isEmpty()) {

                Text(
                    text = "Aún no tienes citas agendadas.",
                )

            } else {

                LazyColumn {

                    items(citas) { cita ->

                        val medico =
                            Repositorio.obtenerMedico(cita.medicoId)
                        val fechaFormateada = formatearFecha(cita.fecha)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    onCitaClick(cita.id)
                                },
                        ) {

                            Column(
                                modifier = Modifier.padding(18.dp),
                            ) {

                                Text(
                                    text = medico?.nombre ?: "Médico",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp),
                                )

                                Text(
                                    text = medico?.especialidad ?: "",
                                )

                                Text(
                                    text = "Fecha: $fechaFormateada",
                                )

                                Text(
                                    text = "Hora: ${cita.hora}",
                                )
                            }
                        }
                    }
                }
            }
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
