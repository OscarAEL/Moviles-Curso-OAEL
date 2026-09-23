package com.eneque.lab05navegacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AcademicLilaAccent,
    secondary = AcademicPurpleSecondary,
    tertiary = AcademicLilaContainer,
    background = AcademicPurpleDark,
    surface = AcademicPurpleDark,
    onPrimary = AcademicOnPrimary,
    onBackground = AcademicOnPrimary,
    onSurface = AcademicOnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AcademicPurplePrimary,
    secondary = AcademicPurpleSecondary,
    tertiary = AcademicLilaAccent,
    primaryContainer = AcademicLilaContainer,
    onPrimaryContainer = AcademicPurpleDark,
    background = AcademicBackground,
    surface = AcademicSurface,
    onPrimary = AcademicOnPrimary,
    onBackground = AcademicOnBackground,
    onSurface = AcademicOnSurface
)

@Composable
fun Lab05NavegacionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}