package com.eneque.avisoscampus

import android.Manifest
import android.app.NotificationManager
import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.eneque.avisoscampus.viewmodel.AvisosViewModel
import com.eneque.avisoscampus.model.Origen
import java.io.File
import org.junit.*
import org.junit.Assert.*
import org.junit.Assume.*
import org.junit.runner.RunWith

/** Ejecutar con el permiso concedido; la clase SinPermisoTest se ejecuta aparte. */
@RunWith(AndroidJUnit4::class)
class AceptacionTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()
    private val device get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val vm get() = ViewModelProvider(regla.activity)[AvisosViewModel::class.java]
    private val manager get() = regla.activity.getSystemService(NotificationManager::class.java)
    private val avisosActivos get() = manager.activeNotifications.filter { it.tag == null }

    @Before fun preparar() {
        assumeTrue(Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            regla.activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        manager.cancelAll()
        device.executeShellCommand("cmd statusbar collapse")
        device.setOrientationNatural()
        regla.waitForIdle()
    }

    @After fun limpiar() {
        device.executeShellCommand("cmd connectivity airplane-mode disable")
        device.setOrientationNatural()
        device.unfreezeRotation()
    }

    private fun captura(nombre: String) {
        val carpeta = File(regla.activity.getExternalFilesDir(null), "evidencias")
        carpeta.mkdirs()
        regla.waitForIdle()
        repeat(3) {
            if (device.takeScreenshot(File(carpeta, nombre))) return
            device.waitForIdle(1000)
        }
        fail("No se pudo capturar $nombre")
    }

    @Test fun canalDosAvisosYAperturaDesdeNotificacion() {
        assertEquals(3, manager.getNotificationChannel("avisos").importance)
        regla.onNodeWithText("Mostrar aviso").performScrollTo().performClick()
        regla.waitUntil(5000) { avisosActivos.size == 1 }
        regla.onNodeWithText("Mostrar aviso").performClick()
        regla.waitUntil(5000) { avisosActivos.size == 2 }
        assertEquals(2, avisosActivos.map { it.id }.distinct().size)
        assertEquals("Tarea lista", avisosActivos.first().notification.extras.getString("android.title"))
        device.pressHome()
        device.openNotification()
        val expandir = device.wait(Until.findObject(By.res("android", "expand_button")), 5000)
        assertNotNull(expandir)
        expandir.click()
        device.waitForIdle()
        assertTrue(device.wait(Until.hasObject(By.text("Tarea lista")), 5000))
        captura("01-notificacion.png")
        device.findObject(By.text("Tarea lista")).click()
        assertTrue(device.wait(Until.hasObject(By.pkg("com.eneque.avisoscampus")), 5000))
        regla.waitUntil(5000) { avisosActivos.size == 1 }
        // El caso con la Activity cerrada se comprueba sin ActivityScenario en AperturaCerradaTest.
    }

    @Test fun cancelarRotarYRecibirRecordatorioEnSegundoPlano() {
        regla.onNodeWithText("Recordar en 10 s").performScrollTo().performClick()
        regla.onNodeWithText("Recordar en 10 s").assertIsNotEnabled()
        regla.onNodeWithText("Cancelar").performScrollTo().performClick()
        regla.waitUntil(3000) { vm.faltan.value == null }
        assertTrue(vm.avisos.value.isEmpty())
        regla.onNodeWithText("Recordar en 10 s").performScrollTo().performClick()
        regla.waitUntil(3000) { vm.faltan.value != null }
        regla.onNodeWithText("Faltan", substring = true).performScrollTo().assertIsDisplayed()
        captura("02-recordatorio.png")
        val antes = vm
        device.setOrientationLeft()
        regla.waitForIdle()
        assertSame(antes, vm)
        assertNotNull(vm.faltan.value)
        device.setOrientationNatural()
        regla.waitForIdle()
        device.pressHome()
        regla.waitUntil(15000) { manager.activeNotifications.any {
            it.notification.extras.getString("android.title") == "Recordatorio"
        } }
        assertEquals(1, vm.avisos.value.count { it.origen == Origen.RECORDATORIO })
        assertNull(vm.faltan.value)
    }

    @Test fun modoAvionSwitchHistorialYRotacion() {
        regla.onNode(isToggleable()).performScrollTo().performClick()
        regla.waitUntil(3000) { vm.escuchandoAvion.value }
        regla.onNode(isToggleable()).assertIsOn()
        regla.waitForIdle()
        device.executeShellCommand("cmd connectivity airplane-mode enable")
        regla.waitUntil(5000) { vm.avisos.value.any { it.texto == "Activado" } }
        device.executeShellCommand("cmd connectivity airplane-mode disable")
        regla.waitUntil(5000) { vm.avisos.value.firstOrNull()?.texto == "Desactivado" }
        regla.onNodeWithText("Desactivado").assertExists()
        captura("03-modo-avion.png")
        val antes = vm
        val avisos = vm.avisos.value
        device.setOrientationLeft()
        regla.waitForIdle()
        assertSame(antes, vm)
        assertEquals(avisos, vm.avisos.value)
        assertTrue(vm.escuchandoAvion.value)
        device.setOrientationNatural()
        regla.waitForIdle()
        regla.onNode(isToggleable()).performScrollTo().performClick()
        val cantidad = vm.avisos.value.size
        device.executeShellCommand("cmd connectivity airplane-mode enable")
        // Se espera al sistema sin bloquear el hilo principal.
        device.waitForIdle(2000)
        assertEquals(cantidad, vm.avisos.value.size)
        device.executeShellCommand("cmd connectivity airplane-mode disable")
        regla.onNodeWithText("Historial").performScrollTo()
        regla.onAllNodesWithText("Desactivado").onFirst().assertExists()
        captura("04-historial.png")
    }
}

