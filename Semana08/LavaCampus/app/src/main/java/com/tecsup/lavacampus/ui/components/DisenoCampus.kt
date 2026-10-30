package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import java.util.Locale

fun dinero(valor: Double) = String.format(Locale.US, "S/ %.2f", valor)

fun numero(id: Int) = "#" + id.toString().padStart(3, '0')

@Composable
fun Cabecera(
    titulo: String,
    volver: (() -> Unit)? = null,
    accion: @Composable RowScope.() -> Unit = {},
) {
    Row(
        Modifier.fillMaxWidth()
            .statusBarsPadding()
            .padding(
                start = if (volver == null) 20.dp else 8.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 12.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (volver != null)
            IconButton(onClick = volver) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    "Volver",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        Text(
            titulo,
            Modifier.weight(1f),
            fontSize = 23.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        accion()
    }
}

@Composable
fun Secundario(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
fun Seccion(texto: String) {
    Text(
        texto.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
    )
}

@Composable
fun Avatar(nombre: String, modifier: Modifier = Modifier) {
    Box(
        modifier.size(46.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            nombre
                .split(' ')
                .filter { it.isNotBlank() }
                .take(2)
                .joinToString("") { it.take(1) }
                .uppercase(),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun Aviso(texto: String, error: Boolean = false) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color =
            if (error) MaterialTheme.colorScheme.errorContainer
            else MaterialTheme.colorScheme.primaryContainer,
        contentColor =
            if (error) MaterialTheme.colorScheme.onErrorContainer
            else MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(texto, Modifier.padding(14.dp), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun FilaImporte(etiqueta: String, importe: String, destacado: Boolean = false) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            etiqueta,
            Modifier.weight(1f).padding(end = 12.dp),
            fontSize = if (destacado) 15.sp else 13.sp,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal,
        )
        Text(importe, fontWeight = FontWeight.Bold, fontSize = if (destacado) 23.sp else 13.sp)
    }
}

@Composable
fun CasilleroDestacado(casillero: String) {
    Surface(
        Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Lock,
                null,
                Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(Modifier.padding(start = 16.dp)) {
                Secundario("Recógelo en el casillero")
                Text(casillero, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun EstadoVacio(
    titulo: String,
    descripcion: String,
    accion: String? = null,
    onAccion: () -> Unit = {},
) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Icon(
                Icons.Outlined.LocalLaundryService,
                null,
                Modifier.padding(24.dp).size(36.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        Secundario(descripcion)
        if (accion != null)
            Button(onClick = onAccion, shape = RoundedCornerShape(16.dp)) { Text(accion) }
    }
}

@Composable
fun ErrorDePantalla(error: String?, limpiar: () -> Unit): SnackbarHostState {
    val host = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            host.showSnackbar(it)
            limpiar()
        }
    }
    return host
}

@Composable
fun ConfirmarCancelacion(
    visible: Boolean,
    operando: Boolean,
    cerrar: () -> Unit,
    confirmar: () -> Unit,
) {
    if (visible)
        AlertDialog(
            onDismissRequest = { if (!operando) cerrar() },
            title = { Text("¿Cancelar pedido?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = confirmar, enabled = !operando) {
                    Text("Cancelar pedido", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = cerrar, enabled = !operando) { Text("Volver") }
            },
        )
}

@Composable
fun PildoraNavegacion(
    actual: Int,
    etiquetas: List<String>,
    iconos: List<ImageVector>,
    acciones: List<() -> Unit>,
) {
    Box(
        Modifier.fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(40.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            etiquetas.forEachIndexed { i, etiqueta ->
                val activo = i == actual
                Row(
                    Modifier.weight(if (activo) 1.8f else 1f)
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            if (activo) MaterialTheme.colorScheme.secondaryContainer
                            else Color.Transparent
                        )
                        .semantics {
                            selected = activo
                            role = Role.Tab
                            contentDescription = etiqueta
                        }
                        .clickable(onClick = acciones[i]),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        iconos[i],
                        null,
                        Modifier.size(21.dp),
                        tint =
                            if (activo) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.secondaryContainer,
                    )
                    if (activo)
                        Text(
                            etiqueta,
                            Modifier.padding(start = 6.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                }
            }
        }
    }
}
