package com.eneque.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eneque.avisoscampus.ui.PantallaAvisos
import com.eneque.avisoscampus.ui.theme.AvisosCampusTheme
import com.eneque.avisoscampus.viewmodel.AvisosViewModel
import com.eneque.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(
                    factory = AvisosViewModelFactory((application as AvisosApp).repository)
                )
                PantallaAvisos(vm)
            }
        }
    }
}
