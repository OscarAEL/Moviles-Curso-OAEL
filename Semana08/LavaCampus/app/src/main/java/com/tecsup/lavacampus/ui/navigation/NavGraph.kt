package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.ui.theme.LocalModoOscuro
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.ui.components.PerfilAjustesScreen
import com.tecsup.lavacampus.ui.components.BarraNavegacionEstudiante
import com.tecsup.lavacampus.ui.components.TabEstudiante
import com.tecsup.lavacampus.ui.administrador.ClientesScreen
import com.tecsup.lavacampus.ui.administrador.FormularioServicioScreen
import com.tecsup.lavacampus.viewmodel.ClientesViewModel
import com.tecsup.lavacampus.viewmodel.PreferenciasViewModel
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.administrador.BandejaScreen
import com.tecsup.lavacampus.ui.administrador.GestionPedidoScreen
import com.tecsup.lavacampus.ui.administrador.PanelScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.components.BarraNavegacionAdmin
import com.tecsup.lavacampus.ui.components.TabAdmin
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import com.tecsup.lavacampus.viewmodel.TarifasViewModel

object Destinos {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val INICIO = "inicio"
    const val PANEL = "panel"
    const val SERVICIOS = "servicios"
    const val NUEVO_SERVICIO = "servicio_nuevo"
    const val EDITAR_SERVICIO = "servicio_editar/{id}"
    const val PEDIDOS_ADMIN = "pedidos_admin?filtro={filtro}"
    const val GESTION_PEDIDO_ADMIN = "gestion_pedido_admin/{id}"
    const val CLIENTES_ADMIN = "clientes_admin"
    const val AJUSTES_ADMIN = "ajustes_admin"
    const val NUEVO_PEDIDO = "nuevo_pedido?repetir={repetir}"
    fun nuevoPedido(repetir: Int? = null) = if (repetir == null) "nuevo_pedido" else "nuevo_pedido?repetir=$repetir"
    const val MIS_PEDIDOS = "mis_pedidos"
    const val TARIFAS = "tarifas"
    const val DETALLE_PEDIDO = "detalle_pedido/{id}"
    const val PERFIL = "perfil"

    fun detallePedido(id: Int) = "detalle_pedido/$id"
    fun pedidosAdmin(filtro: String? = null) =
        if (!filtro.isNullOrEmpty()) "pedidos_admin?filtro=$filtro" else "pedidos_admin"
    fun gestionPedidoAdmin(id: Int) = "gestion_pedido_admin/$id"
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

    val oscuro by sesionViewModel.modoOscuro.collectAsState()
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActualObservada = entrada?.destination?.route

