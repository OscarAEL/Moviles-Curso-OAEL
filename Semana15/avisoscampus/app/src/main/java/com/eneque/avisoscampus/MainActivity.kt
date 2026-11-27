package com.eneque.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eneque.avisoscampus.ui.PantallaAvisos
import com.eneque.avisoscampus.ui.SplashScreen
import com.eneque.avisoscampus.ui.theme.AvisosCampusTheme
import com.eneque.avisoscampus.viewmodel.AvisosViewModel
import com.eneque.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val enPrueba = try {
            Class.forName("androidx.test.platform.app.InstrumentationRegistry")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(
                    factory = AvisosViewModelFactory((application as AvisosApp).repository)
                )
                var mostrarSplash by remember { mutableStateOf(!enPrueba) }

                Box(modifier = Modifier.fillMaxSize()) {
                    PantallaAvisos(vm)
                    if (mostrarSplash) {
                        SplashScreen { mostrarSplash = false }
                    }
                }
            }
        }
    }
}
