package com.eneque.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eneque.campusgps.ui.theme.CampusMono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRadio(
    radios: List<Int>,
    seleccionado: Int,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Radio", style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        SingleChoiceSegmentedButtonRow(Modifier.weight(1f).fillMaxWidth()) {
            radios.forEachIndexed { index, radio ->
                SegmentedButton(
                    selected = seleccionado == radio,
                    onClick = { onSeleccionar(radio) },
                    shape = SegmentedButtonDefaults.itemShape(index, radios.size,
                        baseShape = RoundedCornerShape(14.dp)),
                    border = BorderStroke(0.dp, Color.Transparent),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        inactiveContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("$radio m", fontFamily = CampusMono,
                        style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
