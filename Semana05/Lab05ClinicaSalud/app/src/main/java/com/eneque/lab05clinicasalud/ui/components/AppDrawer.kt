package com.eneque.lab05clinicasalud.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.lab05clinicasalud.ui.theme.GraySubtitle
import com.eneque.lab05clinicasalud.ui.theme.PurpleCircleIconBg
import com.eneque.lab05clinicasalud.ui.theme.PurpleDarkHeader
import com.eneque.lab05clinicasalud.ui.theme.PurpleLightChipBg

@Composable
fun AppDrawerContent(
    opcionSeleccionada: String = "Inicio",
    onInicioClick: () -> Unit = {},
    onMisCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onPerfilClick: () -> Unit = {}
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            // Encabezado del Drawer con información estática del usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = PurpleCircleIconBg,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OE",
                            color = PurpleDarkHeader,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Oscar Eneque",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Paciente",
                        fontSize = 13.sp,
                        color = GraySubtitle
                    )
                }
            }

            // Línea divisoria fina
            HorizontalDivider(
                color = Color(0xFFEEEEEE),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Opciones del Menú
            val opciones = listOf(
                "Inicio" to onInicioClick,
                "Mis citas" to onMisCitasClick,
                "Historial médico" to onHistorialClick,
                "Perfil" to onPerfilClick
            )

            opciones.forEach { (titulo, onClick) ->
                val esSeleccionado = titulo == opcionSeleccionada

                Surface(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    color = if (esSeleccionado) PurpleLightChipBg else Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        // Pequeño círculo a la izquierda de cada opción
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(
                                    border = BorderStroke(
                                        width = 1.5.dp,
                                        color = if (esSeleccionado) PurpleDarkHeader else Color(0xFF666666)
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = titulo,
                            fontSize = 15.sp,
                            fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Normal,
                            color = if (esSeleccionado) PurpleDarkHeader else Color(0xFF333333)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
