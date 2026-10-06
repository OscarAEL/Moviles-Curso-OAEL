package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onLoginClick: () -> Unit,
    onTerminosClick: () -> Unit
) {

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var mensajeError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Regístrate para agendar tus citas",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
            },
            label = {
                Text("Nombre completo")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                mensajeError = ""
            },
            label = {
                Text("Teléfono")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                mensajeError = ""
            },
            label = {
                Text("Correo")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                mensajeError = ""
            },
            label = {
                Text("Contraseña")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (mensajeError.isNotEmpty()) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mensajeError
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
                        contrasena = contrasena
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onLoginClick
        ) {
            Text("¿Ya tienes una cuenta? Iniciar sesión")
        }

        TextButton(
            onClick = onTerminosClick
        ) {
            Text("Términos y condiciones")
        }
    }
}