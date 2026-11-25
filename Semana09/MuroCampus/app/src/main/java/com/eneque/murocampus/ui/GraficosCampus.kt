package com.eneque.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eneque.murocampus.R
import com.eneque.murocampus.ui.theme.*

fun Modifier.sombraDura(radio: Dp = 24.dp, distancia: Dp = 5.dp): Modifier = drawBehind {
    drawRoundRect(
        color = TintaCampus,
        topLeft = Offset(distancia.toPx(), distancia.toPx()),
        size = size,
        cornerRadius = CornerRadius(radio.toPx())
    )
}

enum class Simbolo { Buscar, Atras, Sumar, Enviar, Eliminar, Comprobar, Aviso, Documento }

@Composable
fun IconoCampus(simbolo: Simbolo, modifier: Modifier = Modifier, color: Color = TintaCampus) {
    Canvas(modifier.size(24.dp)) {
        val unidad = size.minDimension / 24f
        fun punto(x: Float, y: Float) = Offset(x * unidad, y * unidad)
        fun linea(x: Float, y: Float, x2: Float, y2: Float) =
            drawLine(color, punto(x, y), punto(x2, y2), 2.4f * unidad, StrokeCap.Round)
        when (simbolo) {
            Simbolo.Buscar -> {
                drawCircle(color, 6.5f * unidad, punto(10f, 10f), style = Stroke(2.4f * unidad))
                linea(15f, 15f, 21f, 21f)
            }
            Simbolo.Atras -> { linea(20f, 12f, 4f, 12f); linea(4f, 12f, 11f, 5f); linea(4f, 12f, 11f, 19f) }
            Simbolo.Sumar -> { linea(4f, 12f, 20f, 12f); linea(12f, 4f, 12f, 20f) }
            Simbolo.Comprobar -> { linea(4f, 12f, 10f, 18f); linea(10f, 18f, 21f, 6f) }
            Simbolo.Aviso -> {
                drawCircle(color, 9f * unidad, punto(12f, 12f), style = Stroke(2.4f * unidad))
                linea(12f, 6f, 12f, 13f)
                drawCircle(color, 1.4f * unidad, punto(12f, 17f))
            }
            Simbolo.Eliminar -> {
                linea(4f, 6f, 20f, 6f); linea(9f, 3f, 15f, 3f)
                linea(6f, 9f, 7f, 21f); linea(7f, 21f, 17f, 21f); linea(17f, 21f, 18f, 9f)
                linea(10f, 10f, 10f, 17f); linea(14f, 10f, 14f, 17f)
            }
            Simbolo.Enviar -> {
                val ruta = Path().apply {
                    moveTo(3f * unidad, 3f * unidad); lineTo(22f * unidad, 12f * unidad)
                    lineTo(3f * unidad, 21f * unidad); lineTo(7f * unidad, 12f * unidad); close()
                }
                drawPath(ruta, color)
            }
            Simbolo.Documento -> {
                drawRoundRect(color, punto(4f, 2f), androidx.compose.ui.geometry.Size(16f * unidad, 20f * unidad), CornerRadius(2f * unidad), style = Stroke(2f * unidad))
                linea(8f, 8f, 16f, 8f); linea(8f, 12f, 16f, 12f); linea(8f, 16f, 12f, 16f)
            }
        }
    }
}

@Composable
fun PanelGrafico(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.sombraDura(),
        color = color,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, TintaCampus)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
    }
}

