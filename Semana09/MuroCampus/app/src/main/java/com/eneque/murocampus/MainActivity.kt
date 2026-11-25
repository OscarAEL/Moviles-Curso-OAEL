package com.eneque.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import com.eneque.murocampus.ui.PantallaCrear
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.eneque.murocampus.ui.PantallaDetalle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eneque.murocampus.data.PostRepositoryRemote
import com.eneque.murocampus.data.remote.RetrofitClient
import com.eneque.murocampus.ui.PantallaLista
import com.eneque.murocampus.ui.theme.MuroCampusTheme
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                MuroCampusApp(vm)
            }
        }
    }
}

@Composable
fun MuroCampusApp(vm: PostsViewModel) {
    var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
    var creando by rememberSaveable { mutableStateOf(false) }
    val listaState = rememberLazyListState()
    val volver = {
        vm.cerrarDetalle()
        vm.reiniciarCreacion()
        creando = false
        idSeleccionado = null
    }
    BackHandler(enabled = idSeleccionado != null || creando) { volver() }
    LaunchedEffect(idSeleccionado) { idSeleccionado?.let(vm::abrir) }
    MarcoCampus(
        onVolver = if (idSeleccionado != null || creando) volver else null,
        onCrear = if (idSeleccionado == null && !creando) ({
            vm.reiniciarCreacion()
            creando = true
        }) else null
    ) { modifier ->
        val id = idSeleccionado
        if (creando) {
            PantallaCrear(vm, modifier)
        } else if (id == null) {
            PantallaLista(
                vm, onAbrir = { idSeleccionado = it },
                modifier = modifier, listaState = listaState
            )
        } else {
            PantallaDetalle(vm, id, modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarcoCampus(
    onVolver: (() -> Unit)? = null,
    onCrear: (() -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            onCrear?.let { crear ->
                FloatingActionButton(onClick = crear) { Text("+", style = MaterialTheme.typography.headlineMedium) }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("MuroCampus") },
                navigationIcon = {
                    onVolver?.let { volver ->
                        TextButton(onClick = volver) {
                            Text("Atrás", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        content(Modifier.fillMaxSize().padding(innerPadding))
    }
}

@Preview(showBackground = true)
@Composable
private fun MuroCampusPreview() {
    MuroCampusTheme {
        MarcoCampus {}
    }
}
