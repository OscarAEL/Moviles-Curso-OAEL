package com.eneque.lab05tecsupfit.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.eneque.lab05tecsupfit.ui.theme.GreenDarkHeader
import com.eneque.lab05tecsupfit.ui.theme.GreenLightIconBg
import com.eneque.lab05tecsupfit.ui.theme.LightGreyCardBg
import com.eneque.lab05tecsupfit.ui.theme.TextDark
import com.eneque.lab05tecsupfit.ui.theme.TextSecondary

@Composable
fun PerfilScreen(
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        bottomBar = bottomBar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Título "Mi perfil"
            Text(
                text = "Mi perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de datos del usuario centrados
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Círculo grande verde claro con las iniciales "OE"
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(GreenLightIconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenDarkHeader
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nombre completo del usuario
                Text(
                    text = "Oscar Eneque",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Plan de membresía
                Text(
                    text = "Plan Premium",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjetas de estadísticas alineadas horizontalmente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Clases
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreyCardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "14",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Clases",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Tarjeta 2: Rachas
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGreyCardBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "3",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Rachas",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilScreenPreview() {
    PerfilScreen()
}
