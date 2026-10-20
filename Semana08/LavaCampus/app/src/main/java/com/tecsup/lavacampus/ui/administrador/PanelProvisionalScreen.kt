package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BotonPrincipal
import com.tecsup.lavacampus.ui.components.LavaScaffold

@Composable
fun PanelProvisionalScreen(
    usuario: Usuario,
    cargando: Boolean = false,
    onVerServicios: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    LavaScaffold(rol = Rol.ADMIN) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Panel (Administrador)",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hola, ${usuario.nombre}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onVerServicios,
                enabled = !cargando,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Ver Servicios y tarifas")
            }
            Spacer(modifier = Modifier.height(16.dp))
            BotonPrincipal(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                enabled = !cargando
            )
        }
    }
}
