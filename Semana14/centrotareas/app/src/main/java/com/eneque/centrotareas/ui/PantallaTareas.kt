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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eneque.centrotareas.model.CargaUi
import com.eneque.centrotareas.model.PrimosUi
import com.eneque.centrotareas.ui.theme.CentroTareasTheme
import com.eneque.centrotareas.viewmodel.TareasViewModel

@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    ContenidoTareas(
        mensaje = mensaje,
        progreso = progreso,
        corriendo = corriendo,
        onTareaLarga = viewModel::tareaLarga,
        onCancelar = viewModel::cancelar,
        primos = primos,
        onContar = viewModel::contar,
        onCancelarPrimos = viewModel::cancelarPrimos,
        carga = carga,
        onCargarUnaPorUna = viewModel::cargarUnaPorUna,
        onCargarALaVez = viewModel::cargarALaVez,
        simularError = simularError,
        onCambiarSimularError = viewModel::cambiarSimularError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContenidoTareas(
    mensaje: String,
    progreso: Float,
    corriendo: Boolean,
    onTareaLarga: () -> Unit,
    onCancelar: () -> Unit,
    primos: PrimosUi,
    onContar: (Int) -> Unit,
    onCancelarPrimos: () -> Unit,
    carga: CargaUi,
    onCargarUnaPorUna: () -> Unit,
    onCargarALaVez: () -> Unit,
    simularError: Boolean,
    onCambiarSimularError: (Boolean) -> Unit
) {
    var toques by rememberSaveable { mutableStateOf(0) }
    var hasta by rememberSaveable { mutableStateOf("500000") }
    val limite = hasta.toIntOrNull()?.takeIf { it >= 2 }
    val calculandoPrimos = primos is PrimosUi.Calculando
    val cargando = carga is CargaUi.Cargando

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
                        enabled = !calculandoPrimos,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { limite?.let(onContar) },
                            enabled = limite != null && !calculandoPrimos
                        ) {
                            Text("Contar")
                        }
                        OutlinedButton(
                            onClick = onCancelarPrimos,
                            enabled = calculandoPrimos
                        ) {
                            Text("Cancelar")
                        }
                    }
                    when (primos) {
                        PrimosUi.Inicial -> Text("Sin cálculo")
                        PrimosUi.Calculando -> {
                            CircularProgressIndicator()
                            Text("Calculando...")
                        }
                        PrimosUi.Cancelado -> Text("Cancelado")
                        is PrimosUi.Listo -> Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFFD8F3DC),
                            contentColor = Color(0xFF174B26)
                        ) {
                            Text(
                                text = "Primos: ${primos.cantidad}\nTiempo: ${primos.milisegundos} ms",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
            item {
                TarjetaTarea(titulo = "Dos cargas") {
                    Text("Perfil y pedidos ficticios")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Simular error")
                        Switch(
                            modifier = Modifier.semantics { contentDescription = "Simular error" },
                            checked = simularError,
                            onCheckedChange = onCambiarSimularError,
                            enabled = !cargando
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onCargarUnaPorUna, enabled = !cargando) {
                            Text("Una por una")
                        }
                        Button(onClick = onCargarALaVez, enabled = !cargando) {
                            Text("A la vez")
                        }
                    }
                    when (carga) {
                        CargaUi.Inicial -> Text("Sin cargas")
                        CargaUi.Cargando -> {
                            CircularProgressIndicator()
                            Text("Cargando...")
                        }
                        is CargaUi.Exito -> Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFFD8F3DC),
                            contentColor = Color(0xFF174B26)
                        ) {
                            Text(
                                text = "Modo: ${carga.modo}\nPerfil: ${carga.perfil}\n" +
                                    "Pedidos: ${carga.pedidos}\nTiempo: ${carga.milisegundos} ms",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        is CargaUi.Error -> Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(carga.mensaje)
                                Button(onClick = onCargarALaVez) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
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
            onCancelar = {},
            primos = PrimosUi.Inicial,
            onContar = {},
            onCancelarPrimos = {},
            carga = CargaUi.Inicial,
            onCargarUnaPorUna = {},
            onCargarALaVez = {},
            simularError = false,
            onCambiarSimularError = {}
        )
    }
}
