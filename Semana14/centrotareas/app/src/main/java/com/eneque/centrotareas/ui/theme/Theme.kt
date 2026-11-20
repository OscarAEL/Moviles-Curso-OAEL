package com.eneque.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TechPrimary,
    onPrimary = TechOnPrimary,
    primaryContainer = TechPrimaryContainer,
    secondary = TechSecondary,
    secondaryContainer = TechSecondaryContainer,
    tertiary = AccentViolet,
    tertiaryContainer = AccentVioletContainer,
    background = BgColor,
    surface = SurfaceColor,
    surfaceContainer = SurfaceContainerColor,
    surfaceContainerHigh = SurfaceContainerHighColor,
    onBackground = OnSurfaceColor,
    onSurface = OnSurfaceColor,
    onSurfaceVariant = OnSurfaceVariantColor,
    outlineVariant = OutlineVariantColor,
    error = TechError
)

@Composable
fun CentroTareasTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
