package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectorCantidad(
    cantidadTexto: String,
    onDecrementar: () -> Unit,
    onIncrementar: () -> Unit,
    puedeDecrementar: Boolean,
    puedeIncrementar: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus Button with 48.dp touch target and 32.dp visual circle
        IconButton(
            onClick = onDecrementar,
            enabled = enabled && puedeDecrementar,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled && puedeDecrementar) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Restar",
                    tint = if (enabled && puedeDecrementar) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Quantity Label
        Text(
            text = cantidadTexto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E1B4B)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Plus Button with 48.dp touch target and 32.dp visual circle
        IconButton(
            onClick = onIncrementar,
            enabled = enabled && puedeIncrementar,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled && puedeIncrementar) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Sumar",
                    tint = if (enabled && puedeIncrementar) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
