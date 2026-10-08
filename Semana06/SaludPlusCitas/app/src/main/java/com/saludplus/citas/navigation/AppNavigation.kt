package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saludplus.citas.ui.screens.agendamiento.CitaExitosaScreen
import com.saludplus.citas.ui.screens.agendamiento.ConfirmarCitaScreen
import com.saludplus.citas.ui.screens.agendamiento.EspecialidadesScreen
import com.saludplus.citas.ui.screens.agendamiento.FechaHoraScreen
import com.saludplus.citas.ui.screens.agendamiento.MedicosScreen
import com.saludplus.citas.ui.screens.auth.LoginScreen
import com.saludplus.citas.ui.screens.auth.RegistroScreen
import com.saludplus.citas.ui.screens.auth.SplashScreen
import com.saludplus.citas.ui.screens.auth.TerminosScreen
import com.saludplus.citas.ui.screens.citas.DetalleCitaScreen
import com.saludplus.citas.ui.screens.citas.MisCitasScreen
import com.saludplus.citas.ui.screens.home.HomeScreen
import com.saludplus.citas.ui.screens.notificaciones.NotificacionesScreen
import com.saludplus.citas.ui.screens.perfil.PerfilScreen
import com.saludplus.citas.ui.screens.resultados.ResultadosScreen

