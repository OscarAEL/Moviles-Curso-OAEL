package com.eneque.lab05tecsupfit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05tecsupfit.ui.ConfirmacionReservaScreen
import com.eneque.lab05tecsupfit.ui.DetalleClaseScreen
import com.eneque.lab05tecsupfit.ui.HomeScreen
import com.eneque.lab05tecsupfit.ui.PerfilScreen
import com.eneque.lab05tecsupfit.ui.ReservasScreen
import com.eneque.lab05tecsupfit.ui.RutinasScreen
import com.eneque.lab05tecsupfit.ui.components.AppBottomBar

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val onTabSelected: (String) -> Unit = { targetRoute ->
        if (targetRoute != currentRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onClaseClick = { claseId ->
                    navController.navigate(Screen.DetalleClase.createRoute(claseId))
                },
                bottomBar = {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = onTabSelected
                    )
                }
            )
        }

        composable(Screen.Reservas.route) {
            ReservasScreen(
                bottomBar = {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = onTabSelected
                    )
                }
            )
        }

        composable(Screen.Rutinas.route) {
            RutinasScreen(
                bottomBar = {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = onTabSelected
                    )
                }
            )
        }

        composable(Screen.Perfil.route) {
            PerfilScreen(
                bottomBar = {
                    AppBottomBar(
                        currentRoute = currentRoute,
                        onTabSelected = onTabSelected
                    )
                }
            )
        }

        composable(
            route = Screen.DetalleClase.route,
            arguments = listOf(
                navArgument("claseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val claseId = backStackEntry.arguments?.getInt("claseId") ?: 0
            DetalleClaseScreen(
                claseId = claseId,
                onBackClick = { navController.popBackStack() },
                onReservarClick = {
                    navController.navigate(Screen.ConfirmacionReserva.createRoute(claseId))
                }
            )
        }

        composable(
            route = Screen.ConfirmacionReserva.route,
            arguments = listOf(
                navArgument("claseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val claseId = backStackEntry.arguments?.getInt("claseId") ?: 0
            ConfirmacionReservaScreen(
                claseId = claseId,
                onVerReservasClick = {
                    navController.navigate(Screen.Reservas.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
