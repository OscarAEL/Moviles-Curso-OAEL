package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.ui.administrador.PanelProvisionalScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.InicioProvisionalScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

object Destinos {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val PANEL = "panel"
    const val SERVICIOS = "servicios"
}

@Composable
fun AppNavGraph(
    app: LavaApp,
    navController: NavHostController = rememberNavController()
) {
    val sesionViewModel: SesionViewModel = viewModel(
        factory = SesionViewModel.Factory(app)
    )

    val serviciosViewModel: ServiciosViewModel = viewModel(
        factory = ServiciosViewModel.Factory(app.repository)
    )

    val usuarioActual by sesionViewModel.usuarioActual.collectAsState()
    val restaurandoSesion by sesionViewModel.restaurandoSesion.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()

    LaunchedEffect(restaurandoSesion, usuarioActual) {
        if (!restaurandoSesion) {
            val usuario = usuarioActual
            if (usuario == null) {
                val rutaActual = navController.currentDestination?.route
                if (rutaActual == Destinos.INICIO || rutaActual == Destinos.PANEL || rutaActual == Destinos.SERVICIOS) {
                    navController.navigate(Destinos.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val destinoValido = if (usuario.rol == "ADMIN") Destinos.PANEL else Destinos.INICIO
                val rutaActual = navController.currentDestination?.route
                if (rutaActual != destinoValido && rutaActual != Destinos.SERVICIOS) {
                    navController.navigate(destinoValido) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    if (restaurandoSesion) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (usuarioActual != null) {
                if (usuarioActual?.rol == "ADMIN") Destinos.PANEL else Destinos.INICIO
            } else {
                Destinos.SPLASH
            }
        ) {
            composable(Destinos.SPLASH) {
                SplashScreen(
                    onComenzarClick = {
                        navController.navigate(Destinos.LOGIN)
                    }
                )
            }

            composable(Destinos.LOGIN) {
                LoginScreen(
                    sesionViewModel = sesionViewModel
                )
            }

            composable(Destinos.INICIO) {
                val usuario = usuarioActual
                if (usuario != null) {
                    InicioProvisionalScreen(
                        usuario = usuario,
                        cargando = cargando,
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                        }
                    )
                }
            }

            composable(Destinos.PANEL) {
                val usuario = usuarioActual
                if (usuario != null) {
                    PanelProvisionalScreen(
                        usuario = usuario,
                        cargando = cargando,
                        onVerServicios = {
                            navController.navigate(Destinos.SERVICIOS)
                        },
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                        }
                    )
                }
            }

            composable(Destinos.SERVICIOS) {
                ServiciosScreen(
                    viewModel = serviciosViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
