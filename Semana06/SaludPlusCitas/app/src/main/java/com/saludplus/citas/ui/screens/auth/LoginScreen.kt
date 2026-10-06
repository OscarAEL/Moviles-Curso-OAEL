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
import com.saludplus.citas.data.repository.Repositorio

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onRegistroClick: () -> Unit
) {

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
            text = "Iniciar sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ingresa a tu cuenta de SaludPlus",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

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
                    correo.isBlank() ||
                    contrasena.isBlank()
                ) {

                    mensajeError = "Completa todos los campos"

                } else {

                    val loginCorrecto =
                        Repositorio.iniciarSesion(
                            correo = correo,
                            contrasena = contrasena
                        )

                    if (loginCorrecto) {
                        onLoginExitoso()
                    } else {
                        mensajeError = "Correo o contraseña incorrectos"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onRegistroClick
        ) {
            Text("¿No tienes una cuenta? Regístrate")
        }
    }
}