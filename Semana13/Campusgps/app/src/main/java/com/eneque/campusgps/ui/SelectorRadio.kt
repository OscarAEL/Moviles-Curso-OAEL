package com.eneque.campusgps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics

@Composable
fun SelectorRadio(
    radios: List<Int>,
    seleccionado: Int,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var abierto by remember { mutableStateOf(false) }
    Box(modifier) {
        TextButton(
            onClick = { abierto = true },
            modifier = Modifier.semantics {
                contentDescription = "Radio de cercanía: $seleccionado metros"
            }
        ) {
            Text("Radio: $seleccionado m", color = MaterialTheme.colorScheme.onPrimary)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            radios.forEach { radio ->
                DropdownMenuItem(
                    text = { Text("$radio m") },
                    trailingIcon = { if (radio == seleccionado) Text("✓") },
                    modifier = Modifier.semantics { selected = radio == seleccionado },
                    onClick = {
                        onSeleccionar(radio)
                        abierto = false
                    }
                )
            }
        }
    }
}
