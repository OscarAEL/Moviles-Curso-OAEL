package com.eneque.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    listaState: LazyListState = rememberLazyListState()
) {
    val estado by vm.lista.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filtro == null, onClick = { vm.filtrar(null) },
                    label = { Text("Todos") }
                )
            }
            items((1..10).toList()) { id ->
                FilterChip(
                    selected = filtro == id, onClick = { vm.filtrar(id) },
                    label = { Text("Usuario $id") }
                )
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (val actual = estado) {
                UiState.Cargando -> Cargando()
                is UiState.Error -> ErrorConReintento(actual.mensaje, vm::cargar)
                is UiState.Exito -> {
                    if (actual.datos.isEmpty()) {
                        Text("Sin publicaciones")
                    } else {
                        Column {
                            Text(
                                "${actual.datos.size} publicaciones",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium
                            )
                            LazyColumn(
                                state = listaState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(actual.datos, key = { it.id }) { post ->
                                    TarjetaPost(post.userId, post.title, post.body, { onAbrir(post.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
