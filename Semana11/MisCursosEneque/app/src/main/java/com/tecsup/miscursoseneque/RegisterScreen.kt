package com.tecsup.miscursoseneque

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HeaderGradientBrush)
    ) {
        // Background canvas graphics (concentric rings & dots)
        HeaderBackgroundDecoration(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        )

        // Main screen content with system bar padding handling
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                TopHeaderRow()

                Spacer(modifier = Modifier.height(16.dp))

                // Title and Subtitle in Header
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Crear cuenta",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Comienza tu experiencia académica",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // White Panel containing form & footer
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email input field
                    PremiumInputField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = ""
                        },
                        label = "Correo electrónico",
                        placeholder = "nombre@correo.com",
                        trailingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password input field
                    PremiumInputField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = ""
                        },
                        label = "Contraseña",
                        placeholder = "Mínimo 6 caracteres",
                        trailingIcon = Icons.Default.Lock,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirm Password input field
                    PremiumInputField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = ""
                        },
                        label = "Confirmar contraseña",
                        placeholder = "Repite tu contraseña",
                        trailingIcon = Icons.Default.Lock,
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Action Button
                    GradientButton(
                        text = "Registrarse",
                        isLoading = isLoading,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = {
                            val cleanEmail = email.trim()
                            val cleanPassword = password.trim()
                            val cleanConfirm = confirmPassword.trim()

                            if (cleanEmail.isEmpty() || cleanPassword.isEmpty() || cleanConfirm.isEmpty()) {
                                errorMessage = "Todos los campos son obligatorios"
                                return@GradientButton
                            }
                            if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                                errorMessage = "Ingrese un correo electrónico válido"
                                return@GradientButton
                            }
                            if (cleanPassword.length < 6) {
                                errorMessage = "La contraseña debe tener al menos 6 caracteres"
                                return@GradientButton
                            }
                            if (cleanPassword != cleanConfirm) {
                                errorMessage = "Las contraseñas no coinciden"
                                return@GradientButton
                            }

                            isLoading = true
                            errorMessage = ""
                            auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Error al registrar usuario"
                                    }
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Navigation Link back to Login
                    Row(
                        modifier = Modifier.clickable { onNavigateToLogin() },
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF3A2FB0),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("← ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFF3A2FB0),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append("Volver al login")
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f, fill = false).heightIn(min = 36.dp))

                    // Footer text at bottom
                    AppFooter()
                }
            }
        }
    }
}
