package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
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
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.util.FechaUtils
import java.util.Locale

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
    onRepetirClick: (() -> Unit)? = null
) {
    val pedido = pedidoConDetalle.pedido
    val detalles = pedidoConDetalle.detalles
    val resumenServicios = formatearResumenDetalles(detalles)

    val fechaFormateada = FechaUtils.formatearFechaCorta(pedido.fechaRegistro)
    val precioFormateado = String.format(Locale.US, "%.2f", pedido.total)
    val numeroPedido = String.format(Locale.US, "%03d", pedido.id)

    val esListoConCasillero = pedido.estado == EstadoPedido.LISTO.name && !pedido.casillero.isNullOrEmpty()
    val colorBorde = if (esListoConCasillero) Color(0xFF10B981) else Color(0xFFF1F0F5)
    val anchoBorde = if (esListoConCasillero) 1.5.dp else 1.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = anchoBorde,
                color = colorBorde,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (esListoConCasillero) Color(0xFFD1FAE5)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconoServicio(
                    icono = "wash",
                    modifier = Modifier.size(28.dp),
                    color = if (esListoConCasillero) Color(0xFF059669) else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pedido #$numeroPedido",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "S/ $precioFormateado",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$resumenServicios · $fechaFormateada",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EstadoPedidoChip(estadoStr = pedido.estado)

                    if (esListoConCasillero) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981)
                        ) {
                            Text(
                                text = "Casillero ${pedido.casillero}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    } else if (mostrarAccionCancelar && onCancelarClick != null) {
                        Text(
                            text = "Cancelar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onCancelarClick() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    } else if (onRepetirClick != null && pedido.estado in setOf("ENTREGADO", "CANCELADO")) {
                        TextButton(onClick = onRepetirClick) { Text("Pedir de nuevo", fontSize = 12.sp) }
                    } else if (pedido.estado == EstadoPedido.EN_PROCESO.name && pedido.fechaEntrega.isNotEmpty()) {
                        Text(
                            text = "Entrega ${FechaUtils.formatearFechaCorta(pedido.fechaEntrega)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
