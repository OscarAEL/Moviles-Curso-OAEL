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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.util.FechaUtils
import java.util.Locale

@Composable
fun PedidoCard(
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarAccionCancelar: Boolean = false,
    onCancelarClick: (() -> Unit)? = null
) {
    val pedido = pedidoConDetalle.pedido
    val detalles = pedidoConDetalle.detalles

    val resumenServicios = when {
        detalles.isEmpty() -> "Sin detalles"
        detalles.size == 1 -> {
            val d = detalles.first()
            val s = d.servicio
            val c = d.detalle.cantidad
            val u = s.unidad
            if (u == "KG") {
                "$c kg"
            } else {
                "${c.toInt()} ${if (c == 1.0) u.lowercase() else if (u == "UNIDAD") "unidades" else "prendas"}"
            }
        }
        detalles.size == 2 -> {
            val d1 = detalles[0]
            val s1 = d1.servicio
            val c1 = if (s1.unidad == "KG") "${d1.detalle.cantidad} kg" else "${d1.detalle.cantidad.toInt()} ${if (d1.detalle.cantidad == 1.0) s1.unidad.lowercase() else if (s1.unidad == "UNIDAD") "unidades" else "prendas"}"

            val d2 = detalles[1]
            val s2 = d2.servicio
            val c2 = if (s2.unidad == "KG") "${d2.detalle.cantidad} kg" else "${d2.detalle.cantidad.toInt()} ${if (d2.detalle.cantidad == 1.0) s2.unidad.lowercase() else if (s2.unidad == "UNIDAD") "unidades" else "prendas"}"

            "$c1 + $c2"
        }
        else -> "${detalles.size} servicios"
    }

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
        color = Color.White,
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
                        color = Color(0xFF1E1B4B)
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
                    color = Color(0xFF6B7280)
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
