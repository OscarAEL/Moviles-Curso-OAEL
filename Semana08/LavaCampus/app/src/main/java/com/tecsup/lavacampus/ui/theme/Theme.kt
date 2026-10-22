package com.tecsup.lavacampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.lavacampus.model.Rol

private val EstudianteLightColorScheme = lightColorScheme(
    primary = EstudiantePrimary,
    primaryContainer = EstudianteContainer,
    background = EstudianteBackground,
    surface = Color.White,
    onPrimary = EstudianteOnPrimary,
    onPrimaryContainer = EstudianteOnContainer,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

private val EstudianteDarkColorScheme = darkColorScheme(
    primary = EstudiantePrimaryDark,
    primaryContainer = EstudianteContainerDark,
    background = EstudianteBackgroundDark,
    surface = Color(0xFF1E293B),
    onPrimary = EstudianteOnPrimaryDark,
    onPrimaryContainer = Color(0xFFCFFAFE),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val AdminLightColorScheme = lightColorScheme(
    primary = AdminPrimary,
    primaryContainer = AdminContainer,
    background = AdminBackground,
    surface = Color.White,
    onPrimary = AdminOnPrimary,
    onPrimaryContainer = AdminOnContainer,
    onBackground = Color(0xFF1E1B4B),
    onSurface = Color(0xFF1E1B4B)
)

private val AdminDarkColorScheme = darkColorScheme(
    primary = AdminPrimaryDark,
    primaryContainer = AdminContainerDark,
    background = AdminBackgroundDark,
    surface = Color(0xFF2E2B5F),
    onPrimary = AdminOnPrimaryDark,
    onPrimaryContainer = Color(0xFFEDE9FE),
    onBackground = Color(0xFFF5F3FF),
    onSurface = Color(0xFFF5F3FF)
)

@Composable
fun LavaTheme(
    rol: Rol = Rol.CLIENTE,
    darkTheme: Boolean = LocalModoOscuro.current,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> if (rol == Rol.ADMIN) AdminDarkColorScheme else EstudianteDarkColorScheme
        else -> if (rol == Rol.ADMIN) AdminLightColorScheme else EstudianteLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