@Composable
fun BotonGrafico(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Color = LimaCampus,
    enabled: Boolean = true,
    simbolo: Simbolo? = null
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 52.dp).sombraDura(100.dp, if (enabled) 5.dp else 2.dp),
        shape = RoundedCornerShape(100.dp),
        color = if (enabled) fondo else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = TintaCampus,
        border = BorderStroke(2.dp, TintaCampus)
    ) {
        Row(
            Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (simbolo != null) IconoCampus(simbolo)
            Text(texto, style = MaterialTheme.typography.labelLarge, color = if (enabled) TintaCampus else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ChipGrafico(texto: String, activo: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.heightIn(min = 48.dp).sombraDura(100.dp, if (activo) 4.dp else 0.dp)
            .semantics { selected = activo; role = Role.RadioButton },
        shape = RoundedCornerShape(100.dp),
        border = BorderStroke(2.dp, TintaCampus),
        color = if (activo) AzulElectrico else MaterialTheme.colorScheme.surface,
        contentColor = if (activo) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Box(Modifier.padding(horizontal = 18.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
            Text(texto, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun EtiquetaUsuario(userId: Int) {
    val acento = when ((userId - 1) % 3) { 1 -> NaranjaCampus; 2 -> RosaCampus; else -> LimaCampus }
    Surface(shape = CircleShape, color = acento, border = BorderStroke(1.5.dp, TintaCampus)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = TintaCampus) {
                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                    Text("$userId", color = acento, style = MaterialTheme.typography.labelLarge)
                }
            }
            Text("Usuario $userId", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), color = TintaCampus, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun EtiquetaGrafica(texto: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier, color = color, shape = CircleShape, border = BorderStroke(1.5.dp, TintaCampus)) {
        Text(texto, Modifier.padding(horizontal = 12.dp, vertical = 5.dp), color = TintaCampus, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun LogoTecsup() {
    Surface(
        modifier = Modifier.sombraDura(12.dp, 3.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, TintaCampus)
    ) {
        Image(
            painterResource(R.drawable.tecsup_logo),
            contentDescription = "Tecsup",
            modifier = Modifier.width(112.dp).height(40.dp).padding(horizontal = 2.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun EncabezadoCampus(
    titulo: String = "MuroCampus",
    contador: String? = null,
    grande: Boolean = false,
    onVolver: (() -> Unit)? = null
) {
    Box(Modifier.fillMaxWidth().background(AzulElectrico).clipToBounds()) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(RosaCampus, 72.dp.toPx(), Offset(size.width + 18.dp.toPx(), 74.dp.toPx()))
            drawCircle(Color.White, 39.dp.toPx(), Offset(size.width - 42.dp.toPx(), size.height - 22.dp.toPx()), style = Stroke(2.5.dp.toPx()))
        }
        Column(
            Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .padding(horizontal = 20.dp).padding(top = 12.dp, bottom = if (grande) 26.dp else 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (onVolver != null) {
                    Surface(
                        onClick = onVolver,
                        modifier = Modifier.size(48.dp).sombraDura(100.dp, 3.dp).semantics { contentDescription = "Atrás" },
                        shape = CircleShape, color = Color.White, border = BorderStroke(2.dp, TintaCampus)
                    ) { Box(contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Atras) } }
                } else {
                    Text("COMUNIDAD\nUNIVERSITARIA", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = Color.White, letterSpacing = 1.sp)
                }
                LogoTecsup()
            }
            if (grande) {
                val ampliado = LocalDensity.current.fontScale > 1.15f
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val contadorCorto = if (maxWidth < 340.dp && contador?.endsWith(" publicaciones") == true) contador.replaceFirst(" ", "\n") else contador
                    Column {
                        Text(if (titulo == "MuroCampus") "Muro" else "Nueva", color = Color.White, style = MaterialTheme.typography.displayLarge)
                        Text(if (titulo == "MuroCampus") "Campus" else "publicación", color = LimaCampus, style = if (titulo == "MuroCampus") MaterialTheme.typography.displayLarge else MaterialTheme.typography.headlineLarge)
                    }
                    if (contador != null && !ampliado) {
                        EtiquetaGrafica(contadorCorto ?: contador, LimaCampus, Modifier.align(Alignment.BottomEnd).rotate(4f).padding(bottom = 5.dp))
                    }
                }
                if (contador != null && ampliado) EtiquetaGrafica(contador, LimaCampus)
            } else {
                Text(titulo, color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
fun CampoBusqueda(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, singleLine = true,
        placeholder = { Text("Buscar por título") },
        leadingIcon = {
            Surface(shape = CircleShape, color = LimaCampus, border = BorderStroke(1.5.dp, TintaCampus)) {
                Box(Modifier.size(34.dp), contentAlignment = Alignment.Center) { IconoCampus(Simbolo.Buscar, Modifier.size(19.dp)) }
            }
        },
        modifier = modifier.fillMaxWidth().sombraDura(100.dp).semantics { contentDescription = "Buscar por título" },
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = TintaCampus, unfocusedBorderColor = TintaCampus,
            cursorColor = AzulElectrico
        ),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun CampoGrafico(
    value: String, onValueChange: (String) -> Unit, etiqueta: String,
    acento: Color, modifier: Modifier = Modifier, enabled: Boolean = true, multilinea: Boolean = false
) {
    Box(modifier.padding(top = 12.dp)) {
        OutlinedTextField(
            value = value, onValueChange = onValueChange, enabled = enabled,
            singleLine = !multilinea, minLines = if (multilinea) 4 else 1,
            modifier = Modifier.fillMaxWidth().sombraDura().semantics { contentDescription = etiqueta },
            placeholder = { Text(if (multilinea) "Comparte tu publicación…" else "Escribe un título") },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = TintaCampus, unfocusedBorderColor = TintaCampus,
                cursorColor = AzulElectrico
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        EtiquetaGrafica(etiqueta, acento, Modifier.align(Alignment.TopStart).offset(x = 16.dp, y = (-12).dp))
    }
}
