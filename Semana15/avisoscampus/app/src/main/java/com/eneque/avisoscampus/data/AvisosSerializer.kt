package com.eneque.avisoscampus.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException

/** Formato local versionado; DataStore controla las escrituras atómicas y el hilo de E/S. */
object AvisosSerializer : Serializer<List<Aviso>> {
    override val defaultValue: List<Aviso> = emptyList()

    override suspend fun readFrom(input: InputStream): List<Aviso> {
        try {
            val datos = DataInputStream(input)
            require(datos.readInt() == 0x41564953) { "Cabecera no válida" }
            require(datos.readInt() == 1) { "Versión no compatible" }
            val cantidad = datos.readInt()
            require(cantidad in 0..100_000) { "Cantidad no válida" }
            return List(cantidad) {
                Aviso(
                    id = datos.readInt().also { require(it > 0) },
                    titulo = datos.readUTF(),
                    texto = datos.readUTF(),
                    origen = Origen.valueOf(datos.readUTF()),
                    hora = datos.readUTF()
                )
            }
        } catch (error: IOException) {
            throw CorruptionException("No se pudo leer el historial de avisos", error)
        } catch (error: IllegalArgumentException) {
            throw CorruptionException("El historial tiene un formato no válido", error)
        }
    }

    override suspend fun writeTo(t: List<Aviso>, output: OutputStream) {
        val datos = DataOutputStream(output)
        datos.writeInt(0x41564953)
        datos.writeInt(1)
        datos.writeInt(t.size)
        t.forEach { aviso ->
            datos.writeInt(aviso.id)
            datos.writeUTF(aviso.titulo)
            datos.writeUTF(aviso.texto)
            datos.writeUTF(aviso.origen.name)
            datos.writeUTF(aviso.hora)
        }
        datos.flush()
        // DataStore es dueño del stream: no se cierra aquí.
    }
}
