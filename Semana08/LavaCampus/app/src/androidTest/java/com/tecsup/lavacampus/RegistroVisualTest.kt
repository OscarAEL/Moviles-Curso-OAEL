package com.tecsup.lavacampus

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.RegistroScreen
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroVisualTest : MejorasTestBase() {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun registroVuelveALoginFiltraEntradasYPermiteIngresar() {
        val modelo =
            vm(SesionViewModel::class.java) {
                SesionViewModel(repo, prefs) { Result.success(Unit) }
            }
        esperar(modelo.restaurandoSesion) { !it }
        val registro = mutableStateOf(false)
        compose.runOnUiThread {
            compose.activity.setContent {
                LavaTheme {
                    if (registro.value) RegistroScreen(modelo, { registro.value = false })
                    else LoginScreen(modelo, { registro.value = true })
                }
            }
        }
        compose.onNodeWithText("¿No tienes cuenta? Regístrate").performScrollTo().performClick()
        compose.onNodeWithText("Solo para estudiantes del campus").assertExists()
        compose
            .onNode(hasText("Crear cuenta") and hasClickAction())
            .performScrollTo()
            .assertIsNotEnabled()
        val valores =
            listOf("Óscar1@ Ñúñez!", "12ab345678", "oscar1@test.com", "Secreto1", "Secreto1")
        valores.forEachIndexed { indice, valor ->
            compose.onAllNodes(hasSetTextAction())[indice].performScrollTo().performTextInput(valor)
        }
        compose
            .onNode(hasText("Crear cuenta") and hasClickAction())
            .performScrollTo()
            .performClick()
        compose.waitUntil(15000) { !registro.value }
        compose.onNodeWithText("Bienvenido").assertExists()
        assertNull(modelo.usuarioActual.value)
        compose.onAllNodes(hasSetTextAction())[0].assertTextContains("oscar1@test.com")
        compose.onAllNodes(hasSetTextAction())[1].performScrollTo().performTextInput("Secreto1")
        compose.onNodeWithText("Ingresar").performScrollTo().performClick()
        compose.waitUntil(15000) { modelo.usuarioActual.value != null }
        assertEquals("Óscar Ñúñez", modelo.usuarioActual.value!!.nombre)
        assertEquals("123456", modelo.usuarioActual.value!!.codigo)
        assertEquals("CLIENTE", modelo.usuarioActual.value!!.rol)
    }

    @Test
    fun administradorNoMuestraEnlaceDeRegistro() {
        val modelo =
            vm(SesionViewModel::class.java) {
                SesionViewModel(repo, prefs) { Result.success(Unit) }
            }
        esperar(modelo.restaurandoSesion) { !it }
        compose.runOnUiThread { compose.activity.setContent { LavaTheme { LoginScreen(modelo) } } }
        compose.onNodeWithText("Administrador").performScrollTo().performClick()
        compose.onNodeWithText("¿No tienes cuenta? Regístrate").assertDoesNotExist()
        compose.onNodeWithText("Estudiante").performScrollTo().performClick()
        compose.onNodeWithText("¿No tienes cuenta? Regístrate").assertExists()
    }
}
