package com.eneque.lab05clinicasalud.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05clinicasalud.ui.AgendarCitaScreen
import com.eneque.lab05clinicasalud.ui.HomeScreen
import com.eneque.lab05clinicasalud.ui.PerfilMedicoScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onMedicoClick = { medicoId ->
                    navController.navigate(Routes.perfilMedico(medicoId))
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
                onConfirmarClick = {
                    // Preparado para navegar a la pantalla de confirmación final en el siguiente avance
                }
            )
        }
    }
}
