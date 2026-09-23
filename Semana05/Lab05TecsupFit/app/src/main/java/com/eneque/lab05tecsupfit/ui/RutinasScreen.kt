package com.eneque.lab05tecsupfit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.eneque.lab05tecsupfit.ui.theme.TextDark

@Composable
fun RutinasScreen(
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        containerColor = Color.White,
        modifier = Modifier.fillMaxSize(),
        bottomBar = bottomBar
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Rutinas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }
    }
}
