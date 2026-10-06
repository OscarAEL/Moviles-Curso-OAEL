package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onLoginClick: () -> Unit,
    onTerminosClick: () -> Unit,
) {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Regístrate para agendar tus citas",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
            },
            label = {
                Text("Nombre completo")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Nombre completo",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                mensajeError = ""
            },
            label = {
                Text("Teléfono")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Teléfono",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                mensajeError = ""
            },
            label = {
                Text("Correo")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Correo electrónico",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                mensajeError = ""
            },
            label = {
                Text("Contraseña")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Contraseña",
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        if (mensajeError.isNotEmpty()) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                if (
                    nombre.isBlank() ||
                    telefono.isBlank() ||
                    correo.isBlank() ||
                    contrasena.isBlank()
                ) {

                    mensajeError = "Completa todos los campos"

                } else if (contrasena.length < 6) {

                    mensajeError = "La contraseña debe tener mínimo 6 caracteres"

                } else {

                    val nuevoId =
                        (Repositorio.usuarios.maxOfOrNull { it.id } ?: 0) + 1

                    val nuevoUsuario = Usuario(
                        id = nuevoId,
                        nombre = nombre,
                        telefono = telefono,
                        correo = correo,
                        contrasena = contrasena,
                    )

                    val registrado =
                        Repositorio.registrarUsuario(nuevoUsuario)

                    if (registrado) {
                        onRegistroExitoso()
                    } else {
                        mensajeError = "El correo ya está registrado"
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = "Registrarme",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Al registrarte aceptas nuestros ",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Términos y condiciones",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onTerminosClick() },
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Iniciar sesión",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onLoginClick() },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
