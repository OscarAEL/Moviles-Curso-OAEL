package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale

@Composable
fun DesgloseDescuento(subtotal: Double, descuento: Double) {
    if (descuento <= 0.0) return
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal")
            Text(String.format(Locale.US, "S/ %.2f", subtotal))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Descuento por volumen\n10% en lavado", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            Text(String.format(Locale.US, "- S/ %.2f", descuento), fontWeight = FontWeight.SemiBold)
        }
    }
}
