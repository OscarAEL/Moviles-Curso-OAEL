package com.saludplus.citas.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH
    ) {

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

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistroExitoso = {
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

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
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

        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onEspecialidadClick = { especialidadId ->
                    navController.navigate(
                        Rutas.medicos(especialidadId)
                    )
                }
            )
        }

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
                    navController.navigate(
                        Rutas.fechaHora(medicoId)
                    )
                }
            )
        }

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
                }
            )
        }

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

                    navController.navigate(
                        Rutas.citaExitosa(citaId)
                    ) {
                        popUpTo(Rutas.ESPECIALIDADES) {
                            inclusive = true
                        }
                    }
                }
            )
        }

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
                    navController.navigate(Rutas.MIS_CITAS)
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
                citaId = citaId
            )
        }

        composable(Rutas.PERFIL) {
            PerfilScreen(
                onCerrarSesionClick = {
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

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen()
        }

        composable(Rutas.TERMINOS) {
            TerminosScreen()
        }
    }
}