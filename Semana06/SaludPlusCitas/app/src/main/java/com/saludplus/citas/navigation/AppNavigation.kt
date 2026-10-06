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
            SplashScreen()
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen()
        }

        composable(Rutas.LOGIN) {
            LoginScreen()
        }

        composable(Rutas.HOME) {
            HomeScreen()
        }

        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen()
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
                especialidadId = especialidadId
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
                medicoId = medicoId
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
                hora = hora
            )
        }

        composable(Rutas.CITA_EXITOSA) {
            CitaExitosaScreen()
        }

        composable(Rutas.MIS_CITAS) {
            MisCitasScreen()
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
            PerfilScreen()
        }

        composable(Rutas.RESULTADOS) {
            ResultadosScreen()
        }

        composable(Rutas.NOTIFICACIONES) {
            NotificacionesScreen()
        }

        composable(Rutas.TERMINOS) {
            TerminosScreen()
        }
    }
}