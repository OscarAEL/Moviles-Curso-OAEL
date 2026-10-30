package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.tecsup.lavacampus.model.*
import com.tecsup.lavacampus.ui.components.*
import com.tecsup.lavacampus.viewmodel.*

@Composable
fun LoginScreen(sesionViewModel: SesionViewModel, onRegistroClick: () -> Unit = {}) {
    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("ana@correo.test") }
    var clave by remember { mutableStateOf("1234") }
    var visible by remember { mutableStateOf(false) }
    val error by sesionViewModel.error.collectAsState()
    val cargando by sesionViewModel.cargando.collectAsState()
    val correoRegistrado by sesionViewModel.correoRegistrado.collectAsState()
    val host = ErrorDePantalla(error, sesionViewModel::limpiarError)
    LaunchedEffect(Unit) {
        correoRegistrado?.let {
            correo = it
            clave = ""
            rol = Rol.CLIENTE
            sesionViewModel.consumirCorreoRegistrado()
            host.showSnackbar("Cuenta creada. Ingresa con tu contraseña")
        }
    }
    LavaScaffold(
        rol = rol,
        modifier = Modifier.imePadding(),
        snackbarHost = { SnackbarHost(host) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(Modifier.height(6.dp))
            Column {
                Text("Bienvenido", fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                Secundario("Ingresa para continuar")
            }
            Row(
                Modifier.fillMaxWidth()
                    .height(52.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(30.dp),
                    )
                    .padding(4.dp)
            ) {
                listOf(Rol.CLIENTE, Rol.ADMIN).forEach { item ->
                    Box(
                        Modifier.weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(26.dp))
                            .background(
                                if (rol == item) MaterialTheme.colorScheme.secondary
                                else Color.Transparent
                            )
                            .clickable(enabled = !cargando) {
                                rol = item
                                if (item == Rol.ADMIN && correo == "ana@correo.test") {
                                    correo = "admin@lavacampus.test"
                                    clave = "admin"
                                } else if (
                                    item == Rol.CLIENTE && correo == "admin@lavacampus.test"
                                ) {
                                    correo = "ana@correo.test"
                                    clave = "1234"
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (item == Rol.CLIENTE) "Estudiante" else "Administrador",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color =
                                if (rol == item) Color.White
                                else MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
            LavaTextField(
                correo,
                { correo = it },
                "Correo",
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )
            LavaTextField(
                clave,
                { clave = it },
                "Contraseña",
                isPassword = true,
                passwordVisible = visible,
                onPasswordToggle = { visible = !visible },
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            Spacer(Modifier.height(4.dp))
            BotonPrincipal(
                if (cargando) "Ingresando…" else "Ingresar",
                { sesionViewModel.login(correo, clave, rol) },
                enabled = !cargando,
            )
            if (rol == Rol.CLIENTE)
                TextButton(
                    onClick = {
                        sesionViewModel.limpiarError()
                        onRegistroClick()
                    },
                    enabled = !cargando,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text("¿No tienes cuenta? Regístrate", fontSize = 13.sp)
                }
        }
    }
}
