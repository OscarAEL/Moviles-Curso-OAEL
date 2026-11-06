package com.tecsup.miscursoseneque

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color definitions matching the design system
object PremiumColors {
    val HeaderGradientStart = Color(0xFF0B2A6F)
    val HeaderGradientMid = Color(0xFF262590)
    val HeaderGradientEnd = Color(0xFF45279F)

    val ButtonGradientStart = Color(0xFF2B5FE0)
    val ButtonGradientEnd = Color(0xFF6B47F5)

    val TurquoiseAccent = Color(0xFF2DD4BF)

    val TextDark = Color(0xFF0F172A)
    val TextMuted = Color(0xFF64748B)
    val TextLight = Color(0xFF94A3B8)

    val InputBorderFocused = Color(0xFF6366F1)
    val InputBorderUnfocused = Color(0xFFE2E8F0)
}

val HeaderGradientBrush = Brush.linearGradient(
    colors = listOf(
        PremiumColors.HeaderGradientStart,
        PremiumColors.HeaderGradientMid,
        PremiumColors.HeaderGradientEnd
    )
)

/**
 * Top Header row showing "OSCAR ENEQUE" on the left and Tecsup official logo on the right.
 */
@Composable
fun TopHeaderRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "OSCAR ENEQUE",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.sp,
            maxLines = 1
        )

        Image(
            painter = painterResource(id = R.drawable.logotecsup_blanco),
            contentDescription = "Logo Tecsup",
            contentScale = ContentScale.Fit,
            modifier = Modifier.height(32.dp)
        )
    }
}

/**
 * Canvas decoration drawing concentric rings, dot matrix grid, and turquoise dot.
 */
@Composable
fun HeaderBackgroundDecoration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val centerX = width * 0.82f
        val centerY = height * 0.45f

        val ringColor = Color.White.copy(alpha = 0.12f)
        val strokeWidth = 1.5.dp.toPx()

        drawCircle(
            color = ringColor,
            radius = 100.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = strokeWidth)
        )
        drawCircle(
            color = ringColor,
            radius = 160.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = strokeWidth)
        )
        drawCircle(
            color = ringColor,
            radius = 220.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = strokeWidth)
        )

        val angle = Math.toRadians(-45.0)
        val dotRadius = 100.dp.toPx()
        val dotX = centerX + (dotRadius * kotlin.math.cos(angle)).toFloat()
        val dotY = centerY + (dotRadius * kotlin.math.sin(angle)).toFloat()
        drawCircle(
            color = PremiumColors.TurquoiseAccent,
            radius = 6.dp.toPx(),
            center = Offset(dotX, dotY)
        )

        val matrixStartX = width * 0.70f
        val matrixStartY = height * 0.68f
        val dotSpacing = 12.dp.toPx()
        for (r in 0..2) {
            for (c in 0..6) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.20f),
                    radius = 1.5.dp.toPx(),
                    center = Offset(matrixStartX + c * dotSpacing, matrixStartY + r * dotSpacing)
                )
            }
        }
    }
}

/**
 * Central academic illustration for HomeScreen header (mortarboard in circle + rings + floating rotated wireframe squares).
 */
@Composable
fun AcademicHeaderIllustration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)

            val strokeWidth = 1.5.dp.toPx()
            val ringColor = Color.White.copy(alpha = 0.15f)

            drawCircle(color = ringColor, radius = 68.dp.toPx(), center = center, style = Stroke(strokeWidth))
            drawCircle(color = ringColor, radius = 108.dp.toPx(), center = center, style = Stroke(strokeWidth))
            drawCircle(color = ringColor, radius = 148.dp.toPx(), center = center, style = Stroke(strokeWidth))

            val angle = Math.toRadians(-50.0)
            val ringRadius = 108.dp.toPx()
            val dotX = center.x + ringRadius * Math.cos(angle).toFloat()
            val dotY = center.y + ringRadius * Math.sin(angle).toFloat()
            drawCircle(color = PremiumColors.TurquoiseAccent, radius = 6.5.dp.toPx(), center = Offset(dotX, dotY))
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp)
                .offset(y = 20.dp)
                .size(40.dp)
                .rotate(-15f)
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp)
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 28.dp)
                .offset(y = 25.dp)
                .size(32.dp)
                .rotate(20f)
                .border(
                    width = 1.5.dp,
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Box(
            modifier = Modifier
                .size(90.dp)
                .background(
                    color = Color(0xFF3F4BB8),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            GraduationCapIcon(
                modifier = Modifier.size(46.dp),
                tint = Color.White
            )
        }
    }
}

