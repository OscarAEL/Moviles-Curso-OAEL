package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraNavegacion

@Composable
fun HomeScreen(
    onAgendarCitaClick: () -> Unit,
    onMisCitasClick: () -> Unit,
    onEspecialidadClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {

    val usuario = Repositorio.usuarioActual
    val especialidadesDestacadas = Repositorio.especialidadesDestacadas()

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                seccionActual = "Inicio",
                onInicioClick = onInicioClick,
                onCitasClick = onCitasClick,
                onResultadosClick = onResultadosClick,
                onPerfilClick = onPerfilClick,
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {

            // 1. ENCABEZADO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¡Hola, ${usuario?.nombre ?: "Paciente"}!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "¿Qué deseas hacer hoy?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. ACCESOS PRINCIPALES (4 TARJETAS 2x2)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Tarjeta 1: Agendar Cita
                    TarjetaAcceso(
                        titulo = "Agendar cita",
                        icono = Icons.Default.DateRange,
                        colorFondo = Color(0xFFE3F2FD),
                        colorIcono = Color(0xFF1976D2),
                        onClick = onAgendarCitaClick,
                        modifier = Modifier.weight(1f),
                    )

                    // Tarjeta 2: Mis Citas
                    TarjetaAcceso(
                        titulo = "Mis citas",
                        icono = Icons.AutoMirrored.Filled.EventNote,
                        colorFondo = Color(0xFFE8F5E9),
                        colorIcono = Color(0xFF388E3C),
                        onClick = onMisCitasClick,
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Tarjeta 3: Mi Perfil
                    TarjetaAcceso(
                        titulo = "Mi perfil",
                        icono = Icons.Default.Person,
                        colorFondo = Color(0xFFF3E5F5),
                        colorIcono = Color(0xFF7B1FA2),
                        onClick = onPerfilClick,
                        modifier = Modifier.weight(1f),
                    )

                    // Tarjeta 4: Resultados
                    TarjetaAcceso(
                        titulo = "Resultados",
                        icono = Icons.AutoMirrored.Filled.Assignment,
                        colorFondo = Color(0xFFFFF3E0),
                        colorIcono = Color(0xFFE65100),
                        onClick = onResultadosClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. ESPECIALIDADES DESTACADAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Especialidades destacadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Ver todas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onAgendarCitaClick()
                    },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(especialidadesDestacadas) { especialidad ->

                    val (iconoEspecialidad, colorEspecialidad, fondoEspecialidad) =
                        obtenerEstiloEspecialidadHome(especialidad.nombre)

                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable {
                                onEspecialidadClick(especialidad.id)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        color = fondoEspecialidad,
                                        shape = CircleShape,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = iconoEspecialidad,
                                    contentDescription = especialidad.nombre,
                                    tint = colorEspecialidad,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = especialidad.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = especialidad.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaAcceso(
    titulo: String,
    icono: ImageVector,
    colorFondo: Color,
    colorIcono: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(108.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = colorIcono.copy(alpha = 0.15f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp),
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun obtenerEstiloEspecialidadHome(nombre: String): Triple<ImageVector, Color, Color> {
    return when {
        nombre.contains("General", ignoreCase = true) -> Triple(
            Icons.Default.LocalHospital,
            Color(0xFF1976D2),
            Color(0xFFE3F2FD),
        )
        nombre.contains("Pediatr", ignoreCase = true) -> Triple(
            Icons.Default.ChildCare,
            Color(0xFFF57C00),
            Color(0xFFFFF3E0),
        )
        nombre.contains("Ginecolog", ignoreCase = true) -> Triple(
            Icons.Default.Female,
            Color(0xFFC2185B),
            Color(0xFFFCE4EC),
        )
        else -> Triple(
            Icons.Default.LocalHospital,
            Color(0xFF0288D1),
            Color(0xFFE1F5FE),
        )
    }
}
