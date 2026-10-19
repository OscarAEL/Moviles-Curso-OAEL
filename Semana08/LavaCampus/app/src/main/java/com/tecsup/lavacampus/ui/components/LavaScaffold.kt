package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.theme.LavaTheme

/**
 * Contenedor y Scaffold reutilizable para las pantallas de LavaCampus.
 * Aplica la pauta centralizada para el manejo de [WindowInsets] (Edge-to-Edge)
 * y el tema correspondiente al [rol] de usuario.
 */
@Composable
fun LavaScaffold(
    modifier: Modifier = Modifier,
    rol: Rol = Rol.CLIENTE,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = Color.Unspecified,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    LavaTheme(rol = rol) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            floatingActionButton = floatingActionButton,
            containerColor = if (containerColor == Color.Unspecified) MaterialTheme.colorScheme.background else containerColor,
            contentWindowInsets = contentWindowInsets,
            content = content
        )
    }
}
