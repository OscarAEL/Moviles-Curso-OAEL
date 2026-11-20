package com.eneque.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eneque.centrotareas.ui.theme.CentroTareasTheme
import com.eneque.centrotareas.viewmodel.TareasViewModel

@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    ContenidoTareas(
        mensaje = mensaje,
        progreso = progreso,
        corriendo = corriendo,
        onTareaLarga = viewModel::tareaLarga,
        onCancelar = viewModel::cancelar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoTareas(
    mensaje: String,
    progreso: Float,
    corriendo: Boolean,
    onTareaLarga: () -> Unit,
    onCancelar: () -> Unit
) {
    var toques by rememberSaveable { mutableStateOf(0) }
    var hasta by rememberSaveable { mutableStateOf("500000") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Hilos en Android",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("Toca el contador para comprobar que la pantalla responde.")
                    Button(onClick = { toques++ }) {
                        Text("Tócame: $toques")
                    }
                }
            }
            item {
                TarjetaTarea(titulo = "Tarea con progreso") {
                    Text(mensaje)
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("${(progreso * 100).toInt()} %")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onTareaLarga, enabled = !corriendo) {
                            Text("Iniciar")
                        }
                        OutlinedButton(onClick = onCancelar, enabled = corriendo) {
                            Text("Cancelar")
                        }
                    }
                }
            }
            item {
                TarjetaTarea(titulo = "Contar números primos") {
                    OutlinedTextField(
                        value = hasta,
                        onValueChange = { hasta = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Hasta N") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    // TODO Paso 5: validar N y conectar el cálculo y su estado.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}, enabled = false) { Text("Contar") }
                        OutlinedButton(onClick = {}, enabled = false) { Text("Cancelar") }
                    }
                    Text("Sin cálculo")
                }
            }
            item {
                TarjetaTarea(titulo = "Dos cargas") {
                    Text("Perfil y pedidos ficticios")
                    // TODO Pasos 6 y 7: conectar cargas, error simulado y reintento.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {}, enabled = false) { Text("Una por una") }
                        Button(onClick = {}, enabled = false) { Text("A la vez") }
                    }
                    Text("Sin cargas")
                }
            }
        }
    }
}

@Composable
private fun TarjetaTarea(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            contenido()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PantallaTareasPreview() {
    CentroTareasTheme {
        ContenidoTareas(
            mensaje = "Listo",
            progreso = 0f,
            corriendo = false,
            onTareaLarga = {},
            onCancelar = {}
        )
    }
}
