package com.eneque.avisoscampus

import android.app.NotificationManager
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AperturaCerradaTest {
    @Test fun notificacionAbreUnaActivityNuevaYSeRetira() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val manager = context.getSystemService(NotificationManager::class.java)
        val device = UiDevice.getInstance(instrumentation)
        manager.cancelAll()
        device.executeShellCommand("cmd statusbar collapse")
        val activity = instrumentation.startActivitySync(
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        val mostrar = device.wait(Until.findObject(By.text("Mostrar aviso")), 15000)
        assertNotNull(mostrar)
        mostrar.click()
        device.waitForIdle()
        assertTrue(manager.activeNotifications.any { it.tag == null })
        instrumentation.runOnMainSync { activity.finish() }
        device.pressHome()
        device.openNotification()
        val aviso = device.wait(Until.findObject(By.text("Tarea lista")), 15000)
        assertNotNull(aviso)
        aviso.click()
        assertTrue(device.wait(Until.hasObject(By.pkg(context.packageName)), 15000))
        device.waitForIdle()
        assertTrue(manager.activeNotifications.none { it.tag == null })
    }
}
