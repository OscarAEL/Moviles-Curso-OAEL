package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import java.util.Locale

@Composable
fun FormularioServicioScreen(
    viewModel: ServiciosViewModel,
    servicioId: Int? = null,
    onBackClick: () -> Unit,
    onGuardadoExito: () -> Unit
) {
    val servicios by viewModel.servicios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val eliminando by viewModel.eliminando.collectAsState()
    val error by viewModel.error.collectAsState()
    val exitoGuardado by viewModel.exitoGuardado.collectAsState()
    val exitoEliminado by viewModel.exitoEliminado.collectAsState()

    val esEdicion = servicioId != null && servicioId > 0

    var cargadoInicial by rememberSaveable { mutableStateOf(false) }
    val servicioActual = servicios.firstOrNull { it.id == servicioId }

    var nombre by rememberSaveable { mutableStateOf("") }
    var unidad by rememberSaveable { mutableStateOf("KG") }
    var precioInput by rememberSaveable { mutableStateOf("") }
    var horasEntrega by rememberSaveable { mutableIntStateOf(24) }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf("wash") }
    var activo by rememberSaveable { mutableStateOf(true) }

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(servicios, servicioId) {
        if (esEdicion && !cargadoInicial) {
            val encontrado = servicios.firstOrNull { it.id == servicioId }
            if (encontrado != null) {
                nombre = encontrado.nombre
                unidad = encontrado.unidad.uppercase()
                precioInput = String.format(Locale.US, "%.2f", encontrado.precio)
                horasEntrega = encontrado.horasEntrega
                descripcion = encontrado.descripcion
                icono = encontrado.icono
                activo = encontrado.activo
                cargadoInicial = true
            }
        }
    }

    LaunchedEffect(exitoGuardado, exitoEliminado) {
        if (exitoGuardado || exitoEliminado) {
            viewModel.resetearEstadoOperacion()
            onGuardadoExito()
        }
    }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    val operando = guardando || eliminando

    if (mostrarDialogoEliminar && servicioActual != null) {
        AlertDialog(
            onDismissRequest = { if (!operando) mostrarDialogoEliminar = false },
            title = {
                Text(
                    text = "¿Eliminar servicio?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar permanentemente el servicio '${servicioActual?.nombre}'?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEliminar = false
                        viewModel.eliminar(servicioActual!!)
                    },
                    enabled = !operando
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false },
                    enabled = !operando
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    LavaScaffold(
        rol = Rol.ADMIN,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    enabled = !operando,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (esEdicion) "Editar servicio" else "Nuevo servicio",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )

                if (esEdicion) {
                    IconButton(
                        onClick = { mostrarDialogoEliminar = true },
                        enabled = !operando && servicioActual != null,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Eliminar servicio",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            if (esEdicion && cargando && !cargadoInicial) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (esEdicion && !cargando && servicioActual == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "El servicio solicitado no fue encontrado",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. Nombre
                    LavaTextField(
                        value = nombre,
                        onValueChange = { if (!operando) nombre = it },
                        label = "Nombre del servicio",
                        enabled = !operando
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 2. Se cobra por (Unidad)
                    Text(
                        text = "Se cobra por",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("KG" to "Kilo", "PRENDA" to "Prenda", "UNIDAD" to "Unidad").forEach { (cod, label) ->
                            val estaSeleccionado = unidad == cod
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(enabled = !operando) { unidad = cod }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Medium,
                                        color = if (estaSeleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 3. Precio (S/)
                    LavaTextField(
                        value = precioInput,
                        onValueChange = { if (!operando) precioInput = it },
                        label = "Precio (S/)",
                        enabled = !operando,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4. Tiempo de entrega
                    Text(
                        text = "Tiempo de entrega",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(24 to "24 h", 48 to "48 h", 72 to "72 h").forEach { (h, label) ->
                            val estaSeleccionado = horasEntrega == h
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = 1.dp,
                                        color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(enabled = !operando) { horasEntrega = h }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (estaSeleccionado) FontWeight.Bold else FontWeight.Medium,
                                        color = if (estaSeleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 5. Descripción
                    LavaTextField(
                        value = descripcion,
                        onValueChange = { if (!operando) descripcion = it },
                        label = "Descripción",
                        enabled = !operando
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6. Ícono
                    Text(
                        text = "Ícono",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("wash", "iron", "shirt").forEach { icoName ->
                            val estaSeleccionado = icono == icoName
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (estaSeleccionado) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (estaSeleccionado) 2.dp else 1.dp,
                                        color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(enabled = !operando) { icono = icoName }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    IconoServicio(
                                        icono = icoName,
                                        modifier = Modifier.size(30.dp),
                                        color = if (estaSeleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 7. Servicio activo Switch
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, Color(0xFFF1F0F5), RoundedCornerShape(18.dp)),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Servicio activo",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Los estudiantes podrán pedirlo",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = activo,
                                onCheckedChange = { if (!operando) activo = it },
                                enabled = !operando,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 8. Action Buttons
                    if (operando) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val sAInsertar = Servicio(
                                        id = if (esEdicion) servicioId ?: 0 else 0,
                                        nombre = nombre,
                                        descripcion = descripcion,
                                        unidad = unidad,
                                        precio = 0.0, // Calculated in viewModel from precioInput
                                        horasEntrega = horasEntrega,
                                        activo = activo,
                                        icono = icono
                                    )
                                    viewModel.guardar(sAInsertar, precioInput)
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Guardar servicio",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            OutlinedButton(
                                onClick = onBackClick,
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Cancelar",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
