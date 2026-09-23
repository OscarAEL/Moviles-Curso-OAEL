package com.eneque.lab05tecsupfit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05tecsupfit.ui.DetalleClaseScreen
import com.eneque.lab05tecsupfit.ui.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onClaseClick = { claseId ->
                    navController.navigate(Screen.DetalleClase.createRoute(claseId))
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
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
