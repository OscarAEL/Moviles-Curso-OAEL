package com.eneque.avisoscampus

import android.app.Application
import com.eneque.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
