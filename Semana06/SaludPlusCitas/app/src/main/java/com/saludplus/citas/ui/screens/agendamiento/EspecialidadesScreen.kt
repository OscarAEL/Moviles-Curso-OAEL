package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras

@Composable
fun EspecialidadesScreen(
    onEspecialidadClick: (Int) -> Unit,
    onAtrasClick: () -> Unit,
) {

    var busqueda by remember {
        mutableStateOf("")
    }

    val especialidadesFiltradas =
        Repositorio.buscarEspecialidades(busqueda)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {

        EncabezadoConAtras(
            titulo = "Especialidades",
            onAtrasClick = onAtrasClick,
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            placeholder = {
                Text("Buscar especialidad...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(
            modifier = Modifier.height(16.dp),
        )

        LazyColumn {

            items(especialidadesFiltradas) { especialidad ->

                val (icono, colorIcono, colorFondo) = obtenerEstiloEspecialidad(especialidad.nombre)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            onEspecialidadClick(especialidad.id)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    ),
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = colorFondo,
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = icono,
                                contentDescription = especialidad.nombre,
                                tint = colorIcono,
                                modifier = Modifier.size(22.dp),
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                        ) {

                            Text(
                                text = especialidad.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp),
                            )

                            Text(
                                text = especialidad.descripcion,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Seleccionar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun obtenerEstiloEspecialidad(nombre: String): Triple<ImageVector, Color, Color> {
    return when {
        nombre.contains("General", ignoreCase = true) -> Triple(
            Icons.Default.LocalHospital,
            Color(0xFF1976D2), // Azul
            Color(0xFFE3F2FD),
        )
        nombre.contains("Pediatr", ignoreCase = true) -> Triple(
            Icons.Default.ChildCare,
            Color(0xFFF57C00), // Naranja
            Color(0xFFFFF3E0),
        )
        nombre.contains("Ginecolog", ignoreCase = true) -> Triple(
            Icons.Default.Female,
            Color(0xFFC2185B), // Rosa / Morado
            Color(0xFFFCE4EC),
        )
        nombre.contains("Cardiolog", ignoreCase = true) -> Triple(
            Icons.Default.Favorite,
            Color(0xFFD32F2F), // Rojo
            Color(0xFFFFEBEE),
        )
        nombre.contains("Dermatolog", ignoreCase = true) -> Triple(
            Icons.Default.Spa,
            Color(0xFFE65100), // Naranja claro
            Color(0xFFFFF8E1),
        )
        nombre.contains("Traumatolog", ignoreCase = true) -> Triple(
            Icons.Default.AccessibilityNew,
            Color(0xFF0097A7), // Turquesa
            Color(0xFFE0F7FA),
        )
        nombre.contains("Oftalmolog", ignoreCase = true) -> Triple(
            Icons.Default.Visibility,
            Color(0xFF0288D1), // Azul
            Color(0xFFE1F5FE),
        )
        else -> Triple(
            Icons.Default.HealthAndSafety,
            Color(0xFF1976D2),
            Color(0xFFE3F2FD),
        )
    }
}
