package com.eneque.centrotareas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.centrotareas.model.CargaUi
import com.eneque.centrotareas.model.PrimosUi
import com.eneque.centrotareas.model.TiempoCarga
import com.eneque.centrotareas.ui.theme.AccentViolet
import com.eneque.centrotareas.ui.theme.AccentVioletContainer
import com.eneque.centrotareas.ui.theme.BarSequential
import com.eneque.centrotareas.ui.theme.BgColor
import com.eneque.centrotareas.ui.theme.DisabledText
import com.eneque.centrotareas.ui.theme.OnSurfaceColor
import com.eneque.centrotareas.ui.theme.OnSurfaceVariantColor
import com.eneque.centrotareas.ui.theme.OutlineVariantColor
import com.eneque.centrotareas.ui.theme.SurfaceColor
import com.eneque.centrotareas.ui.theme.SurfaceContainerColor
import com.eneque.centrotareas.ui.theme.SurfaceContainerHighColor
import com.eneque.centrotareas.ui.theme.TechError
import com.eneque.centrotareas.ui.theme.TechMonoFont
import com.eneque.centrotareas.ui.theme.TechOnPrimary
import com.eneque.centrotareas.ui.theme.TechPrimary
import com.eneque.centrotareas.ui.theme.TechPrimaryBorder
import com.eneque.centrotareas.ui.theme.TechPrimaryContainer
import com.eneque.centrotareas.ui.theme.TechSecondary
import com.eneque.centrotareas.ui.theme.TechSecondaryContainer
import com.eneque.centrotareas.ui.theme.TechWarning
import com.eneque.centrotareas.viewmodel.TareasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    val mensaje by viewModel.mensaje.collectAsState()
    val progreso by viewModel.progreso.collectAsState()
    val corriendo by viewModel.corriendo.collectAsState()
    val primos by viewModel.primos.collectAsState()
    val carga by viewModel.carga.collectAsState()
    val simularError by viewModel.simularError.collectAsState()
    val tiempos by viewModel.tiempos.collectAsState()

    var enSplash by rememberSaveable { mutableStateOf(true) }

    AnimatedContent(
        targetState = enSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(350, easing = FastOutSlowInEasing)) togetherWith
                fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing))
        },
        label = "NavegacionSplashDashboard"
    ) { splashActivo ->
        if (splashActivo) {
            PantallaInicio(onIngresar = { enSplash = false })
        } else {
            ContenidoTareas(
                mensaje = mensaje,
                progreso = progreso,
                corriendo = corriendo,
                onTareaLarga = viewModel::tareaLarga,
                onCancelar = viewModel::cancelar,
                primos = primos,
                onContar = viewModel::contar,
                onCancelarPrimos = viewModel::cancelarPrimos,
                carga = carga,
                onCargarUnaPorUna = viewModel::cargarUnaPorUna,
                onCargarALaVez = viewModel::cargarALaVez,
                simularError = simularError,
                onCambiarSimularError = viewModel::cambiarSimularError,
                tiempos = tiempos
            )
        }
    }
}

