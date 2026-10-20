package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    modifier: Modifier = Modifier
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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
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
            // Service Icon Badge - Siempre lavadora como símbolo general del pedido
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                IconoServicio(
                    icono = "wash",
                    modifier = Modifier.size(28.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Main Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Pedido #$numeroPedido",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1B4B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$resumenServicios · $fechaFormateada",
                    fontSize = 12.5.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EstadoPedidoChip(estadoStr = pedido.estado)

                    if (pedido.estado == EstadoPedido.LISTO.name && !pedido.casillero.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Casillero ${pedido.casillero}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Price Total
            Text(
                text = "S/ $precioFormateado",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
