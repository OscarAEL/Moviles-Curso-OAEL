package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.DetalleConServicio
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.util.FechaUtils

fun formatearResumenDetalles(detalles: List<DetalleConServicio>): String {
    if (detalles.isEmpty()) return "Sin detalles"
    if (detalles.size >= 3) return "${detalles.size} servicios"

    fun formatearUnidad(d: DetalleConServicio): String {
        val cant = d.detalle.cantidad
        val unidad = d.servicio.unidad.uppercase()
        return when (unidad) {
            "KG" -> "$cant kg"
            "PRENDA" -> "${cant.toInt()} ${if (cant == 1.0) "prenda" else "prendas"}"
            "UNIDAD" -> "${cant.toInt()} ${if (cant == 1.0) "unidad" else "unidades"}"
            else -> "${cant.toInt()} ${unidad.lowercase()}"
        }
    }

    return if (detalles.size == 1) {
        formatearUnidad(detalles.first())
    } else {
        "${formatearUnidad(detalles[0])} + ${formatearUnidad(detalles[1])}"
    }
}

@Composable
fun PedidoCard(
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarAccionCancelar: Boolean = false,
    onCancelarClick: (() -> Unit)? = null,
    onRepetirClick: (() -> Unit)? = null,
) {
    val p = pedidoConDetalle.pedido
    val listo = p.estado == "LISTO" && !p.casillero.isNullOrBlank()
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (listo) 24.dp else 0.dp))
            .background(
                if (listo) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = if (listo) 16.dp else 0.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Pedido ${numero(p.id)}",
                Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
            )
            EstadoPedidoChip(p.estado)
        }
        if (listo) {
            Text(
                "Recógelo en el casillero",
                modifier = Modifier.padding(top = 14.dp),
                fontSize = 12.sp,
            )
            Text(p.casillero.orEmpty(), fontWeight = FontWeight.ExtraBold, fontSize = 34.sp)
        }
        Text(
            formatearResumenDetalles(pedidoConDetalle.detalles),
            modifier = Modifier.padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
        )
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(dinero(p.total), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    if (listo) {
                        if (p.pagado) "Pago registrado" else "Pago pendiente"
                    } else FechaUtils.formatearFechaCorta(p.fechaRegistro),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (mostrarAccionCancelar && onCancelarClick != null)
                TextButton(onClick = onCancelarClick) {
                    Text(
                        "Cancelar pedido",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                    )
                }
            if (onRepetirClick != null && p.estado in setOf("ENTREGADO", "CANCELADO"))
                TextButton(onClick = onRepetirClick) { Text("Pedir de nuevo", fontSize = 12.sp) }
        }
    }
    if (!listo) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
