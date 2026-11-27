package com.eneque.avisoscampus

import android.app.NotificationManager
import androidx.test.platform.app.InstrumentationRegistry
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import com.eneque.avisoscampus.notificacion.Notificador
import org.junit.Assert.*
import org.junit.Test
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.delay

class MejorasNotificacionTest {
    @Test fun recordatorioUrgenteYAvisoNormalUsanCanalesDistintos() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = context.getSystemService(NotificationManager::class.java)
        assertEquals(3, manager.getNotificationChannel(Notificador.CANAL).importance)
        assertEquals(4, manager.getNotificationChannel(Notificador.CANAL_URGENTES).importance)
        try {
            assertTrue(Notificador.mostrar(context, Aviso(9001, "Recordatorio", "Aviso ficticio", Origen.RECORDATORIO, "12:00:00")))
            assertTrue(Notificador.mostrar(context, Aviso(9002, "Tarea lista", "Aviso ficticio", Origen.BOTON, "12:00:01")))
            runBlocking {
                withTimeout(5000) {
                    while (manager.activeNotifications.count { it.id == 9001 || it.id == 9002 } < 2) delay(50)
                }
            }
            val avisos = manager.activeNotifications.associateBy { it.id }
            assertEquals("urgentes", avisos.getValue(9001).notification.channelId)
            assertEquals("avisos", avisos.getValue(9002).notification.channelId)
        } finally {
            manager.cancel(9001)
            manager.cancel(9002)
        }
    }
}
