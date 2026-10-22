package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.ui.administrador.PanelProvisionalScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.PerfilProvisionalScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import com.tecsup.lavacampus.viewmodel.TarifasViewModel

object Destinos {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val PANEL = "panel"
    const val SERVICIOS = "servicios"
    const val NUEVO_PEDIDO = "nuevo_pedido"
    const val MIS_PEDIDOS = "mis_pedidos"
    const val TARIFAS = "tarifas"
    const val DETALLE_PEDIDO = "detalle_pedido/{id}"
    const val PERFIL = "perfil"

    fun detallePedido(id: Int) = "detalle_pedido/$id"
}

@Composable
fun AppNavGraph(
    app: LavaApp,
    navController: NavHostController = rememberNavController()
) {
    val sesionViewModel: SesionViewModel = viewModel(
        factory = SesionViewModel.Factory(app)
    )

    val serviciosViewModel: ServiciosViewModel = viewModel(
        factory = ServiciosViewModel.Factory(app.repository)
    )

    val usuarioActual by sesionViewModel.usuarioActual.collectAsState()
    val restaurandoSesion by sesionViewModel.restaurandoSesion.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()

    LaunchedEffect(restaurandoSesion, usuarioActual) {
        if (!restaurandoSesion) {
            val usuario = usuarioActual
            if (usuario == null) {
                val rutaActual = navController.currentDestination?.route
                if (rutaActual != Destinos.SPLASH && rutaActual != Destinos.LOGIN) {
                    navController.navigate(Destinos.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            } else {
                val destinoValido = if (usuario.rol == "ADMIN") Destinos.PANEL else Destinos.INICIO
                val rutaActual = navController.currentDestination?.route
                if (rutaActual == Destinos.SPLASH || rutaActual == Destinos.LOGIN) {
                    navController.navigate(destinoValido) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    if (restaurandoSesion) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (usuarioActual != null) {
                if (usuarioActual?.rol == "ADMIN") Destinos.PANEL else Destinos.INICIO
            } else {
                Destinos.SPLASH
            }
        ) {
            composable(Destinos.SPLASH) {
                SplashScreen(
                    onComenzarClick = {
                        navController.navigate(Destinos.LOGIN)
                    }
                )
            }

            composable(Destinos.LOGIN) {
                LoginScreen(
                    sesionViewModel = sesionViewModel
                )
            }

            composable(Destinos.INICIO) {
                val usuario = usuarioActual
                if (usuario != null) {
                    val inicioViewModel: InicioViewModel = viewModel(
                        key = "inicio_${usuario.id}",
                        factory = InicioViewModel.Factory(app.repository, usuario)
                    )

                    InicioScreen(
                        viewModel = inicioViewModel,
                        onNuevoPedidoClick = {
                            navController.navigate(Destinos.NUEVO_PEDIDO) {
                                launchSingleTop = true
                            }
                        },
                        onMisPedidosClick = {
                            navController.navigate(Destinos.MIS_PEDIDOS) {
                                launchSingleTop = true
                            }
                        },
                        onTarifasClick = {
                            navController.navigate(Destinos.TARIFAS) {
                                launchSingleTop = true
                            }
                        },
                        onPerfilClick = {
                            navController.navigate(Destinos.PERFIL) {
                                launchSingleTop = true
                            }
                        },
                        onPedidoClick = { id ->
                            navController.navigate(Destinos.detallePedido(id)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(Destinos.PANEL) {
                val usuario = usuarioActual
                if (usuario != null) {
                    PanelProvisionalScreen(
                        usuario = usuario,
                        cargando = cargando,
                        onVerServicios = {
                            navController.navigate(Destinos.SERVICIOS)
                        },
                        onCerrarSesion = {
                            sesionViewModel.cerrarSesion()
                        }
                    )
                }
            }

            composable(Destinos.SERVICIOS) {
                ServiciosScreen(
                    viewModel = serviciosViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // Destinos del flujo del estudiante
            composable(Destinos.NUEVO_PEDIDO) {
                val usuario = usuarioActual
                if (usuario != null) {
                    val nuevoPedidoViewModel: NuevoPedidoViewModel = viewModel(
                        key = "nuevo_pedido_${usuario.id}",
                        factory = NuevoPedidoViewModel.Factory(app.repository, app.preferenciasStore, usuario)
                    )

                    NuevoPedidoScreen(
                        viewModel = nuevoPedidoViewModel,
                        onBackClick = { navController.popBackStack() },
                        onPedidoCreadoExito = {
                            navController.navigate(Destinos.MIS_PEDIDOS) {
                                popUpTo(Destinos.NUEVO_PEDIDO) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable(Destinos.MIS_PEDIDOS) {
                val usuario = usuarioActual
                if (usuario != null) {
                    val misPedidosViewModel: MisPedidosViewModel = viewModel(
                        key = "mis_pedidos_${usuario.id}",
                        factory = MisPedidosViewModel.Factory(app.repository, usuario.id)
                    )

                    MisPedidosScreen(
                        viewModel = misPedidosViewModel,
                        onBackClick = { navController.popBackStack() },
                        onNuevoPedidoClick = {
                            navController.navigate(Destinos.NUEVO_PEDIDO) {
                                launchSingleTop = true
                            }
                        },
                        onInicioClick = {
                            navController.navigate(Destinos.INICIO) {
                                popUpTo(Destinos.INICIO) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onTarifasClick = {
                            navController.navigate(Destinos.TARIFAS) {
                                popUpTo(Destinos.INICIO) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onPerfilClick = {
                            navController.navigate(Destinos.PERFIL) {
                                popUpTo(Destinos.INICIO) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onPedidoClick = { id ->
                            navController.navigate(Destinos.detallePedido(id)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(Destinos.TARIFAS) {
                val usuario = usuarioActual
                if (usuario != null) {
                    val tarifasViewModel: TarifasViewModel = viewModel(
                        key = "tarifas_${usuario.id}",
                        factory = TarifasViewModel.Factory(app.repository)
                    )

                    TarifasScreen(
                        viewModel = tarifasViewModel,
                        onBackClick = { navController.popBackStack() },
                        onInicioClick = {
                            navController.navigate(Destinos.INICIO) {
                                popUpTo(Destinos.INICIO) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onPedidosClick = {
                            navController.navigate(Destinos.MIS_PEDIDOS) {
                                popUpTo(Destinos.INICIO) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        onPerfilClick = {
                            navController.navigate(Destinos.PERFIL) {
                                popUpTo(Destinos.INICIO) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(
                route = Destinos.DETALLE_PEDIDO,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val usuario = usuarioActual
                if (usuario != null) {
                    val detalleViewModel: DetallePedidoViewModel = viewModel(
                        key = "detalle_${id}_${usuario.id}",
                        factory = DetallePedidoViewModel.Factory(app.repository, id, usuario.id)
                    )

                    DetallePedidoScreen(
                        viewModel = detalleViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable(Destinos.PERFIL) {
                val usuario = usuarioActual
                if (usuario != null) {
                    PerfilProvisionalScreen(
                        usuario = usuario,
                        cargando = cargando,
                        onBackClick = { navController.popBackStack() },
                        onCerrarSesion = { sesionViewModel.cerrarSesion() }
                    )
                }
            }
        }
    }
}
