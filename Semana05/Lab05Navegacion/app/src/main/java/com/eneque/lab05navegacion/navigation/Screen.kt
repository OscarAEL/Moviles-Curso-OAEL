package com.eneque.lab05navegacion.navigation

sealed class Screen(val route: String) {

    // Pantalla de Inicio de Sesión
    object Login : Screen("login")

    // Pantalla Principal / Bienvenida
    object Home : Screen("home")

    // Directorio de Alumnos
    object List : Screen("list")

    // Mi Perfil Académico
    object Profile : Screen("profile")

    // Expediente Académico del alumno seleccionado
    object Detail : Screen("detail/{itemId}") {

        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}
