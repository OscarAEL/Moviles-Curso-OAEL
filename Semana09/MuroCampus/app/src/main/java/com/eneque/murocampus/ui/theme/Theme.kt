package com.eneque.murocampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzulCampusClaro,
    onPrimary = Color(0xFF003353),
    secondary = AzulSecundarioClaro
)

private val LightColorScheme = lightColorScheme(
    primary = AzulCampus,
    onPrimary = Color.White,
    secondary = AzulSecundario,
    background = Color(0xFFFCFAFF),
    surface = Color(0xFFFCFAFF)
)

@Composable
fun MuroCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
