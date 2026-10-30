package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun PerfilAjustesScreen(
    usuario: Usuario,
    viewModel: PreferenciasViewModel,
    sesionViewModel: SesionViewModel,
    onBackClick: () -> Unit,
    bottomBar: @Composable () -> Unit,
) {
    val admin = usuario.rol == "ADMIN"
    val ajustes by viewModel.ajustes.collectAsState()
    val estadisticas by viewModel.estadisticas.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val errorSesion by sesionViewModel.error.collectAsState()
    val host =
        ErrorDePantalla(error ?: errorSesion) {
            viewModel.limpiarError()
            sesionViewModel.limpiarError()
        }
    LavaScaffold(
        rol = if (admin) Rol.ADMIN else Rol.CLIENTE,
        topBar = { if (admin) Cabecera("Ajustes") },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(host) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(usuario.nombre)
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(usuario.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                    Secundario(usuario.correo)
                    if (!admin) Secundario("Código ${usuario.codigo}")
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            if (!admin) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    EstadisticaPerfil(estadisticas?.activos?.toString() ?: "—", "Activos")
                    EstadisticaPerfil(estadisticas?.totales?.toString() ?: "—", "Pedidos")
                    EstadisticaPerfil(
                        estadisticas?.let {
                            java.lang.String.format(java.util.Locale.US, "%.1f", it.kilos)
                        } ?: "—",
                        "Kilos lavados",
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            Seccion("Preferencias")
            FilaPreferencia("Modo oscuro", "No disponible", false, false) {}
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            FilaPreferencia(
                if (admin) "Notificaciones" else "Avisos de pedido",
                if (admin) "Avisos de nuevos pedidos" else "Cuando cambie su estado",
                ajustes.avisos,
                !guardando && !cargando,
                viewModel::notificaciones,
            )
            if (admin) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaPreferencia(
                    "Recibir pedidos",
                    "Si se apaga, los estudiantes verán que la lavandería no está recibiendo pedidos",
                    ajustes.recibir,
                    !guardando && !cargando,
                    viewModel::recibirPedidos,
                )
            }
            OutlinedButton(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                onClick = sesionViewModel::cambiarPerfil,
                enabled = !guardando && !cargando,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
            ) {
                Text("Cambiar de perfil", fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = sesionViewModel::cerrarSesion,
                enabled = !guardando && !cargando,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Cerrar sesión",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun EstadisticaPerfil(valor: String, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontWeight = FontWeight.ExtraBold, fontSize = 27.sp)
        Secundario(etiqueta)
    }
}

@Composable
private fun FilaPreferencia(
    titulo: String,
    descripcion: String,
    valor: Boolean,
    habilitado: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Secundario(descripcion)
        }
        Switch(checked = valor, enabled = habilitado, onCheckedChange = onChange)
    }
}
