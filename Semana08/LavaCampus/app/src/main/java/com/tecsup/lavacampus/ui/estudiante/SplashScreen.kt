package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun SplashScreen(onComenzarClick: () -> Unit) {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? android.app.Activity)?.window
        window?.let {
            androidx.core.view.WindowCompat.getInsetsController(it, view)
                .isAppearanceLightStatusBars = false
        }
        onDispose {
            window?.let {
                androidx.core.view.WindowCompat.getInsetsController(it, view)
                    .isAppearanceLightStatusBars = true
            }
        }
    }
    LavaScaffold(containerColor = Color(0xFF062F3A)) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Canvas(Modifier.fillMaxWidth().height(270.dp)) {
                val c = Offset(size.width / 2, size.height / 2)
                drawCircle(
                    Color(0xFF205A68),
                    radius = size.width * .46f,
                    center = c,
                    style = Stroke(2.dp.toPx()),
                )
                drawCircle(
                    Color(0xFF1CCAE1),
                    radius = size.width * .35f,
                    center = c,
                    style = Stroke(3.dp.toPx()),
                )
                drawCircle(Color(0xFF0B5262), radius = size.width * .22f, center = c)
                val path = Path()
                for (i in 0..100) {
                    val x = c.x - size.width * .17f + size.width * .34f * i / 100
                    val y = c.y + kotlin.math.sin(i * .19f).toFloat() * 5.dp.toPx()
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, Color(0xFF73E6F2), style = Stroke(4.dp.toPx()))
            }
            Text(
                "LavaCampus",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
            )
            Text(
                "Tu ropa limpia, sin salir del campus.",
                color = Color(0xFF77E4EF),
                fontSize = 15.sp,
            )
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onComenzarClick,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1CCAE1),
                        contentColor = Color(0xFF062F3A),
                    ),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(17.dp),
            ) {
                Text("Comenzar", fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = onComenzarClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = Color(0xFF77E4EF))
            }
        }
    }
}
