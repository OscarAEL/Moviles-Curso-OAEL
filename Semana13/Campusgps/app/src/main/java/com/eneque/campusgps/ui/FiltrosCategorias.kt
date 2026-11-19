package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
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
    LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item(key = "todos") {
            ChipCategoria("Todos", seleccionada == null, null) { onSeleccionar(null) }
        }
        items(categorias, key = { it }) { categoria ->
            ChipCategoria(categoria, seleccionada == categoria, iconoCategoria(categoria)) {
                onSeleccionar(categoria)
            }
        }
    }
}

@Composable
private fun ChipCategoria(
    etiqueta: String,
    activo: Boolean,
    icono: IconoCampus?,
    onClick: () -> Unit
) {
    FilterChip(
        selected = activo,
        onClick = onClick,
        label = { Text(etiqueta, style = MaterialTheme.typography.labelMedium) },
        leadingIcon = if (activo || icono != null) ({
            IconoCampus(if (activo) IconoCampus.Check else requireNotNull(icono),
                Modifier.size(16.dp))
        }) else null,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.dp, MaterialTheme.colorScheme.secondaryContainer),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            iconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.heightIn(min = 48.dp)
    )
}
