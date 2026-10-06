package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun CitaExitosaScreen(
    citaId: Int,
    onMisCitasClick: () -> Unit,
    onInicioClick: () -> Unit
) {

    val cita = Repositorio.obtenerCita(citaId)

    val medico =
        if (cita != null) {
            Repositorio.obtenerMedico(cita.medicoId)
        } else {
            null
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(60.dp)
        )

        Text(
            text = "¡Cita agendada!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Tu cita fue registrada correctamente."
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (cita != null) {

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

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Button(
            onClick = onMisCitasClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mis citas")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onInicioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}