@RunWith(AndroidJUnit4::class)
class SinPermisoTest {
    @get:Rule val regla = createAndroidComposeRule<MainActivity>()

    @Test fun negarPermisoGuardaAvisoAbreAjustesYSeRecupera() {
        assumeTrue(Build.VERSION.SDK_INT >= 33)
        assumeTrue(ContextCompat.checkSelfPermission(regla.activity, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED)
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val manager = regla.activity.getSystemService(NotificationManager::class.java)
        regla.onNodeWithText("Permitir notificaciones").performClick()
        val negar = device.wait(Until.findObject(By.res("com.android.permissioncontroller", "permission_deny_button")), 5000)
        assertNotNull("Debe aparecer el diálogo nativo de permiso", negar)
        negar.click()
        regla.onNodeWithText("Denegado").assertExists()
        regla.onNodeWithText("Mostrar aviso").performScrollTo().performClick()
        regla.waitUntil(5000) {
            ViewModelProvider(regla.activity)[AvisosViewModel::class.java].mensaje.value != null
        }
        assertTrue(manager.activeNotifications.isEmpty())
        assertEquals(1, ViewModelProvider(regla.activity)[AvisosViewModel::class.java].avisos.value.size)
        regla.onNodeWithText("Sin permiso: el aviso se guardó en el historial, pero no se mostró").assertExists()
        regla.onNodeWithText("Abrir ajustes").performScrollTo()
        val carpeta = File(regla.activity.getExternalFilesDir(null), "evidencias")
        carpeta.mkdirs()
        assertTrue(device.takeScreenshot(File(carpeta, "05-permiso-denegado.png")))
        regla.onNodeWithText("Abrir ajustes").performClick()
        assertTrue(device.wait(Until.hasObject(By.pkg("com.android.settings")), 5000))
        // Conceder equivale al cambio del permiso desde Ajustes, sin revocar durante la instrumentación.
        device.executeShellCommand("pm grant com.eneque.avisoscampus android.permission.POST_NOTIFICATIONS")
        device.pressBack()
        regla.waitUntil(5000) {
            ViewModelProvider(regla.activity)[AvisosViewModel::class.java].mensaje.value == null
        }
        regla.onNodeWithText("Permitido").assertExists()
    }
}
