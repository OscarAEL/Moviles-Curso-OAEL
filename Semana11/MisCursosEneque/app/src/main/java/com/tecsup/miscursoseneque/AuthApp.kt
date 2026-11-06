package com.tecsup.miscursoseneque

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tecsup.miscursoseneque.ui.theme.MisCursosEnequeTheme

sealed class AuthScreen {
    object Login : AuthScreen()
    object Register : AuthScreen()
}

@Composable
fun AuthApp() {
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf<FirebaseUser?>(auth.currentUser) }
    var currentScreen by remember { mutableStateOf<AuthScreen>(AuthScreen.Login) }

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    MisCursosEnequeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            val user = currentUser
            if (user != null) {
                HomeScreen(
                    userEmail = user.email ?: "",
                    onSignOut = {
                        auth.signOut()
                        currentScreen = AuthScreen.Login
                    }
                )
            } else {
                when (currentScreen) {
                    AuthScreen.Login -> LoginScreen(
                        onLoginSuccess = {
                            // currentUser se actualizará mediante AuthStateListener
                        },
                        onNavigateToRegister = {
                            currentScreen = AuthScreen.Register
                        }
                    )
                    AuthScreen.Register -> RegisterScreen(
                        onRegisterSuccess = {
                            // currentUser se actualizará mediante AuthStateListener
                        },
                        onNavigateToLogin = {
                            currentScreen = AuthScreen.Login
                        }
                    )
                }
            }
        }
    }
}
