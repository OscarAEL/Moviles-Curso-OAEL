package com.eneque.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltrosCategorias(
    categorias: List<String>,
    seleccionada: String?,
    onSeleccionar: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "todos") {
            FilterChip(
                selected = seleccionada == null,
                onClick = { onSeleccionar(null) },
                label = { Text("Todos") }
            )
        }
        items(categorias, key = { it }) { categoria ->
            FilterChip(
                selected = seleccionada == categoria,
                onClick = { onSeleccionar(categoria) },
                label = { Text(categoria) }
            )
        }
    }
}
