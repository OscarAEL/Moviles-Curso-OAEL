package com.eneque.lab05navegacion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eneque.lab05navegacion.components.MenuOptionCard
import com.eneque.lab05navegacion.navigation.Screen
import com.eneque.lab05navegacion.ui.theme.AcademicTextSecondary

@Composable
fun HomeScreen(navController: NavController) {
    // Stronger, clearly visible vertical gradient: rich purple -> light lila -> white
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8E79C6), // Intenso morado/lila superior
            Color(0xFFC9BDE8), // Transición a lila claro
            Color(0xFFFFFFFF), // Blanco inferior
        ),
    )

    val softRedColor = Color(0xFFD32F2F)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Spacer to center the main block on screen instead of gluing to top
        Spacer(modifier = Modifier.weight(0.8f))

        // Welcome Header Section (CENTERED)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Bienvenido,",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Oscar Eneque",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Qué deseas gestionar hoy?",
                style = MaterialTheme.typography.bodyMedium,
                color = AcademicTextSecondary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Main Menu Options Section (2 Cards)
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Card 1: Directorio de Alumnos
            MenuOptionCard(
                title = "Directorio de Alumnos",
                subtitle = "Ver y gestionar estudiantes",
                icon = Icons.Default.People,
                onClick = {
                    navController.navigate(Screen.List.route)
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Mi Perfil Académico
            MenuOptionCard(
                title = "Mi Perfil Académico",
                subtitle = "Datos personales y progreso",
                icon = Icons.Default.Person,
                onClick = {
                    navController.navigate(Screen.Profile.route)
                },
            )
        }

        // Spacer pushing Cerrar Sesión Segura towards the bottom
        Spacer(modifier = Modifier.weight(1.2f))

        // Option: Cerrar Sesión Segura in Soft Red tone with safe bottom clearance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Cerrar Sesión",
                tint = softRedColor,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Cerrar Sesión Segura",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = softRedColor,
            )
        }

        // Safe clearance from system navigation bar
        Spacer(modifier = Modifier.height(28.dp))
    }
}
