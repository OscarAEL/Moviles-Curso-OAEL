package com.eneque.murocampus

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eneque.murocampus.data.PostRepositoryRemote
import com.eneque.murocampus.data.mensajeDe
import com.eneque.murocampus.data.remote.RetrofitClient
import com.eneque.murocampus.model.NuevoPost
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

// Pruebas de integración: requieren internet y usan únicamente datos de práctica.
@RunWith(AndroidJUnit4::class)
class ServicioRealTest {
    private val repo = PostRepositoryRemote(RetrofitClient.api)

    @Test fun listaYTodosLosFiltros() = runBlocking {
        assertEquals(100, repo.listar().getOrThrow().size)
        for (usuario in 1..10) {
            val posts = repo.porUsuario(usuario).getOrThrow()
            assertEquals("Usuario $usuario", 10, posts.size)
            assertTrue(posts.all { it.userId == usuario })
        }
    }

    @Test fun detallesConCincoComentariosY404() = runBlocking {
        for (id in listOf(1, 2, 11, 50, 100)) {
            assertEquals(id, repo.detalle(id).getOrThrow().id)
            val comentarios = repo.comentarios(id).getOrThrow()
            assertEquals(5, comentarios.size)
            assertTrue(comentarios.all { it.postId == id })
        }
        assertEquals(
            "Error del servidor (404)",
            mensajeDe(repo.detalle(9999).exceptionOrNull()!!)
        )
    }

    @Test fun crearDevuelve101YConservaElCuerpo() = runBlocking {
        val nuevo = NuevoPost(1, "Prueba del laboratorio", "Contenido de práctica")
        val creado = repo.crear(nuevo).getOrThrow()
        assertEquals(101, creado.id)
        assertEquals(nuevo.title, creado.title)
        assertEquals(nuevo.body, creado.body)
        assertEquals(nuevo.userId, creado.userId)
    }
}
