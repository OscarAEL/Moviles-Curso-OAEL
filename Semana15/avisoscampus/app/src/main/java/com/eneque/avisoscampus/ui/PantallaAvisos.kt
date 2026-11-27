package com.eneque.avisoscampus.ui

import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.eneque.avisoscampus.receiver.ModoAvionReceiver
import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eneque.avisoscampus.model.PermisoUi
import com.eneque.avisoscampus.notificacion.Notificador
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eneque.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    LaunchedEffect(vm, contexto) {
        vm.eventos.collect { aviso -> Notificador.mostrar(contexto, aviso) }
    }
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    DisposableEffect(escuchando, contexto) {
        if (escuchando) {
            val receptor = ModoAvionReceiver(vm::alCambiarModoAvion)
            ContextCompat.registerReceiver(
                contexto, receptor, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }
    var explicarPermiso by rememberSaveable { mutableStateOf(false) }
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.permisoRespondido(it)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }
    DisposableEffect(lifecycleOwner, contexto) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val concedido = Notificador.tienePermiso(contexto)
                if (concedido || vm.permiso.value != PermisoUi.SinPedir) {
                    vm.permisoRespondido(concedido)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    if (explicarPermiso) {
        AlertDialog(
            onDismissRequest = { explicarPermiso = false },
            title = { Text("Permitir avisos") },
            text = { Text("AvisosCampus necesita permiso para mostrar tus avisos y recordatorios fuera de la app.") },
            confirmButton = {
                TextButton(onClick = {
                    explicarPermiso = false
                    if (Build.VERSION.SDK_INT >= 33) lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text("Continuar") }
            },
            dismissButton = { TextButton(onClick = { explicarPermiso = false }) { Text("Ahora no") } }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaFuncion("Permiso de notificaciones") {
                val etiqueta = when (permiso) {
                    PermisoUi.SinPedir -> "Sin pedir"
                    PermisoUi.Concedido -> "Permitido"
                    PermisoUi.Denegado -> "Denegado"
                }
                val color = when (permiso) {
                    PermisoUi.SinPedir -> MaterialTheme.colorScheme.onSurfaceVariant
                    PermisoUi.Concedido -> Color(0xFF237A37)
                    PermisoUi.Denegado -> MaterialTheme.colorScheme.error
                }
                Surface(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small) {
                    Text(etiqueta, Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = color)
                }
                Button(onClick = {
                    when {
                        Notificador.tienePermiso(contexto) -> vm.permisoRespondido(true)
                        Build.VERSION.SDK_INT >= 33 -> {
                            val activity = contexto as? Activity
                            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(
                                    activity, Manifest.permission.POST_NOTIFICATIONS
                                )) explicarPermiso = true
                            else lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        else -> vm.permisoRespondido(true)
                    }
                }) { Text("Permitir notificaciones") }
            }
            TarjetaFuncion("Avisos") {
                Button(onClick = vm::avisarAhora) { Text("Mostrar aviso") }
            }
            TarjetaFuncion("Recordatorio") {
                Button(onClick = { vm.recordarEn() }, enabled = faltan == null) { Text("Recordar en 10 s") }
                OutlinedButton(onClick = vm::cancelarRecordatorio, enabled = faltan != null) { Text("Cancelar") }
                faltan?.let { Text("Faltan $it s", style = MaterialTheme.typography.bodyLarge) }
            }
            TarjetaFuncion("Receptor de anuncios") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Escuchar el modo avión", Modifier.weight(1f))
                    Switch(checked = escuchando, onCheckedChange = vm::cambiarEscucha)
                }
            }
            TarjetaFuncion("Historial") { Text("Sin avisos todavía") }
        }
    }
}

@Composable
internal fun TarjetaFuncion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            contenido()
        }
    }
}
