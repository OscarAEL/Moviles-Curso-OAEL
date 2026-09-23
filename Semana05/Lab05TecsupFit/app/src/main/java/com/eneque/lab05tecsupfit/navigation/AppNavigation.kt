package com.eneque.lab05tecsupfit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05tecsupfit.model.Reserva
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

    // Lista temporal de reservas guardadas en memoria
    val listaReservas = remember {
        mutableStateListOf(
            Reserva(id = 1, claseId = 1, estado = "Completada") // Yoga funcional - Completada
        )
    }

    val onTabSelected: (String) -> Unit = { targetRoute ->
        if (targetRoute != currentRoute) {
            navController.navigate(targetRoute) {
                popUpTo(Screen.Home.route) {
                    inclusive = false
                }
                launchSingleTop = true
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
                reservas = listaReservas,
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
                    // Evitar duplicar si ya existe la reserva para esta clase
                    val existenteIndex = listaReservas.indexOfFirst { it.claseId == claseId }
                    if (existenteIndex != -1) {
                        listaReservas[existenteIndex] = listaReservas[existenteIndex].copy(estado = "Confirmada")
                    } else {
                        listaReservas.add(
                            Reserva(
                                id = listaReservas.size + 1,
                                claseId = claseId,
                                estado = "Confirmada"
                            )
                        )
                    }

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
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
