package com.eneque.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(vm: PostsViewModel, id: Int, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val eliminacion by vm.eliminacion.collectAsStateWithLifecycle()
    var confirmar by rememberSaveable(id) { mutableStateOf(false) }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Enviar la eliminación de esta publicación? El servicio solo simula el cambio.") },
            confirmButton = {
                TextButton(onClick = { confirmar = false; vm.eliminar(id) }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text("Cancelar") }
            }
        )
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val actual = estado) {
            UiState.Cargando -> Cargando()
            is UiState.Error -> ErrorConReintento(actual.mensaje, { vm.abrir(id) })
            is UiState.Exito -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Text(actual.datos.post.title, style = MaterialTheme.typography.titleLarge) }
                item { Text(actual.datos.post.body) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { confirmar = true },
                            enabled = eliminacion != UiState.Cargando && eliminacion !is UiState.Exito,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) { Text(if (eliminacion == UiState.Cargando) "Eliminando…" else "Eliminar") }
                        when (val resultado = eliminacion) {
                            null, UiState.Cargando -> Unit
                            is UiState.Exito -> Text(
                                "Eliminado (simulación). El servidor no guarda este cambio.",
                                color = MaterialTheme.colorScheme.primary
                            )
                            is UiState.Error -> ErrorConReintento(resultado.mensaje, { vm.eliminar(id) })
                        }
                    }
                }
                item {
                    Text(
                        "Comentarios (${actual.datos.comentarios.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(actual.datos.comentarios, key = { it.id }) { comentario ->
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(comentario.email, color = MaterialTheme.colorScheme.primary)
                            Text(comentario.body)
                        }
                    }
                }
            }
        }
    }
}
