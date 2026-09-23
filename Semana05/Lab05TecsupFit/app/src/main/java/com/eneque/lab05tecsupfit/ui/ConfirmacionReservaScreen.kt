package com.eneque.lab05tecsupfit.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05tecsupfit.model.ClaseRepository
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.GreenLightIconBg
import com.eneque.lab05tecsupfit.ui.theme.LightGreyCardBg
import com.eneque.lab05tecsupfit.ui.theme.TextDark
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

@Composable
fun ConfirmacionReservaScreen(
    claseId: Int,
    onVerReservasClick: () -> Unit = {}
) {
    val clase = ClaseRepository.getClaseById(claseId)

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (clase != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Círculo verde claro con el símbolo "✓" en verde oscuro
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(GreenLightIconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenDarkHeader
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título de confirmación
                    Text(
                        text = "¡Cupo reservado!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre de la clase seleccionada
                    Text(
                        text = clase.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Fecha, horario y sala de la clase
                    Text(
                        text = "Hoy, ${clase.horario} · ${clase.sala}",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botón "Ver mis reservas"
                    Button(
                        onClick = onVerReservasClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGreyCardBg,
                            contentColor = TextDark
                        ),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = "Ver mis reservas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Clase no encontrada",
                    fontSize = 16.sp,
                    color = Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmacionReservaScreenPreview() {
    ConfirmacionReservaScreen(claseId = 2)
}
