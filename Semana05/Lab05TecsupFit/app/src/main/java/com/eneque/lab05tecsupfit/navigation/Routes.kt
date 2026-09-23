package com.eneque.lab05tecsupfit.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object DetalleClase : Screen("detalle_clase_screen/{claseId}") {
        fun createRoute(claseId: Int) = "detalle_clase_screen/$claseId"
    }
    object ConfirmacionReserva : Screen("confirmacion_reserva_screen/{claseId}") {
        fun createRoute(claseId: Int) = "confirmacion_reserva_screen/$claseId"
    }
}
