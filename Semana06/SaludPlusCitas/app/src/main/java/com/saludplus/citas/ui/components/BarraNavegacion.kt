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

@Composable
fun BarraNavegacion(
    seccionActual: String,
    onInicioClick: () -> Unit,
    onCitasClick: () -> Unit,
    onResultadosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {

    NavigationBar {

        NavigationBarItem(
            selected = seccionActual == "Inicio",
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