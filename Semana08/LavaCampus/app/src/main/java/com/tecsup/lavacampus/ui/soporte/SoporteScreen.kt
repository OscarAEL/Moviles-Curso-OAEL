package com.tecsup.lavacampus.ui.soporte

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun SoporteScreen(usuario: Usuario, viewModel: SoporteViewModel, sesion: SesionViewModel) {
    val servicios by viewModel.servicios.collectAsState()
    val error by viewModel.error.collectAsState()
    val saliendo by sesion.cargando.collectAsState()
    val host = ErrorDePantalla(error, viewModel::limpiarError)
    LavaScaffold(rol = Rol.SOPORTE, topBar = { Cabecera("Centro de soporte") }, snackbarHost = { SnackbarHost(host) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Avatar(usuario.nombre)
                    Column(Modifier.padding(start = 14.dp)) {
                        Text(usuario.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Secundario("Código ${usuario.codigo}")
                        Secundario(usuario.correo)
                    }
                }
            }
            Aviso("Bienvenido al perfil Soporte. Consulta aquí los servicios disponibles de LavaCampus.")
            Seccion("Servicios disponibles")
            servicios.forEach { servicio ->
                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(servicio.nombre, fontWeight = FontWeight.Bold); Secundario("Por ${servicio.unidad.lowercase()} · ${servicio.horasEntrega} h") }
                    Text(dinero(servicio.precio), fontWeight = FontWeight.Bold)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            TextButton(onClick = sesion::cerrarSesion, enabled = !saliendo, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) }
        }
    }
}
