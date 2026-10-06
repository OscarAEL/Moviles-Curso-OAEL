package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras

@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onCitaConfirmada: (Int) -> Unit,
    onAtrasClick: () -> Unit
) {

    val medico = Repositorio.obtenerMedico(medicoId)
    val usuario = Repositorio.usuarioActual

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        EncabezadoConAtras(
            titulo = "Confirmar cita",
            onAtrasClick = onAtrasClick
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = medico?.nombre ?: "Médico",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Especialidad: ${medico?.especialidad ?: ""}"
                )

                Text(
                    text = "Fecha: $fecha"
                )

                Text(
                    text = "Hora: $hora"
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                if (usuario != null) {

                    val nuevoId =
                        (Repositorio.citas.maxOfOrNull { it.id } ?: 0) + 1

                    val nuevaCita = Cita(
                        id = nuevoId,
                        usuarioId = usuario.id,
                        medicoId = medicoId,
                        fecha = fecha,
                        hora = hora
                    )

                    val agendada =
                        Repositorio.agendarCita(nuevaCita)

                    if (agendada) {
                        onCitaConfirmada(nuevaCita.id)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar cita")
        }
    }
}