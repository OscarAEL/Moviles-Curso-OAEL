package com.eneque.lab04carritotecsup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    val scope = rememberCoroutineScope()

    var seccionActual by remember { mutableStateOf("Inicio") }

    val productos = remember { mutableStateListOf<Producto>() }
    val favoritos = remember { mutableStateListOf<Producto>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                seccionActual = seccionActual,
                cantidadFavoritos = favoritos.size,
                onSeccionSeleccionada = { nuevaSeccion ->
                    seccionActual = nuevaSeccion
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Text(
                            text = "Oscar Eneque",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->

            when (seccionActual) {

                "Inicio" -> {
                    PantallaCarrito(
                        productos = productos,
                        onAgregarProducto = { nuevoProducto ->
                            productos.add(nuevoProducto)
                        },
                        onEliminarProducto = { producto ->
                            productos.remove(producto)
                        },
                        onAgregarFavorito = { producto ->
                            if (!favoritos.contains(producto)) {
                                favoritos.add(producto)
                            }
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                "Mis pedidos" -> {
                    PantallaSeccion(
                        titulo = "Mis pedidos",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                "Favoritos" -> {
                    PantallaSeccion(
                        titulo = "Favoritos",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                "Perfil" -> {
                    PantallaSeccion(
                        titulo = "Perfil",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
