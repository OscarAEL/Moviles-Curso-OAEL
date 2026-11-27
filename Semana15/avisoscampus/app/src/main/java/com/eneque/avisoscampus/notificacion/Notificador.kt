package com.eneque.avisoscampus.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Notificador {
    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(
                    NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
                )
        }
    }
}
