package com.eneque.avisoscampus

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import com.eneque.avisoscampus.data.AvisosRepositoryDataStore
import com.eneque.avisoscampus.data.AvisosSerializer
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PersistenciaTest {
    @get:Rule val temporal = TemporaryFolder()

    @Test fun formatoConservaTextoOrigenHoraEIds() = runBlocking {
        val avisos = listOf(
            Aviso(2, "Modo avión", "Activado", Origen.MODO_AVION, "12:00:02"),
            Aviso(1, "Tarea lista", "Información ficticia del campus. ".repeat(20), Origen.BOTON, "12:00:01")
        )
        val salida = ByteArrayOutputStream()
        AvisosSerializer.writeTo(avisos, salida)
        assertEquals(avisos, AvisosSerializer.readFrom(ByteArrayInputStream(salida.toByteArray())))
    }

    @Test fun formatoDanadoSeRechazaEnLugarDeInventarDatos() = runBlocking {
        try {
            AvisosSerializer.readFrom(ByteArrayInputStream(byteArrayOf(1, 2, 3)))
            fail("Debe rechazar un archivo incompleto")
        } catch (_: CorruptionException) {
            // Un archivo inválido no se sobrescribe con un historial vacío.
        }
    }

    @Test fun reabrirConservaHistorialYEscriturasConcurrentesNoRepitenIds() = runBlocking {
        val archivo = File(temporal.newFolder(), "avisos.bin")
        val scope1 = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val almacen1 = DataStoreFactory.create(AvisosSerializer, scope = scope1, produceFile = { archivo })
        val repo1 = AvisosRepositoryDataStore(almacen1, scope1)
        try {
            val primero = repo1.registrar("Tarea lista", "Texto ficticio", Origen.BOTON)
            assertEquals(1, primero.id)
            coroutineScope {
                (1..9).map {
                    async(Dispatchers.Default) { repo1.registrar("Aviso $it", "Texto $it", Origen.BOTON) }
                }.awaitAll()
            }
            assertEquals((10 downTo 1).toList(), almacen1.data.first().map { it.id })
        } finally {
            scope1.coroutineContext[Job]!!.cancelAndJoin()
        }
        val scope2 = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val almacen2 = DataStoreFactory.create(AvisosSerializer, scope = scope2, produceFile = { archivo })
        val repo2 = AvisosRepositoryDataStore(almacen2, scope2)
        try {
            assertEquals(10, almacen2.data.first().size)
            val siguiente = repo2.registrar("Recordatorio", "Texto ficticio", Origen.RECORDATORIO)
            assertEquals(11, siguiente.id)
            val visibles = withTimeout(5000) { repo2.avisos.first { it.size == 11 } }
            assertEquals(siguiente, visibles.first())
        } finally {
            scope2.coroutineContext[Job]!!.cancelAndJoin()
        }
    }
}
