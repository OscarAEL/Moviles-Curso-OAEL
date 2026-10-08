package com.saludplus.citas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

// Barra de navegación inferior reutilizable desplegada en Scaffold(bottomBar = ...).
@Composable
fun BarraNavegacion(
    seccionActual: String, // Recibe el nombre de la pestaña activa para resaltarla en la UI.
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {

    // Componente Material3 NavigationBar que dibuja la barra inferior.
    NavigationBar {

        // Opción Inicio
        NavigationBarItem(
            selected = seccionActual == "Inicio", // Determina si la opción está seleccionada.
            onClick = onInicioClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            }
        )

        // Opción Citas
        NavigationBarItem(
            selected = seccionActual == "Citas",
            onClick = onCitasClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Citas"
                )
            },
            label = {
                Text("Citas")
            }
        )

        // Opción Resultados
        NavigationBarItem(
            selected = seccionActual == "Resultados",
            onClick = onResultadosClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Resultados"
                )
            },
            label = {
                Text("Resultados")
            }
        )

        // Opción Perfil
        NavigationBarItem(
            selected = seccionActual == "Perfil",
            onClick = onPerfilClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text("Perfil")
            }
        )
    }
}