// -------------------------------------------------------------
// PANTALLA 1: SPLASH SCREEN / INICIO TÉCNICO
// -------------------------------------------------------------
@Composable
private fun PantallaInicio(onIngresar: () -> Unit) {
    var animacionIniciada by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animacionIniciada = true
    }

    val escalaNodo by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 200f),
        label = "EscalaNodo"
    )

    val progresoTrazos by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "ProgresoTrazos"
    )

    val alphaContenido by animateFloatAsState(
        targetValue = if (animacionIniciada) 1f else 0f,
        animationSpec = tween(500, delayMillis = 700),
        label = "AlphaContenido"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Chip superior
            StatusChip(
                texto = "MAIN ● OK",
                activo = true,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Bloque central (Isotipo + Título + Badge)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Isotipo animado: tres hilos convergiendo en un nodo central
                ThreadNodeLogo(
                    progresoTrazos = progresoTrazos,
                    escalaNodo = escalaNodo,
                    modifier = Modifier.size(width = 260.dp, height = 150.dp)
                )

                Text(
                    text = "CentroTareas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurfaceColor,
                    modifier = Modifier.scale(if (animacionIniciada) 1f else 0.95f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceContainerColor,
                    border = BorderStroke(1.dp, OutlineVariantColor)
                ) {
                    Text(
                        text = "Android Concurrency & Thread Monitor · Lab 14",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariantColor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bloque inferior (Botón + Sello TECSUP)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                var escalaBoton by remember { mutableFloatStateOf(1f) }
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        scope.launch {
                            escalaBoton = 0.95f
                            delay(90)
                            escalaBoton = 1f
                            onIngresar()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(escalaBoton),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechPrimary,
                        contentColor = TechOnPrimary
                    )
                ) {
                    Text(
                        text = "INGRESAR AL MONITOR ▸",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = TechMonoFont,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "TECSUP · PROGRAMACIÓN EN MÓVILES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = TechMonoFont,
                        letterSpacing = 1.2.sp
                    ),
                    color = OnSurfaceVariantColor.copy(alpha = alphaContenido * 0.7f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// ISOTIPO ANIMADO CON CANVAS (Tres hilos -> Nodo -> Salida)
// -------------------------------------------------------------
@Composable
private fun ThreadNodeLogo(
    progresoTrazos: Float,
    escalaNodo: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AnillosNodo")
    val pulsoAnillo1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulsoAnillo1"
    )
    val pulsoAnillo2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulsoAnillo2"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centroX = w * 0.52f
        val centroY = h * 0.5f

        val strokeWidth = 2.5.dp.toPx()

        // Línea 1 (Superior esmeralda)
        val p1InicioX = w * 0.1f
        val p1InicioY = h * 0.22f
        val p1CodoX = w * 0.38f
        val p1CodoY = h * 0.22f

        val t1 = (progresoTrazos * 1.5f).coerceIn(0f, 1f)
        val path1 = Path().apply {
            moveTo(p1InicioX, p1InicioY)
            val dx1 = p1CodoX - p1InicioX
            val actualCodoX = p1InicioX + dx1 * t1
            lineTo(actualCodoX, p1InicioY)
            if (t1 > 0.6f) {
                val tBajo = (t1 - 0.6f) / 0.4f
                lineTo(p1CodoX + (centroX - p1CodoX) * tBajo, p1CodoY + (centroY - p1CodoY) * tBajo)
            }
        }
        drawPath(path1, TechPrimary, style = Stroke(strokeWidth, cap = StrokeCap.Round))

        // Línea 2 (Central cian)
        val p2InicioX = w * 0.1f
        val p2InicioY = centroY
        val t2 = progresoTrazos
        val path2 = Path().apply {
            moveTo(p2InicioX, p2InicioY)
            lineTo(p2InicioX + (centroX - p2InicioX) * t2, p2InicioY)
        }
        drawPath(path2, TechSecondary, style = Stroke(strokeWidth, cap = StrokeCap.Round))

        // Línea 3 (Inferior esmeralda)
        val p3InicioX = w * 0.1f
        val p3InicioY = h * 0.78f
        val p3CodoX = w * 0.38f
        val p3CodoY = h * 0.78f
        val path3 = Path().apply {
            moveTo(p3InicioX, p3InicioY)
            val dx3 = p3CodoX - p3InicioX
            val actualCodoX = p3InicioX + dx3 * t1
            lineTo(actualCodoX, p3InicioY)
            if (t1 > 0.6f) {
                val tSub = (t1 - 0.6f) / 0.4f
                lineTo(p3CodoX + (centroX - p3CodoX) * tSub, p3CodoY + (centroY - p3CodoY) * tSub)
            }
        }
        drawPath(path3, TechPrimary, style = Stroke(strokeWidth, cap = StrokeCap.Round))

        // Hilo de salida hacia la derecha
        val pSalidaFinX = w * 0.9f
        if (progresoTrazos > 0.7f) {
            val tOut = (progresoTrazos - 0.7f) / 0.3f
            val pathOut = Path().apply {
                moveTo(centroX, centroY)
                lineTo(centroX + (pSalidaFinX - centroX) * tOut, centroY)
            }
            drawPath(pathOut, TechPrimary, style = Stroke(strokeWidth, cap = StrokeCap.Round))
            // Punto satélite sobre hilo de salida
            if (tOut > 0.6f) {
                drawCircle(
                    color = TechSecondary,
                    radius = 3.5.dp.toPx(),
                    center = Offset(centroX + (pSalidaFinX - centroX) * 0.6f, centroY)
                )
            }
        }

        // Puntos codo
        if (progresoTrazos > 0.5f) {
            drawCircle(TechPrimary, 3.5.dp.toPx(), Offset(p1CodoX, p1CodoY))
            drawCircle(TechPrimary, 3.5.dp.toPx(), Offset(p3CodoX, p3CodoY))
        }

        // Nodo central con anillos pulsantes
        if (escalaNodo > 0.05f) {
            val radioBase = 12.dp.toPx() * escalaNodo

            // Anillo exterior 1
            val radioAnillo1 = radioBase + (28.dp.toPx() * pulsoAnillo1)
            val alphaAnillo1 = (1f - pulsoAnillo1).coerceIn(0f, 0.45f)
            drawCircle(
                color = TechPrimary.copy(alpha = alphaAnillo1),
                radius = radioAnillo1,
                center = Offset(centroX, centroY),
                style = Stroke(1.5.dp.toPx())
            )

            // Anillo exterior 2
            val radioAnillo2 = radioBase + (28.dp.toPx() * pulsoAnillo2)
            val alphaAnillo2 = (1f - pulsoAnillo2).coerceIn(0f, 0.45f)
            drawCircle(
                color = TechPrimary.copy(alpha = alphaAnillo2),
                radius = radioAnillo2,
                center = Offset(centroX, centroY),
                style = Stroke(1.5.dp.toPx())
            )

            // Círculo contenedor
            drawCircle(
                color = SurfaceContainerHighColor,
                radius = radioBase,
                center = Offset(centroX, centroY)
            )
            drawCircle(
                color = TechPrimary,
                radius = radioBase,
                center = Offset(centroX, centroY),
                style = Stroke(2.5.dp.toPx())
            )

            // Núcleo verde brillante
            drawCircle(
                color = TechPrimary,
                radius = 4.5.dp.toPx() * escalaNodo,
                center = Offset(centroX, centroY)
            )
        }
    }
}

// -------------------------------------------------------------
// PANTALLA 2: DASHBOARD / MONITOR SCREEN
// -------------------------------------------------------------
@Composable
private fun ContenidoTareas(
    mensaje: String,
    progreso: Float,
    corriendo: Boolean,
    onTareaLarga: () -> Unit,
    onCancelar: () -> Unit,
    primos: PrimosUi,
    onContar: (Int) -> Unit,
    onCancelarPrimos: () -> Unit,
    carga: CargaUi,
    onCargarUnaPorUna: () -> Unit,
    onCargarALaVez: () -> Unit,
    simularError: Boolean,
    onCambiarSimularError: (Boolean) -> Unit,
    tiempos: List<TiempoCarga>
) {
    var toques by rememberSaveable { mutableIntStateOf(0) }
    var hasta by rememberSaveable { mutableStateOf("1250000") }
    val limite = hasta.toIntOrNull()?.takeIf { it >= 2 }
    val calculandoPrimos = primos is PrimosUi.Calculando
    val cargando = carga is CargaUi.Cargando

    // Conteo de jobs activos
    var jobsActivos = 0
    if (corriendo) jobsActivos++
    if (calculandoPrimos) jobsActivos++
    if (cargando) jobsActivos++

    // Pulso dinámico para el Thread Monitor Canvas
    var triggerPico by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Cabecera superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CentroTareas",
                    style = MaterialTheme.typography.titleLarge,
                    color = OnSurfaceColor
                )
                StatusChip(texto = "MAIN ● OK", activo = true)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. THREAD MONITOR
                item {
                    ThreadMonitorCard(
                        toques = toques,
                        jobsActivos = jobsActivos,
                        triggerPico = triggerPico,
                        onTocar = {
                            toques++
                            triggerPico++
                        }
                    )
                }

                // 2. JOB 01 · PROGRESO
                item {
                    Job01ProgresoCard(
                        mensaje = mensaje,
                        progreso = progreso,
                        corriendo = corriendo,
                        onIniciar = onTareaLarga,
                        onCancelar = onCancelar
                    )
                }

                // 3. JOB 02 · PRIMOS
                item {
                    Job02PrimosCard(
                        hasta = hasta,
                        onHastaChange = { hasta = it },
                        limite = limite,
                        primos = primos,
                        calculandoPrimos = calculandoPrimos,
                        onContar = { limite?.let(onContar) },
                        onCancelar = onCancelarPrimos
                    )
                }

                // 4. JOB 03 · DOS CARGAS + BENCHMARK LOG
                item {
                    Job03CargasCard(
                        simularError = simularError,
                        onCambiarSimularError = onCambiarSimularError,
                        cargando = cargando,
                        carga = carga,
                        tiempos = tiempos,
                        onCargarUnaPorUna = onCargarUnaPorUna,
                        onCargarALaVez = onCargarALaVez
                    )
                }

                // Espacio extra para no tapar el footer
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Pie institucional anclado
            TechFooter()
        }
    }
}

// -------------------------------------------------------------
// COMPONENTE: THREAD MONITOR CARD
// -------------------------------------------------------------
@Composable
private fun ThreadMonitorCard(
    toques: Int,
    jobsActivos: Int,
    triggerPico: Int,
    onTocar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerColor),
        border = BorderStroke(1.dp, OutlineVariantColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "THREAD MONITOR",
                style = MaterialTheme.typography.titleSmall,
                color = OnSurfaceVariantColor
            )

            // Gráfico interactivo con Canvas
            PulseGraph(
                triggerPico = triggerPico,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    AnimatedContent(
                        targetState = toques,
                        transitionSpec = {
                            slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                        },
                        label = "AnimToques"
                    ) { count ->
                        Text(
                            text = "$count",
                            style = TextStyle(
                                fontFamily = TechMonoFont,
                                fontWeight = FontWeight.Medium,
                                fontSize = 28.sp
                            ),
                            color = OnSurfaceColor
                        )
                    }
                    Text(
                        text = if (jobsActivos == 0) {
                            "toques · UI responde (sin jobs activos)"
                        } else {
                            "toques · UI responde con $jobsActivos job${if (jobsActivos > 1) "s" else ""} activo${if (jobsActivos > 1) "s" else ""}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantColor
                    )
                }

                var escalaBoton by remember { mutableFloatStateOf(1f) }
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        scope.launch {
                            escalaBoton = 0.92f
                            delay(80)
                            escalaBoton = 1f
                            onTocar()
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .scale(escalaBoton),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechPrimary,
                        contentColor = TechOnPrimary
                    )
                ) {
                    Text(
                        text = "TÓCAME",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// GRÁFICO DE PULSO (CANVAS)
// -------------------------------------------------------------
@Composable
private fun PulseGraph(
    triggerPico: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FasePulso")
    val fase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FaseAnim"
    )

    val amplitudPico = remember { Animatable(0f) }

    LaunchedEffect(triggerPico) {
        if (triggerPico > 0) {
            amplitudPico.snapTo(1f)
            amplitudPico.animateTo(
                targetValue = 0f,
                animationSpec = tween(450, easing = FastOutSlowInEasing)
            )
        }
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val baseY = h * 0.72f

        val path = Path()
        val puntos = 60
        val stepX = w / puntos

        for (i in 0..puntos) {
            val x = i * stepX
            val onda = (sin((fase + (i * 0.25f)).toDouble()) * 4.dp.toPx()).toFloat()

            // Si hay un pico activo, lo dibujamos en la zona central (x ~ 60%)
            val centroPico = puntos * 0.65f
            val dist = kotlin.math.abs(i - centroPico)
            val impulso = if (dist < 5) {
                (1f - (dist / 5f)) * amplitudPico.value * (h * 0.65f)
            } else 0f

            val y = baseY + onda - impulso

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = TechPrimary,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

// -------------------------------------------------------------
// COMPONENTE: JOB 01 · PROGRESO
// -------------------------------------------------------------
@Composable
private fun Job01ProgresoCard(
    mensaje: String,
    progreso: Float,
    corriendo: Boolean,
    onIniciar: () -> Unit,
    onCancelar: () -> Unit
) {
    val progresoAnimado by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "ProgresoJob01"
    )

    val estadoBadge = when {
        corriendo -> BadgeState("RUNNING", TechPrimary, TechPrimaryContainer)
        mensaje == "Terminó" -> BadgeState("DONE", TechPrimary, TechPrimaryContainer)
        mensaje == "Cancelado" -> BadgeState("CANCELLED", TechWarning, SurfaceContainerHighColor)
        else -> BadgeState("IDLE", OnSurfaceVariantColor, SurfaceContainerHighColor)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerColor),
        border = BorderStroke(1.dp, OutlineVariantColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JOB 01 · PROGRESO",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurfaceVariantColor
                )
                JobBadge(state = estadoBadge)
            }

            // Porcentaje grande + barra
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "${(progresoAnimado * 100).toInt()}%",
                    style = TextStyle(
                        fontFamily = TechMonoFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 26.sp
                    ),
                    color = TechPrimary,
                    modifier = Modifier.width(70.dp)
                )

                LinearProgressIndicator(
                    progress = { progresoAnimado },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TechPrimary,
                    trackColor = SurfaceContainerHighColor
                )
            }

            Text(
                text = "status: $mensaje",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantColor
            )

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TechButton(
                    texto = "INICIAR",
                    habilitado = !corriendo,
                    onClick = onIniciar,
                    modifier = Modifier.weight(1f),
                    colorBorde = TechPrimary
                )

                TechButton(
                    texto = "CANCELAR",
                    habilitado = corriendo,
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    colorBorde = if (corriendo) TechWarning else OutlineVariantColor,
                    colorTexto = if (corriendo) TechWarning else DisabledText
                )
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENTE: JOB 02 · PRIMOS
// -------------------------------------------------------------
@Composable
private fun Job02PrimosCard(
    hasta: String,
    onHastaChange: (String) -> Unit,
    limite: Int?,
    primos: PrimosUi,
    calculandoPrimos: Boolean,
    onContar: () -> Unit,
    onCancelar: () -> Unit
) {
    val progresoValor = (primos as? PrimosUi.Calculando)?.progreso ?: 0f
    val progresoAnimado by animateFloatAsState(
        targetValue = progresoValor,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "ProgresoPrimos"
    )

    val estadoBadge = when (primos) {
        is PrimosUi.Calculando -> BadgeState("RUNNING", TechSecondary, TechSecondaryContainer)
        is PrimosUi.Listo -> BadgeState("DONE", TechSecondary, TechSecondaryContainer)
        PrimosUi.Cancelado -> BadgeState("CANCELLED", TechWarning, SurfaceContainerHighColor)
        PrimosUi.Inicial -> BadgeState("IDLE", OnSurfaceVariantColor, SurfaceContainerHighColor)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerColor),
        border = BorderStroke(1.dp, OutlineVariantColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JOB 02 · PRIMOS",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurfaceVariantColor
                )
                JobBadge(state = estadoBadge)
            }

            // Input estilizado tipo terminal: N > 1250000
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerHighColor,
                border = BorderStroke(1.dp, OutlineVariantColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "N  ▶  ",
                        style = TextStyle(fontFamily = TechMonoFont, fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        color = TechSecondary
                    )
                    BasicTextField(
                        value = hasta,
                        onValueChange = onHastaChange,
                        enabled = !calculandoPrimos,
                        textStyle = TextStyle(
                            fontFamily = TechMonoFont,
                            fontSize = 14.sp,
                            color = if (calculandoPrimos) DisabledText else OnSurfaceColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(TechSecondary),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Progreso durante cálculo
            AnimatedVisibility(
                visible = calculandoPrimos,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "${(progresoAnimado * 100).toInt()}%",
                        style = TextStyle(
                            fontFamily = TechMonoFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 24.sp
                        ),
                        color = TechSecondary,
                        modifier = Modifier.width(64.dp)
                    )

                    LinearProgressIndicator(
                        progress = { progresoAnimado },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TechSecondary,
                        trackColor = SurfaceContainerHighColor
                    )
                }
            }

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TechButton(
                    texto = "CONTAR",
                    habilitado = limite != null && !calculandoPrimos,
                    onClick = onContar,
                    modifier = Modifier.weight(1f),
                    colorBorde = TechSecondary,
                    colorTexto = TechSecondary
                )

                TechButton(
                    texto = "CANCELAR",
                    habilitado = calculandoPrimos,
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    colorBorde = if (calculandoPrimos) TechWarning else OutlineVariantColor,
                    colorTexto = if (calculandoPrimos) TechWarning else DisabledText
                )
            }

            // Consola OUTPUT
            when (primos) {
                is PrimosUi.Listo -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerHighColor,
                        border = BorderStroke(1.dp, OutlineVariantColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "OUTPUT",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariantColor
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("primes", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantColor)
                                Text("${primos.cantidad}", style = MaterialTheme.typography.bodySmall, color = TechSecondary, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("elapsed", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantColor)
                                Text("${primos.milisegundos} ms", style = MaterialTheme.typography.bodySmall, color = OnSurfaceColor, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                PrimosUi.Cancelado -> {
                    Text(
                        text = "status: Cancelado",
                        style = MaterialTheme.typography.bodySmall,
                        color = TechWarning
                    )
                }
                else -> {}
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENTE: JOB 03 · DOS CARGAS + BENCHMARK LOG
// -------------------------------------------------------------
@Composable
private fun Job03CargasCard(
    simularError: Boolean,
    onCambiarSimularError: (Boolean) -> Unit,
    cargando: Boolean,
    carga: CargaUi,
    tiempos: List<TiempoCarga>,
    onCargarUnaPorUna: () -> Unit,
    onCargarALaVez: () -> Unit
) {
    var modoSeleccionado by rememberSaveable { mutableStateOf("A la vez") }

    val estadoBadge = when (carga) {
        is CargaUi.Cargando -> BadgeState("RUNNING", AccentViolet, AccentVioletContainer)
        is CargaUi.Exito -> BadgeState("DONE", AccentViolet, AccentVioletContainer)
        is CargaUi.Error -> BadgeState("ERROR", TechError, SurfaceContainerHighColor)
        CargaUi.Inicial -> BadgeState("IDLE", OnSurfaceVariantColor, SurfaceContainerHighColor)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerColor),
        border = BorderStroke(1.dp, OutlineVariantColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JOB 03 · DOS CARGAS",
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurfaceVariantColor
                )
                JobBadge(state = estadoBadge)
            }

            // Fila simular_error [ switch ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "simular_error",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (simularError) "[ ON ]" else "[ OFF ]",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (simularError) TechError else OnSurfaceVariantColor
                    )
                    Switch(
                        modifier = Modifier.semantics { contentDescription = "Simular error" },
                        checked = simularError,
                        onCheckedChange = onCambiarSimularError,
                        enabled = !cargando,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TechError,
                            checkedTrackColor = SurfaceContainerHighColor,
                            uncheckedThumbColor = OnSurfaceVariantColor,
                            uncheckedTrackColor = SurfaceContainerHighColor
                        )
                    )
                }
            }

            // Mode Selector (Segmented Button estilizado)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHighColor,
                border = BorderStroke(1.dp, OutlineVariantColor)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    // Botón Una por una
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (modoSeleccionado == "Una por una") AccentViolet else Color.Transparent)
                            .clickable(enabled = !cargando) {
                                modoSeleccionado = "Una por una"
                                onCargarUnaPorUna()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "UNA POR UNA",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (modoSeleccionado == "Una por una") TechOnPrimary else OnSurfaceColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón A la vez
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (modoSeleccionado == "A la vez") AccentViolet else Color.Transparent)
                            .clickable(enabled = !cargando) {
                                modoSeleccionado = "A la vez"
                                onCargarALaVez()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A LA VEZ",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (modoSeleccionado == "A la vez") TechOnPrimary else OnSurfaceColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Indicador de carga activa
            if (cargando) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = AccentViolet,
                        strokeWidth = 2.dp
                    )
                    Text("Cargando perfil y pedidos...", style = MaterialTheme.typography.bodySmall, color = AccentViolet)
                }
            }

            // Resultado de carga (Éxito o Error)
            when (carga) {
                is CargaUi.Exito -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = TechPrimaryContainer,
                        border = BorderStroke(1.dp, TechPrimaryBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "✓ ${carga.perfil} · ${carga.pedidos} pedidos",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${carga.milisegundos} ms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TechPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                if (carga.modo == "A la vez") {
                                    Text(
                                        text = "-40% vs secuencial",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TechWarning,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                is CargaUi.Error -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceContainerHighColor,
                        border = BorderStroke(1.dp, TechError)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "✗ ${carga.mensaje}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TechError
                            )
                            Button(
                                onClick = onCargarALaVez,
                                modifier = Modifier.height(38.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TechError,
                                    contentColor = TechOnPrimary
                                )
                            ) {
                                Text("REINTENTAR", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                else -> {}
            }

            HorizontalDivider(color = OutlineVariantColor, thickness = 1.dp)

            // BENCHMARK LOG
            BenchmarkLogSection(tiempos = tiempos)
        }
    }
}

// -------------------------------------------------------------
// COMPONENTE: BENCHMARK LOG CON BARRAS PROPORCIONALES
// -------------------------------------------------------------
@Composable
private fun BenchmarkLogSection(tiempos: List<TiempoCarga>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "BENCHMARK LOG",
            style = MaterialTheme.typography.titleSmall,
            color = OnSurfaceVariantColor
        )

        if (tiempos.isEmpty()) {
            Text(
                text = "Sin mediciones. Ejecuta una carga para registrar su tiempo.",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantColor
            )
        } else {
            val maxTiempo = remember(tiempos) {
                tiempos.maxOfOrNull { it.milisegundos }?.toFloat()?.coerceAtLeast(1f) ?: 1f
            }

            tiempos.forEachIndexed { index, tiempo ->
                val proporcion = (tiempo.milisegundos / maxTiempo).coerceIn(0.08f, 1f)
                val barraAnimada by animateFloatAsState(
                    targetValue = proporcion,
                    animationSpec = tween(400, easing = FastOutSlowInEasing),
                    label = "BarraBenchmark"
                )

                val esSecuencial = tiempo.modo == "Una por una"
                val colorBarra = if (esSecuencial) BarSequential else AccentViolet
                val etiquetaModo = if (esSecuencial) "secuencial" else "concurrente"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantColor,
                        modifier = Modifier.width(18.dp)
                    )

                    Text(
                        text = etiquetaModo,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceColor,
                        modifier = Modifier.width(86.dp)
                    )

                    // Barra proporcional
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerHighColor)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(barraAnimada)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colorBarra)
                        )
                    }

                    Text(
                        text = "${tiempo.milisegundos} ms",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (esSecuencial) OnSurfaceColor else AccentViolet,
                        modifier = Modifier.width(66.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Resumen al pie: best / avg / n
            val best = remember(tiempos) { tiempos.minOfOrNull { it.milisegundos } ?: 0L }
            val avg = remember(tiempos) { tiempos.map { it.milisegundos }.average().toLong() }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                color = SurfaceContainerHighColor
            ) {
                Text(
                    text = "best $best ms  ·  avg $avg ms  ·  n=${tiempos.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariantColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// -------------------------------------------------------------
// FOOTER INSTITUCIONAL TECSUP
// -------------------------------------------------------------
@Composable
private fun TechFooter() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BgColor,
        border = BorderStroke(1.dp, OutlineVariantColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TECSUP · Programación en Móviles",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                color = OnSurfaceVariantColor
            )
            Text(
                text = "Lab 14",
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariantColor
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENTES AUXILIARES ESTILIZADOS
// -------------------------------------------------------------
@Composable
private fun StatusChip(
    texto: String,
    activo: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = if (activo) TechPrimaryContainer else SurfaceContainerHighColor,
        border = BorderStroke(1.dp, if (activo) TechPrimary else OutlineVariantColor)
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            color = if (activo) TechPrimary else OnSurfaceVariantColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

private data class BadgeState(
    val texto: String,
    val colorTexto: Color,
    val colorFondo: Color
)

@Composable
private fun JobBadge(state: BadgeState) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = state.colorFondo
    ) {
        Text(
            text = state.texto,
            style = MaterialTheme.typography.labelSmall,
            color = state.colorTexto,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TechButton(
    texto: String,
    habilitado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorBorde: Color = TechPrimary,
    colorTexto: Color = OnSurfaceColor
) {
    val alphaAnimado by animateFloatAsState(
        targetValue = if (habilitado) 1f else 0.45f,
        animationSpec = tween(200),
        label = "AlphaBoton"
    )

    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .height(44.dp)
            .scale(if (habilitado) 1f else 0.98f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (habilitado) colorBorde else OutlineVariantColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceColor.copy(alpha = alphaAnimado),
            contentColor = colorTexto,
            disabledContentColor = DisabledText
        )
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge,
            color = if (habilitado) colorTexto else DisabledText
        )
    }
}
