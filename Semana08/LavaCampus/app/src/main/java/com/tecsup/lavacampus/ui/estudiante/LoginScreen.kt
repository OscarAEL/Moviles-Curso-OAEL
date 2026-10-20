package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BotonPrincipal
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    sesionViewModel: SesionViewModel
) {
    var rolSeleccionado by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("ana@correo.test") }
    var clave by remember { mutableStateOf("1234") }
    var passwordVisible by remember { mutableStateOf(false) }

    val error by sesionViewModel.error.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            sesionViewModel.limpiarError()
        }
    }

    LavaScaffold(
        rol = rolSeleccionado,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Title & Subtitle
                Text(
                    text = "Bienvenido",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Ingresa para continuar",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Role Selector Switch
                SelectorRol(
                    rolSeleccionado = rolSeleccionado,
                    onRolChanged = { nuevoRol ->
                        rolSeleccionado = nuevoRol
                        if (nuevoRol == Rol.ADMIN && correo == "ana@correo.test") {
                            correo = "admin@lavacampus.test"
                            clave = "admin"
                        } else if (nuevoRol == Rol.CLIENTE && correo == "admin@lavacampus.test") {
                            correo = "ana@correo.test"
                            clave = "1234"
                        }
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Email Input
                LavaTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Correo",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !cargando
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                LavaTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = "Contraseña",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordToggle = { passwordVisible = !passwordVisible },
                    enabled = !cargando
                )

                // "¿Olvidaste tu contraseña?" Link
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = { /* Decorativo */ }) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                if (cargando) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    BotonPrincipal(
                        texto = "Ingresar",
                        onClick = {
                            sesionViewModel.login(correo, clave, rolSeleccionado)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // "¿No tienes cuenta? Regístrate" Link
                TextButton(onClick = { /* Decorativo */ }) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Modo Práctica Box (Visible ONLY when ADMIN is selected)
                if (rolSeleccionado == Rol.ADMIN) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            Text(
                                text = "Modo práctica",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SelectorRol(
    rolSeleccionado: Rol,
    onRolChanged: (Rol) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFF3F4F6))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(21.dp))
                    .background(if (rolSeleccionado == Rol.CLIENTE) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onRolChanged(Rol.CLIENTE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Estudiante",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rolSeleccionado == Rol.CLIENTE) MaterialTheme.colorScheme.onPrimary else Color(0xFF6B7280)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(21.dp))
                    .background(if (rolSeleccionado == Rol.ADMIN) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { onRolChanged(Rol.ADMIN) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Administrador",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rolSeleccionado == Rol.ADMIN) MaterialTheme.colorScheme.onPrimary else Color(0xFF6B7280)
                )
            }
        }
    }
}
