package com.eneque.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.murocampus.ui.theme.*

@Composable
fun TarjetaPost(
    userId: Int, titulo: String, texto: String, onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().sombraDura(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, TintaCampus),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EtiquetaUsuario(userId)
            Text(
                titulo, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, lineHeight = 24.sp),
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(
                texto, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun Cargando(modifier: Modifier = Modifier) {
    PanelGrafico(modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Surface(color = LimaCampus, shape = CircleShape, border = BorderStroke(2.dp, TintaCampus)) {
                Box(Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(36.dp), color = AzulElectrico, strokeWidth = 4.dp)
                }
            }
            Text("Cargando publicaciones…", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ErrorConReintento(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    PanelGrafico(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.errorContainer) {
        Surface(color = RosaCampus, shape = CircleShape, border = BorderStroke(2.dp, TintaCampus)) {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Aviso) }
        }
        Text("Algo salió mal", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onErrorContainer)
        Text(mensaje, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
        BotonGrafico("Reintentar", onReintentar, Modifier.fillMaxWidth())
    }
}

@Composable
fun SinPublicaciones(modifier: Modifier = Modifier) {
    PanelGrafico(modifier.fillMaxWidth()) {
        Surface(color = LimaCampus, shape = RoundedCornerShape(16.dp), border = BorderStroke(2.dp, TintaCampus)) {
            Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Documento, Modifier.size(32.dp)) }
        }
        Text("Sin publicaciones", style = MaterialTheme.typography.titleLarge)
        Text("Prueba con otro título o usuario.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AvisoGrafico(titulo: String, descripcion: String, modifier: Modifier = Modifier) {
    PanelGrafico(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = LimaCampus, shape = CircleShape, border = BorderStroke(1.5.dp, TintaCampus)) {
                Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Comprobar, Modifier.size(20.dp)) }
            }
            Text(titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        }
        Text(descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
