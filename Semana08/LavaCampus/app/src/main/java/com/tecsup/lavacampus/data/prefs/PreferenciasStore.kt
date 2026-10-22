package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

data class SesionGuardada(
    val perfil: String,
    val usuarioId: Int
)

class PreferenciasStore(private val dataStore: DataStore<Preferences>) {
    constructor(context: Context) : this(context.applicationContext.dataStore)

    companion object {
        private val PERFIL_ACTIVO = stringPreferencesKey("perfil_activo")
        private val USUARIO_ID = intPreferencesKey("usuario_id")
        private val ULTIMO_CLIENTE = intPreferencesKey("ultimo_cliente_id")
        private val ULTIMO_ADMIN = intPreferencesKey("ultimo_admin_id")
        private val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        private val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        private val RECIBIR_PEDIDOS = booleanPreferencesKey("recibir_pedidos")
    }

    private val safeData: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    val sesionGuardada: Flow<SesionGuardada> = safeData.map { preferences ->
        SesionGuardada(
            perfil = preferences[PERFIL_ACTIVO] ?: "",
            usuarioId = preferences[USUARIO_ID] ?: 0
        )
    }

    val perfilActivo: Flow<String> = safeData.map { preferences ->
        preferences[PERFIL_ACTIVO] ?: ""
    }

    val usuarioId: Flow<Int> = safeData.map { preferences ->
        preferences[USUARIO_ID] ?: 0
    }

    val modoOscuro: Flow<Boolean> = safeData.map { preferences ->
        preferences[MODO_OSCURO] ?: false
    }

    val notificaciones: Flow<Boolean> = safeData.map { preferences ->
        preferences[NOTIFICACIONES] ?: true
    }

    val recibirPedidos: Flow<Boolean> = safeData.map { preferences ->
        preferences[RECIBIR_PEDIDOS] ?: true
    }

    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        dataStore.edit { preferences ->
            preferences[PERFIL_ACTIVO] = rol.name
            preferences[USUARIO_ID] = usuarioId
            preferences[if (rol == Rol.ADMIN) ULTIMO_ADMIN else ULTIMO_CLIENTE] = usuarioId
        }
    }

    fun ultimoUsuario(rol: Rol): Flow<Int> = safeData.map {
        it[if (rol == Rol.ADMIN) ULTIMO_ADMIN else ULTIMO_CLIENTE] ?: 0
    }

    suspend fun guardarModoOscuro(b: Boolean) {
        dataStore.edit { preferences ->
            preferences[MODO_OSCURO] = b
        }
    }

    suspend fun guardarNotificaciones(b: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICACIONES] = b
        }
    }

    suspend fun guardarRecibirPedidos(b: Boolean) {
        dataStore.edit { preferences ->
            preferences[RECIBIR_PEDIDOS] = b
        }
    }

    suspend fun cerrarSesion() {
        dataStore.edit { preferences ->
            preferences.remove(PERFIL_ACTIVO)
            preferences.remove(USUARIO_ID)
        }
    }
}
