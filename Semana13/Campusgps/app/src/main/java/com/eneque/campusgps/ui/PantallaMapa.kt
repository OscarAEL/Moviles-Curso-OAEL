package com.eneque.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel, modifier: Modifier = Modifier) {
    val estado by vm.estado.collectAsState()
    val contexto = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                enabled = estado != UbicacionUi.Buscando,
                onClick = {
                    val tienePermiso = ContextCompat.checkSelfPermission(
                        contexto, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                            contexto, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    if (tienePermiso) vm.buscar()
                    // TODO Paso 2: solicitar permisos si faltan; al aceptar buscar,
                    // y al negar llamar a vm.permisoDenegado().
                }
            ) {
                Text("Obtener mi ubicación")
            }
            val posicion = (estado as? UbicacionUi.Lista)?.posicion
            Text("Latitud: ${posicion?.latitud ?: "—"}")
            Text("Longitud: ${posicion?.longitud ?: "—"}")

            // TODO Paso 3: integrar MapaUbicacion.
            // TODO Paso 6: integrar ListaLugares y selección.
            // TODO Paso 7: botón y texto del lugar más cercano.
            // TODO Paso 8: integrar MensajeError.
        }
    }
}
