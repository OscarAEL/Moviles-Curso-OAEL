package com.eneque.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import java.util.Locale
import androidx.core.content.ContextCompat
import com.eneque.campusgps.model.UbicacionUi
import com.eneque.campusgps.viewmodel.MapaViewModel
import com.eneque.campusgps.util.formatearDistancia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel, modifier: Modifier = Modifier) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugaresFiltrados.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val radioMetros by vm.radioMetros.collectAsState()
    val dentroDelRadio by vm.lugaresDentroDelRadio.collectAsState()
    val contornoRadio by vm.contornoRadio.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                actions = {
                    SelectorRadio(
                        radios = vm.radiosDisponibles,
                        seleccionado = radioMetros,
                        onSeleccionar = { vm.cambiarRadio(it) }
                    )
                },
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    if (tienePermiso) {
                        vm.buscar()
                    } else {
                        lanzador.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            ) {
                Text("Obtener mi ubicación")
            }
            val error = estado as? UbicacionUi.Error
            if (error != null) {
                MensajeError(
                    error = error,
                    onReintentar = { vm.buscar() },
                    onAbrirAjustes = {
                        contexto.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", contexto.packageName, null)
                            }
                        )
                    }
                )
            } else {
                val posicion = (estado as? UbicacionUi.Lista)?.posicion
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Latitud: ${posicion?.let { String.format(Locale.ROOT, "%.6f", it.latitud) } ?: "—"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Longitud: ${posicion?.let { String.format(Locale.ROOT, "%.6f", it.longitud) } ?: "—"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercano = masCercano?.id,
                contornoRadio = contornoRadio,
                onSeleccionarLugar = { vm.seleccionar(it) },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Column(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FiltrosCategorias(
                    categorias = vm.categorias,
                    seleccionada = categoriaSeleccionada,
                    onSeleccionar = { vm.filtrarPorCategoria(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                ListaLugares(
                    lugares = lugares,
                    seleccionado = seleccionado,
                    onClick = { vm.seleccionar(it) },
                    dentroDelRadio = dentroDelRadio,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    enabled = masCercano != null,
                    onClick = { masCercano?.let { vm.seleccionar(it.id) } }
                ) {
                    Text("Ir al más cercano")
                }
                val cercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }
                val metros = cercano?.metros
                if (cercano != null && metros != null) {
                    Text("Más cercano: ${cercano.lugar.nombre} (${formatearDistancia(metros)})")
                }
            }
        }
    }
}
