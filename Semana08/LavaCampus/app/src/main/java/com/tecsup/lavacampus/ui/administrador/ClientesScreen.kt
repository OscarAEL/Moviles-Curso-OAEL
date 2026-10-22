package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BarraNavegacionAdmin
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.TabAdmin
import com.tecsup.lavacampus.viewmodel.ClientesViewModel
import java.util.Locale

@Composable
fun ClientesScreen(
    viewModel: ClientesViewModel,
    onBackClick: () -> Unit = {},
    onPanelClick: () -> Unit = {},
    onPedidosClick: () -> Unit = {},
    onServiciosClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {}
) {
    val busqueda by viewModel.busqueda.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.limpiarError()
        }
    }

    LavaScaffold(
        rol = Rol.ADMIN,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "Clientes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = {
            BarraNavegacionAdmin(
                tabSeleccionada = TabAdmin.CLIENTES,
                onPanelClick = onPanelClick,
                onPedidosClick = onPedidosClick,
                onServiciosClick = onServiciosClick,
                onClientesClick = { },
                onAjustesClick = onAjustesClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                LavaTextField(
                    value = busqueda,
                    onValueChange = viewModel::actualizarBusqueda,
                    label = "Buscar estudiante",
                    leadingIcon = Icons.Default.Search
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clients List
                if (cargando && clientes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (clientes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron clientes",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = clientes,
                            key = { it.id }
                        ) { cliente ->
                            TarjetaClienteItem(cliente = cliente)
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaClienteItem(cliente: ClienteResumen) {
    val totalGastadoStr = String.format(Locale.US, "%.2f", cliente.totalGastado)
    val iniciales = cliente.nombre.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = Color(0xFFF1F0F5),
                shape = RoundedCornerShape(20.dp)
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Initials Avatar Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciales,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = cliente.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (cliente.codigo.isNotEmpty()) cliente.codigo else cliente.correo,
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cliente.cantidadPedidos} pedidos",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "S/ $totalGastadoStr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
