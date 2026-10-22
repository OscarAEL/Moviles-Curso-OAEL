package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.tecsup.lavacampus.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as LavaApp
        setContent {
            val oscuro by app.preferenciasStore.modoOscuro.collectAsState(initial = false)
            SideEffect {
                val estilo = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { oscuro }
                enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !oscuro
                    isAppearanceLightNavigationBars = !oscuro
                }
            }
            AppNavGraph(app = app)
        }
    }
}
