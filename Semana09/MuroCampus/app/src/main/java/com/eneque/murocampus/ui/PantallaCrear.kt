package com.eneque.murocampus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eneque.murocampus.ui.theme.LimaCampus
import com.eneque.murocampus.ui.theme.RosaCampus
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(
    vm: PostsViewModel, modifier: Modifier = Modifier,
    paddingSistema: PaddingValues = PaddingValues(), onVolver: () -> Unit = {}
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }
    val estado by vm.envio.collectAsStateWithLifecycle()
    val enviando = estado == UiState.Cargando
    val desplazamiento = rememberLazyListState()
    val teclado = LocalSoftwareKeyboardController.current
    val foco = LocalFocusManager.current
    LaunchedEffect(estado) {
        if (estado is UiState.Exito) {
            titulo = ""
            texto = ""
        }
        if (estado is UiState.Exito || estado is UiState.Error) {
            teclado?.hide()
            foco.clearFocus()
            desplazamiento.scrollToItem(0)
        }
    }
    LazyColumn(
        state = desplazamiento,
        modifier = modifier.fillMaxSize(), contentPadding = paddingSistema,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { EncabezadoCampus(titulo = "Nueva publicación", grande = true, onVolver = onVolver) }
        when (val actual = estado) {
            is UiState.Exito -> item {
                AvisoGrafico(
                    "Publicado con id ${actual.datos.id}",
                    "El servicio simula la creación; la publicación no se guarda en el servidor.",
                    Modifier.padding(horizontal = 20.dp)
                )
            }
            is UiState.Error -> item {
                ErrorConReintento(actual.mensaje, { vm.publicar(titulo, texto) }, Modifier.padding(horizontal = 20.dp))
            }
            else -> Unit
        }
        item {
            CampoGrafico(
                titulo, { titulo = it }, "Título", LimaCampus,
                Modifier.padding(horizontal = 20.dp), enabled = !enviando
            )
        }
        item {
            CampoGrafico(
                texto, { texto = it }, "Contenido", RosaCampus,
                Modifier.padding(horizontal = 20.dp), enabled = !enviando, multilinea = true
            )
        }
        item {
            Text(
                "El servicio simula la creación; la publicación no se guarda en el servidor.",
                Modifier.padding(horizontal = 22.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            BotonGrafico(
                if (enviando) "Publicando…" else "Publicar",
                { vm.publicar(titulo, texto) },
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                enabled = titulo.isNotBlank() && texto.isNotBlank() && !enviando,
                simbolo = Simbolo.Enviar
            )
        }
        if (enviando) item {
            Text("Enviando publicación…", Modifier.padding(horizontal = 20.dp), style = MaterialTheme.typography.labelLarge)
        }
        item { Spacer(Modifier.height(20.dp)) }
    }
}
