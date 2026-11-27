package com.eneque.avisoscampus.notificacion

import android.app.PendingIntent
import android.content.Intent
import com.eneque.avisoscampus.MainActivity
import android.annotation.SuppressLint
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.eneque.avisoscampus.R
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object Notificador {
    const val CANAL = "avisos"
    const val CANAL_URGENTES = "urgentes"

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false
        val urgente = aviso.origen == Origen.RECORDATORIO
        val canal = if (urgente) CANAL_URGENTES else CANAL
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return false
        if (context.getSystemService(NotificationManager::class.java)
                .getNotificationChannel(canal)?.importance == NotificationManager.IMPORTANCE_NONE
        ) return false
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificacion = NotificationCompat.Builder(context, canal)
            .setContentIntent(pendiente)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setPriority(if (urgente) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        return try {
            manager.notify(aviso.id, notificacion)
            true
        } catch (_: SecurityException) {
            false
        }
    }

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannels(
                    listOf(
                        NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT),
                        NotificationChannel(CANAL_URGENTES, "Urgentes", NotificationManager.IMPORTANCE_HIGH)
                    )
                )
        }
    }
}
