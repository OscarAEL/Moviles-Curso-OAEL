package com.eneque.registronotas_eneque

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eneque.registronotas_eneque.ui.theme.RegistroNotasEnequeTheme
import kotlin.math.roundToInt
import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroNotasEnequeTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Registro de Notas", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { innerPadding ->
                    PantallaNotas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Datos fijos de los cursos (peso en decimal, ej. 0.20f = 20%)
data class Curso(val nombre: String, val peso: Float)

val listaCursos = listOf(
    Curso("Fundamentos de Programación", 0.20f),
    Curso("Programación Orientada a Objetos", 0.25f),
    Curso("Programación en Móviles", 0.30f),
    Curso("Base de Datos", 0.25f)
)

@Composable
fun PantallaNotas(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEDE7F6), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Notas del ciclo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Desliza para asignar cada nota (0 a 20)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))

            var nota1 by remember { mutableFloatStateOf(0f) }
            var nota2 by remember { mutableFloatStateOf(0f) }
            var nota3 by remember { mutableFloatStateOf(0f) }
            var nota4 by remember { mutableFloatStateOf(0f) }
            var redondear by remember { mutableStateOf(false) }
            var confirmado by remember { mutableStateOf(false) }
            var calculado by remember { mutableStateOf(false) }

            FilaCurso(curso = listaCursos[0], nota = nota1, onNotaChange = { nota1 = it })
            Spacer(modifier = Modifier.height(20.dp))
            FilaCurso(curso = listaCursos[1], nota = nota2, onNotaChange = { nota2 = it })
            Spacer(modifier = Modifier.height(20.dp))
            FilaCurso(curso = listaCursos[2], nota = nota3, onNotaChange = { nota3 = it })
            Spacer(modifier = Modifier.height(20.dp))
            FilaCurso(curso = listaCursos[3], nota = nota4, onNotaChange = { nota4 = it })
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Redondear promedio final",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = redondear,
                    onCheckedChange = { redondear = it }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = confirmado,
                    onCheckedChange = { confirmado = it }
                )
                Text(
                    text = "Confirmo que las notas son correctas",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { calculado = true },
                enabled = confirmado,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("CALCULAR PROMEDIO", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (!calculado) {
                Text(
                    text = "Asigna las notas y confirma para calcular",
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                val promedioPonderado = nota1 * listaCursos[0].peso +
                        nota2 * listaCursos[1].peso +
                        nota3 * listaCursos[2].peso +
                        nota4 * listaCursos[3].peso

                val promedioFinal = if (redondear) {
                    promedioPonderado.roundToInt().toFloat()
                } else {
                    promedioPonderado
                }

                val observacion = when {
                    promedioFinal >= 17f -> "EXCELENTE"
                    promedioFinal >= 13f -> "APROBADO"
                    promedioFinal >= 10f -> "EN RECUPERACIÓN"
                    else -> "DESAPROBADO"
                }

                val colorChip = when (observacion) {
                    "EXCELENTE" -> Color(0xFF1B5E20)      // verde oscuro
                    "APROBADO" -> Color(0xFF4CAF50)       // verde
                    "EN RECUPERACIÓN" -> Color(0xFFFFA000) // ámbar
                    else -> Color(0xFFD32F2F)             // rojo
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Promedio ponderado: ${String.format(Locale.US, "%.2f", promedioPonderado)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Promedio final: ${
                                if (redondear) promedioFinal.toInt().toString()
                                else String.format(Locale.US, "%.2f", promedioFinal)
                            }",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (redondear) {
                            Text(
                                text = "(redondeado)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colorChip.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = observacion,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold,
                                color = colorChip
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "✓ Promedio calculado correctamente",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            text = "Desarrollado por: Oscar Eneque",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilaCurso(curso: Curso, nota: Float, onNotaChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(
                    text = curso.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(${(curso.peso * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${nota.toInt()}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Slider(
            value = nota,
            onValueChange = onNotaChange,
            valueRange = 0f..20f,
            steps = 19,
            thumb = {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                )
            }
        )
    }
}