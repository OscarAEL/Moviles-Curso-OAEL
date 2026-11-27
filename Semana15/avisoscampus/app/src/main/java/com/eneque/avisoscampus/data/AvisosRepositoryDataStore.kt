package com.eneque.avisoscampus.data

import androidx.datastore.core.DataStore
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn

class AvisosRepositoryDataStore(
    private val almacen: DataStore<List<Aviso>>,
    scope: CoroutineScope
) : AvisosRepository {
    override val avisos = almacen.data.catch { error ->
        if (error is IOException) emit(emptyList()) else throw error
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override suspend fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
        val actualizados = almacen.updateData { anteriores ->
            val ultimoId = anteriores.maxOfOrNull { it.id } ?: 0
            if (ultimoId == Int.MAX_VALUE) throw IOException("No quedan IDs disponibles")
            val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            listOf(Aviso(ultimoId + 1, titulo, texto, origen, hora)) + anteriores
        }
        return actualizados.first()
    }
}
