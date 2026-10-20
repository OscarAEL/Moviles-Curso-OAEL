package com.tecsup.lavacampus

import android.app.Application
import android.util.Log
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LavaApp : Application() {
    lateinit var database: LavaDatabase
        private set
    lateinit var repository: LavaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = LavaDatabase.getInstance(this)
        repository = LavaRepositoryRoom(database)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatosIniciales.sembrar(database)
            } catch (e: Exception) {
                Log.e("LavaApp", "Error durante la siembra de datos iniciales", e)
            }
        }
    }
}
