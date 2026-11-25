package com.eneque.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eneque.murocampus.data.PostRepositoryRemote
import com.eneque.murocampus.data.remote.RetrofitClient
import com.eneque.murocampus.ui.*
import com.eneque.murocampus.ui.theme.*
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    private val vm: PostsViewModel by viewModels {
        PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent { MuroCampusTheme { MuroCampusApp(vm) } }
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
        onCrear = if (idSeleccionado == null && !creando) ({
            vm.reiniciarCreacion()
            creando = true
        }) else null
    ) { paddingSistema ->
        val id = idSeleccionado
        val modifier = Modifier.fillMaxSize().consumeWindowInsets(paddingSistema)
        when {
            creando -> PantallaCrear(vm, modifier, paddingSistema, volver)
            id != null -> PantallaDetalle(vm, id, modifier, paddingSistema, volver)
            else -> PantallaLista(
                vm, modifier = modifier, onAbrir = { idSeleccionado = it },
                listaState = listaState, paddingSistema = paddingSistema
            )
        }
    }
}

@Composable
private fun MarcoCampus(
    onCrear: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val tecladoVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Box(Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
        bottomBar = {
            if (!tecladoVisible) onCrear?.let { crear ->
                Box(
                    Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        onClick = crear,
                        modifier = Modifier.size(62.dp).sombraDura(100.dp)
                            .semantics { contentDescription = "Crear publicación" },
                        shape = CircleShape, color = LimaCampus, border = BorderStroke(2.dp, TintaCampus)
                    ) {
                        Box(contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Sumar, Modifier.size(32.dp)) }
                    }
                }
            }
        },
        content = content
    )
    Spacer(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars).background(AzulElectrico))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun MuroCampusPreview() {
    MuroCampusTheme(darkTheme = false) {
        MarcoCampus {
            Column {
                EncabezadoCampus(grande = true, contador = "100 publicaciones")
                Spacer(Modifier.height(20.dp))
                CampoBusqueda("", {}, Modifier.padding(horizontal = 20.dp))
                Spacer(Modifier.height(24.dp))
                TarjetaPost(1, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", "quia et suscipit\nsuscipit recusandae consequuntur...", {}, Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}
