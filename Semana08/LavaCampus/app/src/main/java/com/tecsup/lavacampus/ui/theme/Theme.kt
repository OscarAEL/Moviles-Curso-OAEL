package com.tecsup.lavacampus.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.lavacampus.model.Rol

private fun colores(admin: Boolean) =
    lightColorScheme(
        primary = Color(if (admin) 0xFF7025E8 else 0xFF0C7892),
        onPrimary = Color.White,
        primaryContainer = Color(if (admin) 0xFFEDE7FF else 0xFFC9F9FE),
        onPrimaryContainer = Color(if (admin) 0xFF241047 else 0xFF07313D),
        secondary = Color(if (admin) 0xFF200C3F else 0xFF062F3A),
        onSecondary = Color.White,
        secondaryContainer = Color(if (admin) 0xFFD5C5FF else 0xFF1CCAE1),
        onSecondaryContainer = Color(if (admin) 0xFF200C3F else 0xFF062F3A),
        background = Color(if (admin) 0xFFFAF8FF else 0xFFF4FBFC),
        onBackground = Color(if (admin) 0xFF200C3F else 0xFF07313D),
        surface = Color.White,
        onSurface = Color(if (admin) 0xFF200C3F else 0xFF07313D),
        onSurfaceVariant = Color(if (admin) 0xFF67577F else 0xFF496E7A),
        surfaceContainerHighest = Color(if (admin) 0xFFECE5F8 else 0xFFDFEFF3),
        outline = Color(if (admin) 0xFFBDA9DC else 0xFF8EBFCB),
        outlineVariant = Color(if (admin) 0xFFE5DDF2 else 0xFFD6EBEF),
        error = Color(0xFFBB2029),
        errorContainer = Color(0xFFFFE4E5),
        onErrorContainer = Color(0xFF8E1720),
    )

@Suppress("UNUSED_PARAMETER")
@Composable
fun LavaTheme(rol: Rol = Rol.CLIENTE, darkTheme: Boolean = false, content: @Composable () -> Unit) {
    // La opción oscura permanece visible, pero el rediseño usa siempre el tema claro.
    MaterialTheme(
        colorScheme = if (rol == Rol.SOPORTE) colores(false).copy(
            primary = Color(0xFF087F5B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD5F5E6),
            onPrimaryContainer = Color(0xFF073C2B),
            secondary = Color(0xFF073C2B),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF76E0AE),
            onSecondaryContainer = Color(0xFF073C2B),
            background = Color(0xFFF3FBF6),
            onBackground = Color(0xFF103E2D),
            onSurface = Color(0xFF103E2D),
            onSurfaceVariant = Color(0xFF446B57),
            surfaceContainerHighest = Color(0xFFE0F0E7),
            outline = Color(0xFF8CBBA2),
            outlineVariant = Color(0xFFD5E9DD),
        ) else colores(rol == Rol.ADMIN),
        typography = Typography,
        content = content,
    )
}
