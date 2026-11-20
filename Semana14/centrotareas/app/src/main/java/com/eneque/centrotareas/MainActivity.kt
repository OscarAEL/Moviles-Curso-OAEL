package com.eneque.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eneque.centrotareas.data.TareasRepositoryLocal
import com.eneque.centrotareas.ui.PantallaTareas
import com.eneque.centrotareas.ui.theme.CentroTareasTheme
import com.eneque.centrotareas.viewmodel.TareasViewModel
import com.eneque.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    private val tareasViewModel: TareasViewModel by viewModels {
        TareasViewModelFactory(TareasRepositoryLocal())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                PantallaTareas(viewModel = tareasViewModel)
            }
        }
    }
}
