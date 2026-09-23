package com.eneque.lab05clinicasalud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.eneque.lab05clinicasalud.ui.theme.GraySubtitle
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleCircleIconBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import com.eneque.lab05clinicasalud.ui.theme.StarYellow

@Composable
fun PerfilMedicoScreen(
    medicoId: Int,
    onBackClick: () -> Unit,
    onAgendarCitaClick: () -> Unit = {}
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
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // Encabezado superior con fondo blanco
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
                    text = "Perfil del médico",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            if (medico != null) {
                // Área de contenido que ocupa el espacio disponible antes del botón
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Círculo grande de color lila muy claro con símbolo "+" morado
                    Surface(
                        shape = CircleShape,
                        color = PurpleCircleIconBg,
                        modifier = Modifier.size(90.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                color = PurpleDarkHeader,
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre del médico
                    Text(
                        text = medico.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Especialidad y años de experiencia
                    Text(
                        text = "${medico.especialidad} · ${medico.experiencia} años exp.",
                        fontSize = 15.sp,
                        color = GraySubtitle
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Calificación y reseñas
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★",
                            color = StarYellow,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${medico.calificacion} (${medico.resenas} reseñas)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Descripción del médico
                    Text(
                        text = medico.descripcion,
                        fontSize = 15.sp,
                        color = Color(0xFF333333),
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }

                // Botón ancho "Agendar cita" en la parte inferior
                Button(
                    onClick = onAgendarCitaClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDarkHeader
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Agendar cita",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Médico no encontrado",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilMedicoScreenPreview() {
    Lab05ClinicaSaludTheme {
        PerfilMedicoScreen(
            medicoId = 1,
            onBackClick = {}
        )
    }
}
