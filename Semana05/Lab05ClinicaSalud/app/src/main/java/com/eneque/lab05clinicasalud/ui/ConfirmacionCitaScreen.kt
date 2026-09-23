package com.eneque.lab05clinicasalud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05clinicasalud.model.listaMedicos
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleLightChipBg

@Composable
fun ConfirmacionCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onVerMisCitasClick: () -> Unit = {}
) {
    val medico = listaMedicos.find { it.id == medicoId }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Círculo verde claro con símbolo de check "✓" verde
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(90.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color(0xFF2E7D32),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Título: "¡Cita agendada!"
            Text(
                text = "¡Cita agendada!",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del médico seleccionado
            if (medico != null) {
                Text(
                    text = medico.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Fecha y hora seleccionadas
            val textoHora = if (hora.lowercase().contains("am") || hora.lowercase().contains("pm")) hora else "$hora am"
            Text(
                text = "$fecha, $textoHora",
                fontSize = 15.sp,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Botón "Ver mis citas"
            Button(
                onClick = onVerMisCitasClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleLightChipBg,
                    contentColor = Color(0xFF333333)
                ),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = "Ver mis citas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionCitaScreenPreview() {
    Lab05ClinicaSaludTheme {
        ConfirmacionCitaScreen(
            medicoId = 1,
            fecha = "Vie 27",
            hora = "10:30"
        )
    }
}
