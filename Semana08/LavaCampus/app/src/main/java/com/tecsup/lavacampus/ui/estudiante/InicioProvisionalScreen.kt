package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun InicioProvisionalScreen(
    usuario: Usuario,
    cargando: Boolean = false,
    onCerrarSesion: () -> Unit
) {
    LavaScaffold(rol = Rol.CLIENTE) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Inicio (Estudiante)",
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
            BotonPrincipal(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                enabled = !cargando
            )
        }
    }
}
