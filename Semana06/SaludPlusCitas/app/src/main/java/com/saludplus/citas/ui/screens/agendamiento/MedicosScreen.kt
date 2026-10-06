package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.EncabezadoConAtras

@Composable
fun MedicosScreen(
    especialidadId: Int,
    onMedicoClick: (Int) -> Unit,
    onAtrasClick: () -> Unit
) {

    var busqueda by remember {
        mutableStateOf("")
    }

    val especialidad =
        Repositorio.obtenerEspecialidad(especialidadId)

    val medicosFiltrados =
        Repositorio.buscarMedicos(
            especialidadId = especialidadId,
            texto = busqueda
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        EncabezadoConAtras(
            titulo = especialidad?.nombre ?: "Médicos",
            onAtrasClick = onAtrasClick
        )

        Text(
            text = "Selecciona un médico"
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text("Buscar médico")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        if (medicosFiltrados.isEmpty()) {

            Text(
                text = "No se encontraron médicos"
            )

        } else {

            LazyColumn {

                items(medicosFiltrados) { medico ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                onMedicoClick(medico.id)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = medico.nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(text = medico.especialidad)
                            Text(text = medico.cmp)
                            Text(text = "Calificación: ${medico.calificacion}")
                        }
                    }
                }
            }
        }
    }
}