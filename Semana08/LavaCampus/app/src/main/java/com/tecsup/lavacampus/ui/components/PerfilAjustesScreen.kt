package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.viewmodel.PreferenciasViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import java.util.Locale

@Composable
fun PerfilAjustesScreen(
    usuario: Usuario,
    viewModel: PreferenciasViewModel,
    sesionViewModel: SesionViewModel,
    onBackClick: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    val admin = usuario.rol == Rol.ADMIN.name
    val ajustes by viewModel.ajustes.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()
    val errorPreferencias by viewModel.error.collectAsState()
    val errorSesion by sesionViewModel.error.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(errorPreferencias, errorSesion) {
        (errorPreferencias ?: errorSesion)?.let {
            snackbar.showSnackbar(it)
            viewModel.limpiarError()
            sesionViewModel.limpiarError()
        }
    }
    LavaScaffold(
        rol = if (admin) Rol.ADMIN else Rol.CLIENTE,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = bottomBar,
        topBar = {
            Box(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp)) {
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                }
                Text(if (admin) "Ajustes" else "Perfil", Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                            Text(usuario.nombre.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) },
                                color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(usuario.nombre, fontWeight = FontWeight.Bold)
                        if (usuario.codigo.isNotBlank()) Text(usuario.codigo, style = MaterialTheme.typography.bodySmall)
                        Text(usuario.correo, style = MaterialTheme.typography.bodySmall)
                        Text(if (admin) "Perfil activo · Administrador" else "Estudiante", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (!admin) {
                val stats = estadisticas
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    EstadisticaPerfil(stats?.activos?.toString() ?: "—", "Pedidos activos", Modifier.weight(1f))
                    EstadisticaPerfil(stats?.totales?.toString() ?: "—", "Pedidos totales", Modifier.weight(1f))
                    EstadisticaPerfil(stats?.let { String.format(Locale.US, "%.1f kg", it.kilos) } ?: "—", "Lavados", Modifier.weight(1f))
                }
            }
            Button(onClick = sesionViewModel::cambiarPerfil, enabled = !cargando && !guardando,
                shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.SwapHoriz, null)
                Column(Modifier.padding(8.dp).weight(1f)) {
                    Text("Cambiar de perfil", fontWeight = FontWeight.Bold)
                    Text("Modo práctica: ir a vista ${if (admin) "Estudiante" else "Administrador"}", style = MaterialTheme.typography.labelSmall)
                }
                Text("›", fontSize = 24.sp)
            }
            Text("Preferencias", fontWeight = FontWeight.Bold)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(horizontal = 12.dp)) {
                    FilaPreferencia("Modo oscuro", "Cambia el tema de la app", ajustes.oscuro, !guardando && !cargando, viewModel::modoOscuro)
                    HorizontalDivider()
                    FilaPreferencia(if (admin) "Notificaciones" else "Avisos de pedido listo",
                        if (admin) "Avisar pedidos nuevos" else "Preferencia de avisos para recoger",
                        ajustes.avisos, !guardando && !cargando, viewModel::notificaciones)
                    if (admin) {
                        HorizontalDivider()
                        FilaPreferencia("Recibir pedidos", "Abrir o cerrar la recepción", ajustes.recibir, !guardando && !cargando, viewModel::recibirPedidos)
                    }
                }
            }
            Text("Tus preferencias se guardan en este dispositivo.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = sesionViewModel::cerrarSesion, enabled = !cargando && !guardando,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text(if (cargando) "Espera…" else "Cerrar sesión")
            }
        }
    }
}

@Composable
private fun EstadisticaPerfil(valor: String, etiqueta: String, modifier: Modifier) {
    Surface(modifier, shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer) {
        Column(Modifier.padding(horizontal = 4.dp, vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(etiqueta, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun FilaPreferencia(titulo: String, descripcion: String, valor: Boolean, habilitado: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(end = 8.dp)) {
            Text(titulo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = valor, onCheckedChange = onChange, enabled = habilitado)
    }
}
