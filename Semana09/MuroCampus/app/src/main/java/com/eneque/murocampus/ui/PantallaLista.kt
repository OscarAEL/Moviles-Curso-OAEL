package com.eneque.murocampus.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eneque.murocampus.viewmodel.PostsViewModel
import com.eneque.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    modifier: Modifier = Modifier,
    onAbrir: (Int) -> Unit = {},
    listaState: LazyListState = rememberLazyListState(),
    paddingSistema: PaddingValues = PaddingValues()
) {
    val estado by vm.listaFiltrada.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val contador = when (val actual = estado) {
        is UiState.Exito -> "${actual.datos.size} publicaciones"
        UiState.Cargando -> "Conectando…"
        is UiState.Error -> "Muro del campus"
    }
    LazyColumn(
        state = listaState,
        modifier = modifier.fillMaxSize(),
        contentPadding = paddingSistema
    ) {
        item(key = "encabezado") { EncabezadoCampus(grande = true, contador = contador) }
        item(key = "busqueda") {
            CampoBusqueda(busqueda, vm::buscar, Modifier.padding(horizontal = 20.dp).offset(y = (-20).dp))
        }
        item(key = "filtros") {
            LazyRow(
                contentPadding = PaddingValues(start = 20.dp, end = 24.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { ChipGrafico("Todos", filtro == null) { vm.filtrar(null) } }
                items((1..10).toList()) { id ->
                    ChipGrafico("Usuario $id", filtro == id) { vm.filtrar(id) }
                }
            }
        }
        when (val actual = estado) {
            UiState.Cargando -> item { Cargando(Modifier.padding(horizontal = 20.dp, vertical = 28.dp)) }
            is UiState.Error -> item {
                ErrorConReintento(actual.mensaje, vm::cargar, Modifier.padding(horizontal = 20.dp, vertical = 28.dp))
            }
            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    item { SinPublicaciones(Modifier.padding(horizontal = 20.dp, vertical = 28.dp)) }
                } else {
                    items(actual.datos, key = { "post-${it.id}" }) { post ->
                        TarjetaPost(
                            post.userId, post.title, post.body, { onAbrir(post.id) },
                            Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
        item(key = "espacio-final") { Spacer(Modifier.height(16.dp)) }
    }
}