// Composable principal que administra el grafo de navegación de toda la aplicación.
@Composable
fun AppNavigation() {

    // Controlador que gestiona la pila de navegación y el cambio entre pantallas.
    val navController = rememberNavController()

    // Contenedor de rutas; inicia mostrando la pantalla Splash.
    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

        // Pantalla de Bienvenida (Splash)
        composable(Rutas.SPLASH) {
            SplashScreen(
                onRegistroClick = {
                    navController.navigate(Rutas.REGISTRO)
                },
                onLoginClick = {
                    navController.navigate(Rutas.LOGIN)
                }
            )
        }

        // Pantalla de Registro de usuario
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
                    // Limpia la pantalla Splash del back stack al iniciar sesión
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.navigate(Rutas.LOGIN)
                },
                onTerminosClick = {
                    navController.navigate(Rutas.TERMINOS)
                }
            )
        }

        // Pantalla de Iniciar sesión
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    // Remueve Splash del historial al entrar al Home
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.SPLASH) {
                            inclusive = true
                        }
                    }
                },
                onRegistroClick = {
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        // Pantalla Principal (Home)
        composable(Rutas.HOME) {
            HomeScreen(
                onAgendarCitaClick = {
                    navController.navigate(Rutas.ESPECIALIDADES)
                },
                onMisCitasClick = {
                    navController.navigate(Rutas.MIS_CITAS)
                },
                onEspecialidadClick = { especialidadId ->
                    navController.navigate(
                        Rutas.medicos(especialidadId)
                    )
                },
                onInicioClick = {
                    // Ya estamos en Inicio
                },
                onCitasClick = {
                    navController.navigate(Rutas.MIS_CITAS)
                },
                onResultadosClick = {
                    navController.navigate(Rutas.RESULTADOS)
                },
                onPerfilClick = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // Listado de Especialidades Médicas
        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onEspecialidadClick = { especialidadId ->
                    navController.navigate(
                        Rutas.medicos(especialidadId)
                    )
                },
                onAtrasClick = {
                    navController.popBackStack()
                }
            )
        }

        // Listado de Médicos filtrados por especialidadId
        composable(
            route = Rutas.MEDICOS,
            arguments = listOf(
                navArgument("especialidadId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val especialidadId =
                backStackEntry.arguments?.getInt("especialidadId") ?: 0

            MedicosScreen(
                especialidadId = especialidadId,
                onMedicoClick = { medicoId ->
                    // Valida que la pantalla esté activa antes de navegar para evitar doble clic accidental
                    if (backStackEntry.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        navController.navigate(
                            Rutas.fechaHora(medicoId)
                        )
                    }
                },
                onAtrasClick = {
                    navController.popBackStack()
                }
            )
        }

        // Selección de Fecha y Hora con un médico específico
        composable(
            route = Rutas.FECHA_HORA,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val medicoId =
                backStackEntry.arguments?.getInt("medicoId") ?: 0

            FechaHoraScreen(
                medicoId = medicoId,
                onContinuarClick = { fecha, hora ->
                    navController.navigate(
                        Rutas.confirmarCita(
                            medicoId = medicoId,
                            fecha = fecha,
                            hora = hora
                        )
                    )
                },
                onAtrasClick = {
                    navController.popBackStack()
                }
            )
        }

        // Confirmación final antes de agendar la cita
        composable(
            route = Rutas.CONFIRMAR_CITA,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                },
                navArgument("fecha") {
                    type = NavType.StringType
                },
                navArgument("hora") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val medicoId =
                backStackEntry.arguments?.getInt("medicoId") ?: 0

            val fecha =
                backStackEntry.arguments?.getString("fecha") ?: ""

            val hora =
                backStackEntry.arguments?.getString("hora") ?: ""

            ConfirmarCitaScreen(
                medicoId = medicoId,
                fecha = fecha,
                hora = hora,
                onCitaConfirmada = { citaId ->
                    // Remueve las pantallas intermedias del flujo de reserva
                    navController.navigate(
                        Rutas.citaExitosa(citaId)
                    ) {
                        popUpTo(Rutas.ESPECIALIDADES) {
                            inclusive = true
                        }
                    }
                },
                onAtrasClick = {
                    navController.popBackStack()
                }
            )
        }

        // Comprobante de Cita Creada con Éxito
        composable(
            route = Rutas.CITA_EXITOSA,
            arguments = listOf(
                navArgument("citaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val citaId =
                backStackEntry.arguments?.getInt("citaId") ?: 0

            CitaExitosaScreen(
                citaId = citaId,

                onMisCitasClick = {
                    // Remueve la pantalla de confirmación del back stack al ir a Mis Citas
                    navController.navigate(Rutas.MIS_CITAS) {
                        popUpTo(Rutas.CITA_EXITOSA) {
                            inclusive = true
                        }
                    }
                },

                onInicioClick = {
                    navController.navigate(Rutas.HOME) {
                        popUpTo(Rutas.HOME) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        // Historial de Citas del Usuario
        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(
                onCitaClick = { citaId ->
                    navController.navigate(
                        Rutas.detalleCita(citaId)
                    )
                },
                onInicioClick = {
                    navController.navigate(Rutas.HOME)
                },
                onCitasClick = {
                    // Ya estamos en Citas
                },
                onResultadosClick = {
                    navController.navigate(Rutas.RESULTADOS)
                },
                onPerfilClick = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // Detalle completo de una Cita específica
        composable(
            route = Rutas.DETALLE_CITA,
            arguments = listOf(
                navArgument("citaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val citaId =
                backStackEntry.arguments?.getInt("citaId") ?: 0

            DetalleCitaScreen(
                citaId = citaId,
                onAtrasClick = {
                    navController.popBackStack()
                }
            )
        }

        // Perfil del usuario activo
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onCerrarSesionClick = {
                    // Limpia toda la pila de navegación al cerrar sesión
                    navController.navigate(Rutas.SPLASH) {
                        popUpTo(0)
                    }
                },
                onInicioClick = {
                    navController.navigate(Rutas.HOME)
                },
                onCitasClick = {
                    navController.navigate(Rutas.MIS_CITAS)
                },
                onResultadosClick = {
                    navController.navigate(Rutas.RESULTADOS)
                },
                onPerfilClick = {
                    // Ya estamos en Perfil
                }
            )
        }

        // Pantalla de Resultados
        composable(Rutas.RESULTADOS) {
            ResultadosScreen(
                onInicioClick = {
                    navController.navigate(Rutas.HOME)
                },
                onCitasClick = {
                    navController.navigate(Rutas.MIS_CITAS)
                },
                onResultadosClick = {
                    // Ya estamos en Resultados
                },
                onPerfilClick = {
                    navController.navigate(Rutas.PERFIL)
                }
            )
        }

        // Pantallas secundarias estáticas
        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen()
        }

        composable(Rutas.TERMINOS) {
            TerminosScreen()
        }
    }
}
