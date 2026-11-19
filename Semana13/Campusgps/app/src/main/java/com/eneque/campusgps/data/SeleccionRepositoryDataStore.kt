package com.eneque.campusgps.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Una única instancia por archivo y proceso; usa el contexto de aplicación.
private val Context.seleccionDataStore by preferencesDataStore(
    name = "seleccion_campus",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

class SeleccionRepositoryDataStore(
    private val dataStore: DataStore<Preferences>
) : SeleccionRepository {
    constructor(context: Context) : this(context.applicationContext.seleccionDataStore)

    override val ultimoLugarId: Flow<Int?> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                Log.w("SeleccionRepository", "No se pudo leer la selección guardada", error)
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { it[ULTIMO_LUGAR_ID] }

    override suspend fun guardarLugar(id: Int) {
        try {
            dataStore.edit { it[ULTIMO_LUGAR_ID] = id }
        } catch (error: IOException) {
            // Un fallo de disco no debe cerrar la app ni modificar el error de ubicación.
            Log.w("SeleccionRepository", "No se pudo guardar la selección", error)
        }
    }

    private companion object {
        val ULTIMO_LUGAR_ID = intPreferencesKey("ultimo_lugar_id")
    }
}
