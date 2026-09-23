package com.eneque.lab05navegacion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eneque.lab05navegacion.screens.DetailScreen
import com.eneque.lab05navegacion.screens.HomeScreen
import com.eneque.lab05navegacion.screens.ListScreen
import com.eneque.lab05navegacion.screens.LoginScreen
import com.eneque.lab05navegacion.screens.ProfileScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // 1. Pantalla de Inicio de Sesión
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        // 2. Pantalla Principal / Bienvenida
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // 3. Directorio de Alumnos
        composable(Screen.List.route) {
            ListScreen(navController)
        }

        // 4. Mi Perfil Académico
        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }

        // 5. Expediente Académico del alumno seleccionado
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->

            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0

            DetailScreen(
                navController = navController,
                itemId = itemId
            )
        }
    }
}
