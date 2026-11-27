package com.eneque.avisoscampus

import androidx.datastore.core.DataStoreFactory
import com.eneque.avisoscampus.data.AvisosRepository
import com.eneque.avisoscampus.data.AvisosRepositoryDataStore
import com.eneque.avisoscampus.data.AvisosSerializer
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import android.app.Application
import com.eneque.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val almacenAvisos by lazy {
        DataStoreFactory.create(
            serializer = AvisosSerializer,
            scope = applicationScope,
            produceFile = { File(filesDir, "datastore/avisos.bin") }
        )
    }
    val repository: AvisosRepository by lazy {
        AvisosRepositoryDataStore(almacenAvisos, applicationScope)
    }

    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}
