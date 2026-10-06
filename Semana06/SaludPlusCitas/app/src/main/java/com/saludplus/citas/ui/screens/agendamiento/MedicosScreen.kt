package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras

@Composable
fun MedicosScreen(
    especialidadId: Int,
    onMedicoClick: (Int) -> Unit,
    onAtrasClick: () -> Unit,
) {

    var busqueda by remember {
        mutableStateOf("")
    }

    val especialidad =
        Repositorio.obtenerEspecialidad(especialidadId)

    val medicosFiltrados =
        Repositorio.buscarMedicos(
            especialidadId = especialidadId,
            texto = busqueda,
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {

        EncabezadoConAtras(
            titulo = "Médicos de ${especialidad?.nombre ?: "Especialidad"}",
            onAtrasClick = onAtrasClick,
        )

        Text(
            text = "Selecciona un médico",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(
            modifier = Modifier.height(12.dp),
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            placeholder = {
                Text("Buscar médico...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar médico",
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

        if (medicosFiltrados.isEmpty()) {

            Text(
                text = "No se encontraron médicos",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                items(medicosFiltrados) { medico ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onMedicoClick(medico.id)
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

                            // IZQUIERDA: Imagen real del médico
                            Image(
                                painter = painterResource(id = obtenerImagenMedico(medico.nombre)),
                                contentDescription = medico.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape),
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // CENTRO: Información del médico
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {

                                Text(
                                    text = medico.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = medico.especialidad,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Calificación",
                                        tint = Color(0xFFFFA000),
                                        modifier = Modifier.size(16.dp),
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Text(
                                        text = medico.calificacion.toString(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = medico.cmp,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // DERECHA: Disponibilidad y Flecha
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Text(
                                    text = "Disponible",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32),
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Seleccionar médico",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun obtenerImagenMedico(nombre: String?): Int {
    return when (nombre) {
        "Dra. Ana Torres" -> R.drawable.doctora_1
        "Dra. Mariana Soto" -> R.drawable.doctora_2
        "Dr. Carlos Rojas" -> R.drawable.doctor_1
        "Dr. Luis Ramírez" -> R.drawable.doctor_2
        else -> R.drawable.doctor_1
    }
}
