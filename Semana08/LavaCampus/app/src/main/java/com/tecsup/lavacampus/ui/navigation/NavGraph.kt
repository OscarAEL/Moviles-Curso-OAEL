package com.tecsup.lavacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel

object Destinos {
    const val SPLASH = "splash"
    const val SERVICIOS = "servicios"
}

@Composable
fun AppNavGraph(
    app: LavaApp,
    navController: NavHostController = rememberNavController()
) {
    val serviciosViewModel: ServiciosViewModel = viewModel(
        factory = ServiciosViewModel.Factory(app.repository)
    )

    NavHost(
        navController = navController,
        startDestination = Destinos.SPLASH
    ) {
        composable(Destinos.SPLASH) {
            SplashScreen(
                onComenzarClick = {
                    navController.navigate(Destinos.SERVICIOS)
                }
            )
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
