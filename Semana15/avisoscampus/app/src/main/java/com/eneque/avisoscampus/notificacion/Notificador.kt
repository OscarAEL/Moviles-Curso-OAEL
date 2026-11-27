package com.eneque.avisoscampus.notificacion

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Notificador {
    const val CANAL = "avisos"

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(
                    NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
                )
        }
    }
}
