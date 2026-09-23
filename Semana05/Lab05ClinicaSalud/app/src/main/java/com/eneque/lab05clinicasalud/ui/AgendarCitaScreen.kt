package com.eneque.lab05clinicasalud.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import com.eneque.lab05clinicasalud.ui.theme.PurpleLightChipBg

data class OpcionFecha(
    val dia: String,
    val numero: String
)

@Composable
fun AgendarCitaScreen(
    medicoId: Int,
    onBackClick: () -> Unit,
    onConfirmarClick: (fecha: String, hora: String) -> Unit = { _, _ -> }
) {
    val opcionesFecha = listOf(
        OpcionFecha("Jue", "26"),
        OpcionFecha("Vie", "27"),
        OpcionFecha("Sáb", "28")
    )
    val opcionesHora = listOf("9:00", "10:30", "3:00")

    // Selección única de fecha y hora
    var fechaSeleccionada by remember { mutableStateOf("Vie 27") }
    var horaSeleccionada by remember { mutableStateOf("10:30") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // Encabezado superior
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "←",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .padding(end = 12.dp)
                )
                Text(
                    text = "Agendar cita",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Sección: Selecciona fecha
                Text(
                    text = "Selecciona fecha",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    opcionesFecha.forEach { opcion ->
                        val clave = "${opcion.dia} ${opcion.numero}"
                        val esSeleccionado = clave == fechaSeleccionada

                        Surface(
                            onClick = { fechaSeleccionada = clave },
                            shape = RoundedCornerShape(16.dp),
                            color = if (esSeleccionado) PurpleDarkHeader else PurpleLightChipBg,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = opcion.dia,
                                    color = if (esSeleccionado) Color.White else Color(0xFF333333),
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = opcion.numero,
                                    color = if (esSeleccionado) Color.White else Color.Black,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sección: Selecciona hora
                Text(
                    text = "Selecciona hora",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    opcionesHora.forEach { hora ->
                        val esSeleccionado = hora == horaSeleccionada

                        Surface(
                            onClick = { horaSeleccionada = hora },
                            shape = RoundedCornerShape(16.dp),
                            color = if (esSeleccionado) PurpleDarkHeader else PurpleLightChipBg,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hora,
                                    color = if (esSeleccionado) Color.White else Color(0xFF333333),
                                    fontSize = 15.sp,
                                    fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Botón inferior "Confirmar cita"
            Button(
                onClick = { onConfirmarClick(fechaSeleccionada, horaSeleccionada) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleDarkHeader
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Confirmar cita",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendarCitaScreenPreview() {
    Lab05ClinicaSaludTheme {
        AgendarCitaScreen(
            medicoId = 1,
            onBackClick = {}
        )
    }
}
