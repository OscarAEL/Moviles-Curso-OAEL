package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    onRegistroClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Clínica",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "SaludPlus",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tu salud, nuestra prioridad",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(180.dp))

        Button(
            onClick = onRegistroClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comenzar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onLoginClick
        ) {
            Text("Ya tengo una cuenta")
        }
    }
}