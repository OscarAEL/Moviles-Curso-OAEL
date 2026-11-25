package com.eneque.murocampus.data

import com.eneque.murocampus.data.remote.PostApi
import com.eneque.murocampus.data.remote.UsuarioApi
import com.eneque.murocampus.model.Comentario
import com.eneque.murocampus.model.NuevoPost
import com.eneque.murocampus.model.Post
import com.eneque.murocampus.model.Usuario
import kotlinx.coroutines.CancellationException

interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
}

class PostRepositoryRemote(private val api: PostApi) : PostRepository {
    override suspend fun listar() = llamar { api.listar() }
    override suspend fun detalle(id: Int) = llamar { api.detalle(id) }
    override suspend fun comentarios(id: Int) = llamar { api.comentarios(id) }
    override suspend fun porUsuario(userId: Int) = llamar { api.porUsuario(userId) }
    override suspend fun crear(nuevo: NuevoPost) = llamar { api.crear(nuevo) }
}

interface UsuarioRepository {
    suspend fun listar(): Result<List<Usuario>>
}

class UsuarioRepositoryRemote(private val api: UsuarioApi) : UsuarioRepository {
    override suspend fun listar() = llamar { api.listar() }
}

// La cancelación pertenece al ciclo de vida de la corrutina, no es un error de red.
private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
    try {
        Result.success(bloque())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
