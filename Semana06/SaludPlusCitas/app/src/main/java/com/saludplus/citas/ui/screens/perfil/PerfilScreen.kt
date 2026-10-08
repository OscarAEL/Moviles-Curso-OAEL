package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraNavegacion

// Pantalla de perfil de usuario para consultar datos personales y cerrar sesión.
@Composable
fun PerfilScreen(
    onCerrarSesionClick: () -> Unit, // Callback para llevar fuera de la sesión tras cerrar el acceso.
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit,
) {

    // Obtiene los datos del usuario logueado en la sesión activa
    val usuario = Repositorio.usuarioActual
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            BarraNavegacion(
                seccionActual = "Perfil",
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {

            Text(
                text = "Mi perfil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(
                modifier = Modifier.height(20.dp),
            )

            if (usuario != null) {

                // CABECERA DEL PERFIL: Muestra avatar e información principal del paciente
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color(0xFFE3F2FD),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar Usuario",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = usuario.nombre,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Paciente SaludPlus",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp),
                )

                // INFORMACIÓN PERSONAL: Muestra nombre, correo y teléfono registrados
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {

                        // Fila 1: Nombre
                        FilaInfoPerfil(
                            icono = Icons.Default.Person,
                            titulo = "Nombre",
                            valor = usuario.nombre,
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Fila 2: Correo
                        FilaInfoPerfil(
                            icono = Icons.Default.Email,
                            titulo = "Correo",
                            valor = usuario.correo,
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Fila 3: Teléfono
                        FilaInfoPerfil(
                            icono = Icons.Default.Phone,
                            titulo = "Teléfono",
                            valor = usuario.telefono,
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(32.dp),
                )

                // BOTÓN CERRAR SESIÓN: Limpia el estado del usuario en el Repositorio y ejecuta la navegación
                OutlinedButton(
                    onClick = {
                        Repositorio.cerrarSesion() // Borra el usuarioActual
                        onCerrarSesionClick()      // Navega fuera limpiando el historial
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                ) {
                    Text(
                        text = "Cerrar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

            } else {

                // SIN USUARIO: Estado si no se encuentra sesión activa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No hay usuario con sesión iniciada.",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaInfoPerfil(
    icono: ImageVector,
    titulo: String,
    valor: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = titulo,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
