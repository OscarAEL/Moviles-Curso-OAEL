package com.eneque.lab05clinicasalud.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05clinicasalud.model.Cita
import com.eneque.lab05clinicasalud.ui.AgendarCitaScreen
import com.eneque.lab05clinicasalud.ui.ConfirmacionCitaScreen
import com.eneque.lab05clinicasalud.ui.HistorialMedicoScreen
import com.eneque.lab05clinicasalud.ui.HomeScreen
import com.eneque.lab05clinicasalud.ui.MisCitasScreen
import com.eneque.lab05clinicasalud.ui.PerfilMedicoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Citas almacenadas temporalmente en memoria para la ejecución actual
    val citas = remember {
        mutableStateListOf(
            Cita(
                id = 1,
                medicoId = 2,
                fecha = "Miércoles 15",
                hora = "3:00 pm",
                estado = "Completada"
            )
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onMedicoClick = { medicoId ->
                    navController.navigate(Routes.perfilMedico(medicoId))
                },
                onMisCitasClick = {
                    navController.navigate(Routes.MIS_CITAS) {
                        launchSingleTop = true
                    }
                },
                onHistorialClick = {
                    navController.navigate(Routes.HISTORIAL_MEDICO) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.PERFIL_MEDICO,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            PerfilMedicoScreen(
                medicoId = medicoId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAgendarCitaClick = {
                    navController.navigate(Routes.agendarCita(medicoId))
                }
            )
        }

        composable(
            route = Routes.AGENDAR_CITA,
            arguments = listOf(
                navArgument("medicoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            AgendarCitaScreen(
                medicoId = medicoId,
                onBackClick = {
                    navController.popBackStack()
                },
                onConfirmarClick = { fecha, hora ->
                    val yaExiste = citas.any {
                        it.medicoId == medicoId && it.fecha == fecha && it.hora == hora && it.estado == "Confirmada"
                    }
                    if (!yaExiste) {
                        val nuevaCita = Cita(
                            id = citas.size + 1,
                            medicoId = medicoId,
                            fecha = fecha,
                            hora = hora,
                            estado = "Confirmada"
                        )
                        citas.add(nuevaCita)
                    }
                    navController.navigate(Routes.confirmacionCita(medicoId, fecha, hora))
                }
            )
        }

        composable(
            route = Routes.CONFIRMACION_CITA,
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
            val medicoId = backStackEntry.arguments?.getInt("medicoId") ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            ConfirmacionCitaScreen(
                medicoId = medicoId,
                fecha = fecha,
                hora = hora,
                onVerMisCitasClick = {
                    navController.navigate(Routes.MIS_CITAS) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.MIS_CITAS) {
            MisCitasScreen(
                citas = citas,
                onInicioClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onMisCitasClick = {
                    // Permanecer en la pantalla actual
                },
                onHistorialClick = {
                    navController.navigate(Routes.HISTORIAL_MEDICO) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.HISTORIAL_MEDICO) {
            HistorialMedicoScreen(
                onInicioClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onMisCitasClick = {
                    navController.navigate(Routes.MIS_CITAS) {
                        launchSingleTop = true
                    }
                },
                onHistorialClick = {
                    // Permanecer en la pantalla actual
                }
            )
        }
    }
}
