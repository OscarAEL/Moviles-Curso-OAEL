package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.theme.EstadoCancelado
import com.tecsup.lavacampus.ui.theme.EstadoEnProceso
import com.tecsup.lavacampus.ui.theme.EstadoEntregado
import com.tecsup.lavacampus.ui.theme.EstadoListo
import com.tecsup.lavacampus.ui.theme.EstadoRecibido
import com.tecsup.lavacampus.ui.theme.LocalModoOscuro

@Composable
fun EstadoPedidoChip(
    estadoStr: String,
    modifier: Modifier = Modifier
) {
    val (texto, claro, oscuro) = when (estadoStr) {
        EstadoPedido.RECIBIDO.name -> Triple("Recibido", Color(0xFF92400E), Color(0xFFFCD34D))
        EstadoPedido.EN_PROCESO.name -> Triple("En proceso", Color(0xFF1D4ED8), Color(0xFF93C5FD))
        EstadoPedido.LISTO.name -> Triple("Listo", Color(0xFF166534), Color(0xFF86EFAC))
        EstadoPedido.ENTREGADO.name -> Triple("Entregado", Color(0xFF334155), Color(0xFFCBD5E1))
        EstadoPedido.CANCELADO.name -> Triple("Cancelado", Color(0xFFB91C1C), Color(0xFFFCA5A5))
        else -> Triple(estadoStr, Color(0xFF374151), Color(0xFFE5E7EB))
    }
    val color = if (LocalModoOscuro.current) oscuro else claro

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
