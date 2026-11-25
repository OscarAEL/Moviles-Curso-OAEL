package com.eneque.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(
    primary = AzulElectrico,
    onPrimary = Color.White,
    secondary = LimaCampus,
    onSecondary = TintaCampus,
    tertiary = RosaCampus,
    onTertiary = TintaCampus,
    background = FondoCampus,
    onBackground = TintaCampus,
    surface = Color.White,
    onSurface = TintaCampus,
    surfaceVariant = LavandaCampus,
    onSurfaceVariant = Color(0xFF515977),
    outline = TintaCampus,
    error = Color(0xFFB6194D),
    errorContainer = Color(0xFFFFDCE9),
    onErrorContainer = TintaCampus
)

private val Oscuro = darkColorScheme(
    primary = Color(0xFFAAB5FF),
    onPrimary = TintaCampus,
    secondary = LimaCampus,
    onSecondary = TintaCampus,
    tertiary = RosaCampus,
    onTertiary = TintaCampus,
    background = Color(0xFF14172D),
    onBackground = Color(0xFFF4F4FF),
    surface = Color(0xFF242940),
    onSurface = Color(0xFFF4F4FF),
    surfaceVariant = Color(0xFF353D60),
    onSurfaceVariant = Color(0xFFC7CEEB),
    outline = Color(0xFFABB5DF),
    errorContainer = Color(0xFF5A2037),
    onErrorContainer = Color(0xFFFFDAE6)
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) Oscuro else Claro,
        typography = Typography,
        content = content
    )
}
