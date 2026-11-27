package com.eneque.avisoscampus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import com.eneque.avisoscampus.ui.HistorialAvisos
import com.eneque.avisoscampus.ui.theme.AvisosCampusTheme
import org.junit.Rule
import org.junit.Test

class HistorialUiTest {
    @get:Rule val regla = createComposeRule()

    @Test fun historialLimitaAVeinteSinInventarAvisos() {
        val avisos = (25 downTo 1).map { Aviso(it, "Aviso $it", "Texto $it", Origen.BOTON, "12:00:00") }
        regla.setContent {
            AvisosCampusTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) { HistorialAvisos(avisos) }
            }
        }
        regla.onNodeWithText("Aviso 25").assertExists()
        regla.onNodeWithText("Aviso 6").assertExists()
        regla.onNodeWithText("Aviso 5").assertDoesNotExist()
    }

    @Test fun historialVacio() {
        regla.setContent { AvisosCampusTheme { HistorialAvisos(emptyList()) } }
        regla.onNodeWithText("Sin avisos todavía").assertExists()
    }
}
