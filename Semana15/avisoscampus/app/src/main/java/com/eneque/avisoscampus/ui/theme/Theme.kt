package com.eneque.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberTechColorScheme = darkColorScheme(
    primary = CyanTecsup,
    onPrimary = TextoSobreCyan,
    primaryContainer = Color(0xFF0E2A4A),
    onPrimaryContainer = CyanClaro,
    secondary = CyanClaro,
    onSecondary = TextoSobreCyan,
    secondaryContainer = Color(0xFF101F4A),
    onSecondaryContainer = Color(0xFF9DB8FF),
    tertiary = Color(0xFFFFC857),
    onTertiary = Color(0xFF2E2410),
    background = FondoMedianoche,
    onBackground = TextoPrincipal,
    surface = TarjetaInicio,
    onSurface = TextoPrincipal,
    surfaceVariant = TarjetaFin,
    onSurfaceVariant = TextoSecundario,
    outline = MicroBorde,
    outlineVariant = Color(0xFF0E2442),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = ErrorBannerFondo,
    onErrorContainer = ErrorBannerTexto
)

@Composable
fun AvisosCampusTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberTechColorScheme,
        typography = Typography,
        content = content
    )
}
