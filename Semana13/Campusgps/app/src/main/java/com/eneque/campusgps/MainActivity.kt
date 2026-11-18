package com.eneque.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.eneque.campusgps.data.LugaresRepositoryLocal
import com.eneque.campusgps.data.UbicacionRepositoryGoogle
import com.eneque.campusgps.ui.PantallaMapa
import com.eneque.campusgps.ui.theme.CampusGPSTheme
import com.eneque.campusgps.viewmodel.CampusViewModelFactory
import com.eneque.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {
    private val vm: MapaViewModel by viewModels {
        CampusViewModelFactory(
            UbicacionRepositoryGoogle(applicationContext),
            LugaresRepositoryLocal()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                PantallaMapa(vm)
            }
        }
    }
}