    LaunchedEffect(restaurandoSesion, usuarioActual, rutaActualObservada) {
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
                val rutasAdmin = setOf(Destinos.PANEL, Destinos.SERVICIOS, Destinos.PEDIDOS_ADMIN, Destinos.GESTION_PEDIDO_ADMIN, Destinos.CLIENTES_ADMIN, Destinos.AJUSTES_ADMIN, Destinos.NUEVO_SERVICIO, Destinos.EDITAR_SERVICIO)
                val rolIncorrecto = rutaActual != null && rutaActual != Destinos.SPLASH && rutaActual != Destinos.LOGIN && ((rutaActual in rutasAdmin) != (usuario.rol == "ADMIN"))
                if (rutaActual == Destinos.SPLASH || rutaActual == Destinos.LOGIN || rolIncorrecto) {
                    navController.navigate(destinoValido) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    CompositionLocalProvider(LocalModoOscuro provides oscuro) {
        LavaTheme(rol = if (usuarioActual?.rol == "ADMIN") Rol.ADMIN else Rol.CLIENTE) {
            if (restaurandoSesion) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val destinoInicial = remember { if (usuarioActual?.rol == "ADMIN") Destinos.PANEL else if (usuarioActual != null) Destinos.INICIO else Destinos.SPLASH }
                NavHost(
                    navController = navController,
                    startDestination = destinoInicial
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
                                    navController.navigate(Destinos.nuevoPedido()) {
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

                    // Flujo del Administrador
                    composable(Destinos.PANEL) {
                        val usuario = usuarioActual
                        if (usuario != null) {
                            val panelViewModel: PanelViewModel = viewModel(
                                key = "panel_${usuario.id}",
                                factory = PanelViewModel.Factory(app.repository)
                            )

                            PanelScreen(
                                viewModel = panelViewModel,
                                onVerPedidosListo = {
                                    navController.navigate(Destinos.pedidosAdmin("LISTO")) {
                                        launchSingleTop = true
                                    }
                                },
                                onPedidosClick = {
                                    navController.navigate(Destinos.pedidosAdmin()) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onServiciosClick = {
                                    navController.navigate(Destinos.SERVICIOS) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onClientesClick = {
                                    navController.navigate(Destinos.CLIENTES_ADMIN) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onAjustesClick = {
                                    navController.navigate(Destinos.AJUSTES_ADMIN) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }

                    composable(Destinos.SERVICIOS) {
                        ServiciosScreen(
                            viewModel = serviciosViewModel,
                            onNuevoServicio = { navController.navigate(Destinos.NUEVO_SERVICIO) },
                            onEditarServicio = { navController.navigate("servicio_editar/$it") },
                            onBackClick = {
                                navController.popBackStack()
                            },
                            onPanelClick = {
                                navController.navigate(Destinos.PANEL) {
                                    popUpTo(Destinos.PANEL) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onPedidosClick = {
                                navController.navigate(Destinos.pedidosAdmin()) {
                                    popUpTo(Destinos.PANEL) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onClientesClick = {
                                navController.navigate(Destinos.CLIENTES_ADMIN) {
                                    popUpTo(Destinos.PANEL) { inclusive = false }
                                    launchSingleTop = true
                                }
                            },
                            onAjustesClick = {
                                navController.navigate(Destinos.AJUSTES_ADMIN) {
                                    popUpTo(Destinos.PANEL) { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(
                        route = Destinos.PEDIDOS_ADMIN,
                        arguments = listOf(
                            navArgument("filtro") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val filtro = backStackEntry.arguments?.getString("filtro")
                        val usuario = usuarioActual
                        if (usuario != null) {
                            val bandejaViewModel: BandejaViewModel = viewModel(
                                key = "bandeja_${usuario.id}_${filtro ?: "todos"}",
                                factory = BandejaViewModel.Factory(app.repository, filtro)
                            )

                            BandejaScreen(
                                viewModel = bandejaViewModel,
                                filtroInicialStr = filtro,
                                onBackClick = { navController.popBackStack() },
                                onGestionarPedido = { id ->
                                    navController.navigate(Destinos.gestionPedidoAdmin(id)) {
                                        launchSingleTop = true
                                    }
                                },
                                onPanelClick = {
                                    navController.navigate(Destinos.PANEL) {
                                        popUpTo(Destinos.PANEL) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onServiciosClick = {
                                    navController.navigate(Destinos.SERVICIOS) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onClientesClick = {
                                    navController.navigate(Destinos.CLIENTES_ADMIN) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                },
                                onAjustesClick = {
                                    navController.navigate(Destinos.AJUSTES_ADMIN) {
                                        popUpTo(Destinos.PANEL) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }

                    composable(
                        route = Destinos.GESTION_PEDIDO_ADMIN,
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        val usuario = usuarioActual
                        if (usuario != null) {
                            val gestionViewModel: GestionPedidoViewModel = viewModel(
                                key = "gestion_${id}_${usuario.id}",
                                factory = GestionPedidoViewModel.Factory(app.repository, id)
                            )

                            GestionPedidoScreen(
                                viewModel = gestionViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }

                    composable(Destinos.NUEVO_SERVICIO) {
                        val vm: ServiciosViewModel = viewModel(factory = ServiciosViewModel.Factory(app.repository))
                        FormularioServicioScreen(vm, onBackClick = { navController.popBackStack() }, onGuardadoExito = { navController.popBackStack() })
                    }
                    composable(Destinos.EDITAR_SERVICIO, arguments = listOf(navArgument("id") { type = NavType.IntType })) { entry ->
                        val vm: ServiciosViewModel = viewModel(factory = ServiciosViewModel.Factory(app.repository))
                        FormularioServicioScreen(vm, servicioId = entry.arguments?.getInt("id"), onBackClick = { navController.popBackStack() }, onGuardadoExito = { navController.popBackStack() })
                    }
                    composable(Destinos.CLIENTES_ADMIN) {
                        val vm: ClientesViewModel = viewModel(factory = ClientesViewModel.Factory(app.repository))
                        ClientesScreen(vm,
                            onBackClick = { navController.popBackStack() },
                            onPanelClick = { navController.navigate(Destinos.PANEL) { popUpTo(Destinos.PANEL) { inclusive = true }; launchSingleTop = true } },
                            onPedidosClick = { navController.navigate(Destinos.pedidosAdmin()) { popUpTo(Destinos.PANEL); launchSingleTop = true } },
                            onServiciosClick = { navController.navigate(Destinos.SERVICIOS) { popUpTo(Destinos.PANEL); launchSingleTop = true } },
                            onAjustesClick = { navController.navigate(Destinos.AJUSTES_ADMIN) { popUpTo(Destinos.PANEL); launchSingleTop = true } }
                        )
                    }

                    composable(Destinos.AJUSTES_ADMIN) {
                        val usuario = usuarioActual
                        if (usuario != null) {
                            val preferencias: PreferenciasViewModel = viewModel(key = "preferencias_${usuario.id}", factory = PreferenciasViewModel.Factory(app.repository, app.preferenciasStore, usuario.id))
                            PerfilAjustesScreen(usuario, preferencias, sesionViewModel,
                                onBackClick = { navController.popBackStack() },
                                bottomBar = {
                                    BarraNavegacionAdmin(TabAdmin.AJUSTES,
                                        onPanelClick = { navController.navigate(Destinos.PANEL) { popUpTo(Destinos.PANEL) { inclusive = true }; launchSingleTop = true } },
                                        onPedidosClick = { navController.navigate(Destinos.pedidosAdmin()) { popUpTo(Destinos.PANEL); launchSingleTop = true } },
                                        onServiciosClick = { navController.navigate(Destinos.SERVICIOS) { popUpTo(Destinos.PANEL); launchSingleTop = true } },
                                        onClientesClick = { navController.navigate(Destinos.CLIENTES_ADMIN) { popUpTo(Destinos.PANEL); launchSingleTop = true } },
                                        onAjustesClick = {})
                                }
                            )
                        }
                    }

                    // Destinos del flujo del estudiante
                    composable(Destinos.NUEVO_PEDIDO, arguments = listOf(navArgument("repetir") { type = NavType.IntType; defaultValue = 0 })) { entry ->
                        val repetirId = entry.arguments?.getInt("repetir")?.takeIf { it > 0 }
                        val usuario = usuarioActual
                        if (usuario != null) {
                            val nuevoPedidoViewModel: NuevoPedidoViewModel = viewModel(
                                key = "nuevo_pedido_${usuario.id}_${repetirId ?: 0}",
                                factory = NuevoPedidoViewModel.Factory(app.repository, app.preferenciasStore, usuario, repetirId)
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
                                onRepetirPedido = { id -> navController.navigate(Destinos.nuevoPedido(id)) { launchSingleTop = true } },
                                onBackClick = { navController.popBackStack() },
                                onNuevoPedidoClick = {
                                    navController.navigate(Destinos.nuevoPedido()) {
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
                            val preferencias: PreferenciasViewModel = viewModel(key = "preferencias_${usuario.id}", factory = PreferenciasViewModel.Factory(app.repository, app.preferenciasStore, usuario.id))
                            PerfilAjustesScreen(usuario, preferencias, sesionViewModel,
                                onBackClick = { navController.popBackStack() },
                                bottomBar = {
                                    BarraNavegacionEstudiante(TabEstudiante.PERFIL,
                                        onInicioClick = { navController.navigate(Destinos.INICIO) { popUpTo(Destinos.INICIO) { inclusive = true }; launchSingleTop = true } },
                                        onPedidosClick = { navController.navigate(Destinos.MIS_PEDIDOS) { popUpTo(Destinos.INICIO); launchSingleTop = true } },
                                        onTarifasClick = { navController.navigate(Destinos.TARIFAS) { popUpTo(Destinos.INICIO); launchSingleTop = true } },
                                        onPerfilClick = {})
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
