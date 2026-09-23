package com.eneque.lab05navegacion.navigation

sealed class Screen(val route: String) {

    // Pantalla de inicio
    object Home : Screen("home")

    // Pantalla de lista
    object List : Screen("list")

    // Pantalla de perfil
    object Profile : Screen("profile")

    // Pantalla de detalle con argumento
    object Detail : Screen("detail/{itemId}") {

        fun createRoute(itemId: Int): String = "detail/$itemId"
    }
}