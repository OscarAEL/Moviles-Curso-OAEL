package com.eneque.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eneque.murocampus.ui.theme.*
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(
    vm: PostsViewModel, id: Int, modifier: Modifier = Modifier,
    paddingSistema: PaddingValues = PaddingValues(), onVolver: () -> Unit = {}
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val eliminacion by vm.eliminacion.collectAsStateWithLifecycle()
    var confirmar by rememberSaveable(id) { mutableStateOf(false) }
    if (confirmar) {
        Dialog(onDismissRequest = { confirmar = false }) {
            PanelGrafico(Modifier.fillMaxWidth()) {
                EtiquetaGrafica("CONFIRMACIÓN", RosaCampus)
                Text("Eliminar publicación", style = MaterialTheme.typography.headlineMedium)
                Text("El servicio simula el cambio: la publicación no se borra del servidor.")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonGrafico("Cancelar", { confirmar = false }, Modifier.weight(1f), fondo = MaterialTheme.colorScheme.surfaceVariant)
                    BotonGrafico("Eliminar", { confirmar = false; vm.eliminar(id) }, Modifier.weight(1f), fondo = RosaCampus)
                }
            }
        }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = paddingSistema,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { EncabezadoCampus(onVolver = onVolver) }
        when (val actual = estado) {
            UiState.Cargando -> item { Cargando(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) }
            is UiState.Error -> item {
                ErrorConReintento(actual.mensaje, { vm.abrir(id) }, Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
            }
            is UiState.Exito -> {
                item {
                    PanelGrafico(Modifier.padding(horizontal = 20.dp)) {
                        EtiquetaUsuario(actual.datos.post.userId)
                        Text(actual.datos.post.title, style = MaterialTheme.typography.headlineMedium)
                        Surface(color = LimaCampus, shape = CircleShape, border = BorderStroke(1.5.dp, TintaCampus)) {
                            Spacer(Modifier.width(68.dp).height(6.dp))
                        }
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(18.dp)) {
                            Text(actual.datos.post.body, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                item {
                    Column(
                        Modifier.padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        BotonGrafico(
                            if (eliminacion == UiState.Cargando) "Eliminando…" else "Eliminar",
                            { confirmar = true }, fondo = RosaCampus, simbolo = Simbolo.Eliminar,
                            enabled = eliminacion != UiState.Cargando && eliminacion !is UiState.Exito
                        )
                        when (val resultado = eliminacion) {
                            null, UiState.Cargando -> Unit
                            is UiState.Exito -> AvisoGrafico("Eliminado (simulación)", "El servidor no guarda este cambio.")
                            is UiState.Error -> ErrorConReintento(resultado.mensaje, { vm.eliminar(id) })
                        }
                    }
                }
                item {
                    Surface(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = AzulElectrico, contentColor = Color.White,
                        shape = CircleShape, border = BorderStroke(2.dp, TintaCampus)
                    ) {
                        Text(
                            "Comentarios (${actual.datos.comentarios.size})",
                            Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                itemsIndexed(actual.datos.comentarios, key = { _, comentario -> comentario.id }) { indice, comentario ->
                    PanelGrafico(
                        Modifier.padding(start = if (indice % 2 == 0) 20.dp else 34.dp, end = if (indice % 2 == 0) 34.dp else 20.dp)
                    ) {
                        Surface(color = LavandaCampus, shape = RoundedCornerShape(14.dp)) {
                            Text(
                                "@  ${comentario.email}",
                                Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                color = AzulElectrico, style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Text(comentario.body, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}
