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

@Composable
fun EstadoPedidoChip(
    estadoStr: String,
    modifier: Modifier = Modifier
) {
    val (texto, color) = when (estadoStr) {
        EstadoPedido.RECIBIDO.name -> "Recibido" to EstadoRecibido
        EstadoPedido.EN_PROCESO.name -> "En proceso" to EstadoEnProceso
        EstadoPedido.LISTO.name -> "Listo" to EstadoListo
        EstadoPedido.ENTREGADO.name -> "Entregado" to EstadoEntregado
        EstadoPedido.CANCELADO.name -> "Cancelado" to EstadoCancelado
        else -> estadoStr to Color.Gray
    }

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
