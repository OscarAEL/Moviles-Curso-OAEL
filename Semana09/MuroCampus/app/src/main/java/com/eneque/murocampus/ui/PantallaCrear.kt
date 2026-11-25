package com.eneque.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }
    val estado by vm.envio.collectAsStateWithLifecycle()
    val enviando = estado == UiState.Cargando
    LaunchedEffect(estado) {
        if (estado is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }
    Column(
        modifier = modifier.imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear publicación", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = titulo, onValueChange = { titulo = it },
            label = { Text("Título") }, singleLine = true,
            enabled = !enviando, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = texto, onValueChange = { texto = it },
            label = { Text("Contenido") }, minLines = 4,
            enabled = !enviando, modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.publicar(titulo, texto) },
            enabled = titulo.isNotBlank() && texto.isNotBlank() && !enviando,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (enviando) "Publicando…" else "Publicar") }
        when (val actual = estado) {
            null -> Unit
            UiState.Cargando -> Text("Enviando publicación…")
            is UiState.Error -> ErrorConReintento(actual.mensaje, { vm.publicar(titulo, texto) })
            is UiState.Exito -> {
                Text(
                    "Publicado con id ${actual.datos.id}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("El servicio simula la creación; la publicación no se guarda en el servidor.")
            }
        }
    }
}
