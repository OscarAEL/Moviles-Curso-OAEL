package com.tecsup.lavacampus

import android.app.Application
import android.util.Log
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LavaApp : Application() {
    lateinit var database: LavaDatabase
        private set
    lateinit var repository: LavaRepository
        private set
    lateinit var preferenciasStore: PreferenciasStore
        private set

    val resultadoInicializacion = MutableStateFlow<Result<Unit>?>(null)

    override fun onCreate() {
        super.onCreate()
        database = LavaDatabase.getInstance(this)
        repository = LavaRepositoryRoom(database)
        preferenciasStore = PreferenciasStore(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatosIniciales.sembrar(database)
                resultadoInicializacion.value = Result.success(Unit)
            } catch (e: Exception) {
                Log.e("LavaApp", "Error durante la siembra de datos iniciales", e)
                resultadoInicializacion.value = Result.failure(e)
            }
        }
    }

    suspend fun esperarInicializacion(): Result<Unit> {
        return resultadoInicializacion.filterNotNull().first()
    }
}
