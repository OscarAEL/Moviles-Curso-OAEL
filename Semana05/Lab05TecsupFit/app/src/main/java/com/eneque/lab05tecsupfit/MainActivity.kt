package com.eneque.lab05tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eneque.lab05tecsupfit.ui.HomeScreen
import com.eneque.lab05tecsupfit.ui.theme.Lab05TecsupFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab05TecsupFitTheme {
                HomeScreen()
            }
        }
    }
}