/**
 * Graduation Cap (Mortarboard) vector drawn on Canvas.
 */
@Composable
fun GraduationCapIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val capPath = Path().apply {
            moveTo(w * 0.5f, h * 0.22f)
            lineTo(w * 0.88f, h * 0.38f)
            lineTo(w * 0.5f, h * 0.54f)
            lineTo(w * 0.12f, h * 0.38f)
            close()
        }
        drawPath(capPath, color = tint, style = Fill)

        val baseCapPath = Path().apply {
            moveTo(w * 0.26f, h * 0.48f)
            lineTo(w * 0.26f, h * 0.68f)
            cubicTo(w * 0.26f, h * 0.78f, w * 0.74f, h * 0.78f, w * 0.74f, h * 0.68f)
            lineTo(w * 0.74f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.58f)
            close()
        }
        drawPath(baseCapPath, color = tint, style = Fill)

        val tasselPath = Path().apply {
            moveTo(w * 0.82f, h * 0.41f)
            lineTo(w * 0.82f, h * 0.68f)
        }
        drawPath(tasselPath, color = tint, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

        drawCircle(color = tint, radius = 2.5.dp.toPx(), center = Offset(w * 0.82f, h * 0.70f))
    }
}

/**
 * Custom Input field matching prototype design: rounded container, internal label, placeholder, trailing icon.
 */
@Composable
fun PremiumInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    trailingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = if (isFocused) PremiumColors.InputBorderFocused else PremiumColors.InputBorderUnfocused
    val labelColor = if (isFocused) PremiumColors.InputBorderFocused else PremiumColors.TextMuted
    val iconColor = if (isFocused) PremiumColors.InputBorderFocused else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .border(
                width = if (isFocused) 1.8.dp else 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    singleLine = true,
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = keyboardOptions,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = PremiumColors.TextDark
                    ),
                    cursorBrush = SolidColor(PremiumColors.InputBorderFocused),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                fontSize = 15.sp,
                                color = PremiumColors.TextLight
                            )
                        }
                        innerTextField()
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Main Gradient Action Button with blue to violet gradient and turquoise action circle chip.
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    val buttonBrush = Brush.horizontalGradient(
        colors = listOf(
            PremiumColors.ButtonGradientStart,
            PremiumColors.ButtonGradientEnd
        )
    )

    Surface(
        onClick = onClick,
        enabled = !isLoading,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 6.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(buttonBrush),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                        .size(40.dp)
                        .background(PremiumColors.TurquoiseAccent, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PremiumColors.TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Display box showing the current user's email in HomeScreen.
 */
@Composable
fun EmailDisplayBox(
    email: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sesión iniciada como",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF1F0FF), shape = RoundedCornerShape(16.dp))
                .border(width = 1.dp, color = Color(0xFFE0E0FF), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = email.ifEmpty { "usuario@correo.com" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumColors.TextDark
                )
            }
        }
    }
}

/**
 * Common footer text at the bottom of the screens.
 */
@Composable
fun AppFooter(
    modifier: Modifier = Modifier
) {
    Text(
        text = "Tecsup · Programación Móvil",
        fontSize = 12.sp,
        color = PremiumColors.TextLight,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}
