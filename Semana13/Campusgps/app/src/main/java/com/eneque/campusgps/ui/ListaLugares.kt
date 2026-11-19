package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.ui.theme.CampusMono
import com.eneque.campusgps.ui.theme.CampusNearest
import com.eneque.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dentroDelRadio: Set<Int> = emptySet(),
    masCercano: Int? = null
) {
    LazyColumn(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(lugares, key = { it.lugar.id }) { item ->
            TarjetaLugar(item, item.lugar.id == seleccionado, item.lugar.id in dentroDelRadio,
                item.lugar.id == masCercano, { onClick(item.lugar.id) })
        }
    }
}

@Composable
fun TarjetaLugar(
    item: LugarConDistancia,
    seleccionado: Boolean,
    dentroDelRadio: Boolean,
    masCercano: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().semantics { selected = seleccionado },
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (seleccionado) 2.dp else 1.dp,
            if (seleccionado) colores.primary else colores.outlineVariant),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) colores.primaryContainer else colores.surface
        )
    ) {
        Row(
            modifier = Modifier
                .drawBehind {
                    if (seleccionado) drawRect(colores.primary, size = Size(3.dp.toPx(), size.height))
                }
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box {
                Box(
                    Modifier.size(40.dp).background(
                        if (seleccionado) colores.primary else colores.primaryContainer,
                        RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    IconoCampus(iconoCategoria(item.lugar.categoria),
                        color = if (seleccionado) colores.onPrimary else colores.primary)
                }
                if (masCercano) Box(
                    Modifier.align(Alignment.TopEnd).size(10.dp)
                        .background(CampusNearest, CircleShape)
                        .border(2.dp, colores.surface, CircleShape)
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(item.lugar.nombre, style = MaterialTheme.typography.titleMedium)
                Text(item.lugar.categoria, style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant)
            }
            Column(Modifier.widthIn(min = 58.dp, max = 112.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    item.metros?.let { formatearDistancia(it) } ?: "—",
                    fontFamily = CampusMono, fontWeight = FontWeight.Medium,
                    fontSize = 15.sp, color = colores.onSurface
                )
                if (item.metros == null) {
                    Text("Sin calcular", style = MaterialTheme.typography.labelSmall,
                        color = colores.onSurfaceVariant)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        if (dentroDelRadio) IconoCampus(IconoCampus.Check,
                            Modifier.size(12.dp), colores.primary)
                        Text(if (dentroDelRadio) "Dentro del radio" else "Fuera del radio",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (dentroDelRadio) colores.primary else colores.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
