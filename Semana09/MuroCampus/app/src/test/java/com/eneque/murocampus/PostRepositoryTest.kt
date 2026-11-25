package com.eneque.murocampus

import com.eneque.murocampus.data.PostRepositoryRemote
import com.eneque.murocampus.data.mensajeDe
import com.eneque.murocampus.data.remote.PostApi
import com.eneque.murocampus.model.Comentario
import com.eneque.murocampus.model.NuevoPost
import com.eneque.murocampus.model.Post
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PostRepositoryTest {
    private val post = Post(1, 1, "Título", "Contenido")

    private inner class ApiFalsa(private val error: Exception? = null, val codigoEliminar: Int = 200) : PostApi {
        private fun comprobar() { error?.let { throw it } }
        override suspend fun eliminar(id: Int): Response<Unit> {
            comprobar()
            return if (codigoEliminar in 200..299) Response.success(Unit)
            else Response.error(codigoEliminar, "{}".toResponseBody())
        }
        override suspend fun listar(): List<Post> { comprobar(); return listOf(post) }
        override suspend fun detalle(id: Int): Post { comprobar(); return post }
        override suspend fun comentarios(id: Int): List<Comentario> {
            comprobar()
            return listOf(Comentario(id, 1, "Nombre", "alumno@campus.test", "Comentario"))
        }
        override suspend fun porUsuario(userId: Int): List<Post> {
            comprobar()
            return listOf(post.copy(userId = userId))
        }
        override suspend fun crear(nuevo: NuevoPost): Post {
            comprobar()
            return Post(nuevo.userId, 101, nuevo.title, nuevo.body)
        }
    }

    @Test fun exitoYCreacionSimulada() = runBlocking {
        val repo = PostRepositoryRemote(ApiFalsa())
        assertEquals(listOf(post), repo.listar().getOrThrow())
        assertEquals(101, repo.crear(NuevoPost(1, "Título", "Contenido")).getOrThrow().id)
    }

    @Test fun todasLasOperacionesConviertenErroresEnResult() = runBlocking {
        val fallo = IOException("Sin red")
        val repo = PostRepositoryRemote(ApiFalsa(fallo))
        val resultados = listOf(
            repo.listar(), repo.detalle(1), repo.comentarios(1),
            repo.porUsuario(2), repo.crear(NuevoPost(1, "T", "C")), repo.eliminar(1)
        )
        resultados.forEach { assertSame(fallo, it.exceptionOrNull()) }
        assertTrue(mensajeDe(fallo).startsWith("Sin conexión a internet."))
    }

    @Test fun cancelacionSePropagaEnTodasLasOperaciones() = runBlocking {
        val repo = PostRepositoryRemote(ApiFalsa(CancellationException("Salir de pantalla")))
        val operaciones: List<suspend () -> Unit> = listOf(
            { repo.listar(); Unit }, { repo.detalle(1); Unit }, { repo.comentarios(1); Unit },
            { repo.porUsuario(2); Unit }, { repo.crear(NuevoPost(1, "T", "C")); Unit },
            { repo.eliminar(1); Unit }
        )
        operaciones.forEach { operacion ->
            try {
                operacion()
                fail("La cancelación no debe convertirse en Result.failure")
            } catch (_: CancellationException) {
                // Excepción esperada: se conserva la cancelación estructurada.
            }
        }
    }

    @Test fun eliminarCompruebaElCodigoHttp() = runBlocking {
        assertTrue(PostRepositoryRemote(ApiFalsa()).eliminar(1).isSuccess)
        val fallo = PostRepositoryRemote(ApiFalsa(codigoEliminar = 500)).eliminar(1)
        assertEquals("Error del servidor (500)", mensajeDe(fallo.exceptionOrNull()!!))
    }

    @Test fun recursoInexistenteExpone404SinCerrarLaApp() = runBlocking {
        val error = HttpException(Response.error<Post>(404, "{}".toResponseBody()))
        val resultado = PostRepositoryRemote(ApiFalsa(error)).detalle(9999)
        assertEquals("Error del servidor (404)", mensajeDe(resultado.exceptionOrNull()!!))
    }

    @Test fun jsonToleraCamposExtrasYRechazaCamposObligatoriosAusentes() {
        val json = Json { ignoreUnknownKeys = true }
        val valido = """{"userId":1,"id":1,"title":"Título","body":"Contenido","extra":true}"""
        assertEquals(post, json.decodeFromString<Post>(valido))
        try {
            json.decodeFromString<Post>("""{"userId":1,"id":1,"title":"Título"}""")
            fail("body es obligatorio")
        } catch (error: SerializationException) {
            assertEquals("Respuesta inesperada", mensajeDe(error))
        }
        assertFalse(json.encodeToString(NuevoPost(1, "T", "C")).contains("\"id\""))
    }
}
