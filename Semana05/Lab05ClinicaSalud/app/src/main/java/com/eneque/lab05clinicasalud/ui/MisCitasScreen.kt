package com.eneque.lab05clinicasalud.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.eneque.lab05clinicasalud.model.Cita
import com.eneque.lab05clinicasalud.model.listaMedicos
import com.eneque.lab05clinicasalud.ui.components.AppDrawerContent
import com.eneque.lab05clinicasalud.ui.theme.GraySubtitle
import com.eneque.lab05clinicasalud.ui.theme.Lab05ClinicaSaludTheme
import com.eneque.lab05clinicasalud.ui.theme.PurpleCardBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import kotlinx.coroutines.launch

@Composable
fun MisCitasScreen(
    citas: List<Cita>,
    onInicioClick: () -> Unit = {},
    onMisCitasClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                opcionSeleccionada = "Mis citas",
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
                // Encabezado con título "Mis citas" y botón hamburguesa ☰
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis citas",
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

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de citas utilizando LazyColumn
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = citas,
                        key = { it.id }
                    ) { cita ->
                        CitaCardItem(cita = cita)
                    }
                }
            }
        }
    }
}

@Composable
fun CitaCardItem(cita: Cita) {
    val medico = listaMedicos.find { it.id == cita.medicoId }
    val esConfirmada = cita.estado == "Confirmada"

    val lineaColor = if (esConfirmada) PurpleDarkHeader else Color(0xFF888888)
    val chipBgColor = if (esConfirmada) Color(0xFFE8F5E9) else Color(0xFFEEEEEE)
    val chipTextColor = if (esConfirmada) Color(0xFF2E7D32) else Color(0xFF666666)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleCardBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Línea vertical lateral izquierda de color
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(lineaColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(
                    text = medico?.nombre ?: "Médico",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                val textoHora = if (cita.hora.lowercase().contains("am") || cita.hora.lowercase().contains("pm")) {
                    cita.hora
                } else {
                    "${cita.hora} am"
                }

                Text(
                    text = "${cita.fecha}, $textoHora",
                    fontSize = 14.sp,
                    color = GraySubtitle
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Etiqueta del estado
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = chipBgColor,
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = cita.estado,
                            color = chipTextColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MisCitasScreenPreview() {
    Lab05ClinicaSaludTheme {
        MisCitasScreen(
            citas = listOf(
                Cita(
                    id = 1,
                    medicoId = 2,
                    fecha = "Miércoles 15",
                    hora = "3:00 pm",
                    estado = "Completada"
                ),
                Cita(
                    id = 2,
                    medicoId = 1,
                    fecha = "Vie 27",
                    hora = "10:30",
                    estado = "Confirmada"
                )
            )
        )
    }
}
