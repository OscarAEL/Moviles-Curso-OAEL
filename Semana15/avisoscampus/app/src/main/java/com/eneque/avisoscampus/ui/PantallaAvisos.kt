package com.eneque.avisoscampus.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eneque.avisoscampus.R
import com.eneque.avisoscampus.model.PermisoUi
import com.eneque.avisoscampus.notificacion.Notificador
import com.eneque.avisoscampus.receiver.ModoAvionReceiver
import com.eneque.avisoscampus.ui.theme.*
import com.eneque.avisoscampus.viewmodel.AvisosViewModel

private data class EstadoPermisoVisual(
    val etiqueta: String,
    val fondo: Color,
    val texto: Color,
    val dot: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    LaunchedEffect(vm, contexto) {
        vm.eventos.collect { aviso ->
            if (!Notificador.mostrar(contexto, aviso)) vm.noSePudoMostrar()
        }
    }
    val mensaje by vm.mensaje.collectAsState()
    val necesitaAjustes by vm.necesitaAjustes.collectAsState()
    val avisos by vm.avisos.collectAsState()
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
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tecsup),
                            contentDescription = "Tecsup Logo",
                            modifier = Modifier.height(26.dp)
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(MicroBordeBrillante)
                        )
                        Text(
                            text = "AvisosCampus",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BarraSuperiorColor,
                    titleContentColor = TextoPrincipal
                )
            )
        },
        containerColor = FondoMedianoche
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaFuncion("Permiso de notificaciones") {
                val visual = when (permiso) {
                    PermisoUi.SinPedir -> EstadoPermisoVisual("Sin pedir", PermisoSinPedirFondo, PermisoSinPedirTexto, PermisoSinPedirTexto)
                    PermisoUi.Concedido -> EstadoPermisoVisual("Permitido", PermisoPermitidoFondo, PermisoPermitidoTexto, PermisoPermitidoTexto)
                    PermisoUi.Denegado -> EstadoPermisoVisual("Denegado", PermisoDenegadoFondo, PermisoDenegadoTexto, Color(0xFFFF5449))
                }
                Surface(
                    color = visual.fondo,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, visual.dot.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(visual.dot)
                        )
                        Text(
                            text = visual.etiqueta,
                            color = visual.texto,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Button(
                    onClick = {
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanTecsup,
                        contentColor = TextoSobreCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Permitir notificaciones", fontWeight = FontWeight.Bold)
                }
                if (permiso == PermisoUi.Denegado || necesitaAjustes) {
                    OutlinedButton(
                        onClick = {
                            contexto.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanClaro),
                        border = BorderStroke(1.dp, CyanTecsup.copy(alpha = 0.65f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Abrir ajustes", fontWeight = FontWeight.SemiBold)
                    }
                }
                mensaje?.let {
                    Surface(
                        color = ErrorBannerFondo,
                        contentColor = ErrorBannerTexto,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ErrorBannerBorde),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            TarjetaFuncion("Avisos") {
                Button(
                    onClick = vm::avisarAhora,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanTecsup,
                        contentColor = TextoSobreCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Mostrar aviso", fontWeight = FontWeight.Bold)
                }
            }

            TarjetaFuncion("Recordatorio") {
                Button(
                    onClick = { vm.recordarEn() },
                    enabled = faltan == null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanTecsup,
                        contentColor = TextoSobreCyan,
                        disabledContainerColor = Color(0xFF132840),
                        disabledContentColor = Color(0xFF5A7A9A)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Recordar en 10 s", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = vm::cancelarRecordatorio,
                    enabled = faltan != null,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyanClaro,
                        disabledContentColor = Color(0xFF3B556E)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (faltan != null) CyanTecsup.copy(alpha = 0.7f) else MicroBorde
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.SemiBold)
                }
                faltan?.let {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF07243B),
                        border = BorderStroke(1.5.dp, CyanClaro.copy(alpha = 0.85f))
                    ) {
                        Box(
                            modifier = Modifier.padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Faltan $it s",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = CyanClaro
                            )
                        }
                    }
                }
            }

            TarjetaFuncion("Receptor de anuncios") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Escuchar el modo avión",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoPrincipal
                    )
                    Switch(
                        checked = escuchando,
                        onCheckedChange = vm::cambiarEscucha,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextoSobreCyan,
                            checkedTrackColor = CyanTecsup,
                            uncheckedThumbColor = TextoSecundario,
                            uncheckedTrackColor = TarjetaFin,
                            uncheckedBorderColor = MicroBorde
                        )
                    )
                }
            }

            HistorialAvisos(avisos)
        }
    }
}

@Composable
internal fun TarjetaFuncion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = TarjetaInicio,
        border = BorderStroke(1.dp, MicroBorde)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(TarjetaInicio, TarjetaFin)
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(CyanClaro)
                    )
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = CyanClaro,
                        fontWeight = FontWeight.Bold
                    )
                }
                contenido()
            }
        }
    }
}
