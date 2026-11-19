package com.eneque.campusgps.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CampusPrimary, onPrimary = Color.White,
    primaryContainer = CampusSelected, onPrimaryContainer = CampusTitle,
    secondary = CampusAction, onSecondary = CampusOnAction,
    secondaryContainer = CampusVariant, onSecondaryContainer = CampusText,
    background = CampusBackground, onBackground = CampusText,
    surface = Color.White, onSurface = CampusText,
    surfaceVariant = CampusVariant, onSurfaceVariant = CampusSecondaryText,
    surfaceContainer = CampusBackground,
    surfaceContainerLow = Color.White,
    outline = CampusOutline, outlineVariant = CampusOutline,
    error = Color(0xFFB3261E), onError = Color.White,
    errorContainer = Color(0xFFFDECEA), onErrorContainer = Color(0xFF812018)
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF83D5C9), onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF174B44), onPrimaryContainer = Color(0xFFDCEFEB),
    secondary = CampusAction, onSecondary = CampusOnAction,
    secondaryContainer = Color(0xFF35413B), onSecondaryContainer = Color(0xFFE3E8E2),
    background = Color(0xFF101B19), onBackground = Color(0xFFE3E8E2),
    surface = Color(0xFF182521), onSurface = Color(0xFFE3E8E2),
    surfaceVariant = Color(0xFF293833), onSurfaceVariant = Color(0xFFBDC9C1),
    surfaceContainer = Color(0xFF1C2D27), surfaceContainerLow = Color(0xFF182521),
    outline = Color(0xFF60746A), outlineVariant = Color(0xFF394C42)
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colors, typography = Typography, content = content)
}
