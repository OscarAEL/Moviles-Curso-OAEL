package com.eneque.campusgps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.eneque.campusgps.viewmodel.MapaViewModel

@Composable
fun PantallaMapa(vm: MapaViewModel, modifier: Modifier = Modifier) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugaresFiltrados.collectAsState()
    val categoria by vm.categoriaSeleccionada.collectAsState()
    val radio by vm.radioMetros.collectAsState()
    val dentro by vm.lugaresDentroDelRadio.collectAsState()
    val contorno by vm.contornoRadio.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val cercano by vm.masCercano.collectAsState()
    val contexto = LocalContext.current
    val camara = rememberEstadoCamaraMapa()
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }
    ContenidoCampus(
        estado = EstadoPantallaCampus(estado, lugares, vm.categorias, categoria,
            vm.radiosDisponibles, radio, seleccionado, cercano, dentro),
        onBuscar = {
            val tienePermiso = ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(contexto,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            if (tienePermiso) vm.buscar()
            else lanzador.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        },
        onReintentar = { vm.buscar() },
        onAbrirAjustes = {
            contexto.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", contexto.packageName, null)
            })
        },
        onCategoria = { vm.filtrarPorCategoria(it) },
        onRadio = { vm.cambiarRadio(it) },
        onSeleccionar = { vm.seleccionar(it) },
        modifier = modifier,
        mapa = { mapaModifier ->
            MapaUbicacion(estado, lugares, seleccionado, modifier = mapaModifier,
                masCercano = cercano?.id, contornoRadio = contorno,
                onSeleccionarLugar = { vm.seleccionar(it) }, estadoCamara = camara)
        }
    )
}
