package com.saludplus.citas.ui.screens.citas

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun MisCitasScreen(
    onCitaClick: (Int) -> Unit
) {

    val usuario = Repositorio.usuarioActual

    val citas =
        if (usuario != null) {
            Repositorio.citasDelUsuario(usuario.id)
        } else {
            emptyList()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Mis citas",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (citas.isEmpty()) {

            Text(
                text = "Aún no tienes citas agendadas."
            )

        } else {

            LazyColumn {

                items(citas) { cita ->

                    val medico =
                        Repositorio.obtenerMedico(cita.medicoId)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                onCitaClick(cita.id)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = medico?.nombre ?: "Médico",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = medico?.especialidad ?: ""
                            )

                            Text(
                                text = "Fecha: ${cita.fecha}"
                            )

                            Text(
                                text = "Hora: ${cita.hora}"
                            )
                        }
                    }
                }
            }
        }
    }
}