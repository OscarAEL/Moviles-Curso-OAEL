package com.tecsup.lavacampus.ui.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.BotonPrincipal
import com.tecsup.lavacampus.ui.components.Cabecera
import com.tecsup.lavacampus.ui.components.LavaScaffold
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun RegistroScreen(sesionViewModel: SesionViewModel, onVolver: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var codigo by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmacion by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var intento by rememberSaveable { mutableStateOf(false) }
    val errorNombre =
        if (nombre.trim().length !in 2..100) "Ingresa tu nombre completo, solo letras y espacios"
        else null
    val errorCodigo =
        if (!Regex("[0-9]{6}").matches(codigo)) "El código debe tener 6 dígitos" else null
    val errorCorreo =
        if (
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() ||
                correo.trim().length > 254
        )
            "Ingresa un correo válido"
        else null
    val errorClave =
        if (
            clave.length !in 8..128 ||
                !clave.any { it.isUpperCase() } ||
                !clave.any { it.isLowerCase() } ||
                !clave.any { it.isDigit() }
        )
            "Mínimo 8 caracteres, con mayúscula, minúscula y número"
        else null
    val errorConfirmacion = if (clave != confirmacion) "Las contraseñas no coinciden" else null
    val cargando by sesionViewModel.cargando.collectAsState()
    val error by sesionViewModel.error.collectAsState()
    val registroCompletado by sesionViewModel.registroCompletado.collectAsState()
    LaunchedEffect(registroCompletado) {
        if (registroCompletado) {
            sesionViewModel.consumirRegistroCompletado()
            onVolver()
        }
    }
    val snackbar = remember { SnackbarHostState() }
    BackHandler(enabled = cargando) {}
    LaunchedEffect(error) {
        error?.let {
            snackbar.showSnackbar(it)
            sesionViewModel.limpiarError()
        }
    }
    LavaScaffold(
        rol = Rol.CLIENTE,
        topBar = { Cabecera("", if (!cargando) onVolver else null) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Crea tu cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Solo para estudiantes del campus",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LavaTextField(
                nombre,
                { nombre = it.filter { c -> c.isLetter() || c == ' ' }.take(100) },
                "Nombre completo",
                enabled = !cargando,
                errorText = if (intento) errorNombre else null,
            )
            LavaTextField(
                codigo,
                { codigo = it.filter { c -> c in '0'..'9' }.take(6) },
                "Código de estudiante",
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                errorText = if (intento) errorCodigo else null,
            )
            Text(
                "Exactamente 6 dígitos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LavaTextField(
                correo,
                { correo = it },
                "Correo",
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                errorText = if (intento) errorCorreo else null,
            )
            LavaTextField(
                clave,
                { clave = it },
                "Contraseña",
                enabled = !cargando,
                isPassword = true,
                passwordVisible = visible,
                onPasswordToggle = { visible = !visible },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                errorText = if (intento) errorClave else null,
            )
            Text(
                "De 8 a 128 caracteres con minúscula, mayúscula y número",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LavaTextField(
                confirmacion,
                { confirmacion = it },
                "Confirmar contraseña",
                enabled = !cargando,
                isPassword = true,
                passwordVisible = visible,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                errorText = if (intento) errorConfirmacion else null,
            )
            if (cargando) {
                CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            } else {
                BotonPrincipal(
                    "Crear cuenta",
                    onClick = {
                        intento = true
                        if (
                            listOf(
                                    errorNombre,
                                    errorCodigo,
                                    errorCorreo,
                                    errorClave,
                                    errorConfirmacion,
                                )
                                .all { it == null }
                        ) {
                            sesionViewModel.registrar(nombre, codigo, correo, clave, confirmacion)
                        }
                    },
                    enabled =
                        nombre.isNotBlank() &&
                            codigo.isNotBlank() &&
                            correo.isNotBlank() &&
                            clave.isNotEmpty() &&
                            confirmacion.isNotEmpty(),
                )
            }
            Text(
                "Al terminar volverás a iniciar sesión",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(
                onClick = onVolver,
                enabled = !cargando,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("Ya tengo cuenta. Ingresar")
            }
        }
    }
}
