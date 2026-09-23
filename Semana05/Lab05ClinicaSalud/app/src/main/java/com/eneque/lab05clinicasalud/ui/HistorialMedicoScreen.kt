package com.eneque.lab05clinicasalud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05clinicasalud.model.listaMedicos
import com.eneque.lab05clinicasalud.ui.components.AppDrawerContent
import com.eneque.lab05clinicasalud.ui.theme.GraySubtitle
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleCardBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import kotlinx.coroutines.launch

data class RegistroHistorial(
    val id: Int,
    val medicoId: Int,
    val especialidad: String,
    val fecha: String,
    val estado: String = "Atendido"
)

val listaHistorial = listOf(
    RegistroHistorial(
        id = 1,
        medicoId = 1,
        especialidad = "Cardiología",
        fecha = "Lun 12"
    ),
    RegistroHistorial(
        id = 2,
        medicoId = 2,
        especialidad = "Pediatría",
        fecha = "Mar 06"
    )
)

@Composable
fun HistorialMedicoScreen(
    onInicioClick: () -> Unit = {},
    onMisCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                opcionSeleccionada = "Historial médico",
                onInicioClick = {
                    scope.launch { drawerState.close() }
                    onInicioClick()
                },
                onMisCitasClick = {
                    scope.launch { drawerState.close() }
                    onMisCitasClick()
                },
                onHistorialClick = {
                    scope.launch { drawerState.close() }
                    onHistorialClick()
                },
                onPerfilClick = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
            ) {
                // Encabezado con título "Historial médico" e icono ☰
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historial médico",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "☰",
                        color = Color.Black,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                scope.launch { drawerState.open() }
                            }
                            .padding(8.dp)
                    )
                }

                // Subtítulo "Atenciones anteriores"
                Text(
                    text = "Atenciones anteriores",
                    fontSize = 15.sp,
                    color = GraySubtitle,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // LazyColumn con el historial
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = listaHistorial,
                        key = { it.id }
                    ) { registro ->
                        HistorialCardItem(registro = registro)
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialCardItem(registro: RegistroHistorial) {
    val medico = listaMedicos.find { it.id == registro.medicoId }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleCardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = medico?.nombre ?: "Médico",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                // Etiqueta "Atendido"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8DEF8),
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = registro.estado,
                            color = PurpleDarkHeader,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = registro.especialidad,
                fontSize = 14.sp,
                color = GraySubtitle
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fecha: ${registro.fecha}",
                fontSize = 13.sp,
                color = Color(0xFF555555)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistorialMedicoScreenPreview() {
    Lab05ClinicaSaludTheme {
        HistorialMedicoScreen()
    }
}
