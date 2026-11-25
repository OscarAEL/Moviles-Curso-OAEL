package com.eneque.murocampus.data.remote

import com.eneque.murocampus.model.Comentario
import com.eneque.murocampus.model.NuevoPost
import com.eneque.murocampus.model.Post
import com.eneque.murocampus.model.Usuario
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @GET("posts")
    suspend fun listar(): List<Post>

    @GET("posts/{id}")
    suspend fun detalle(@Path("id") id: Int): Post

    @GET("posts/{id}/comments")
    suspend fun comentarios(@Path("id") id: Int): List<Comentario>

    @GET("posts")
    suspend fun porUsuario(@Query("userId") userId: Int): List<Post>

    @POST("posts")
    suspend fun crear(@Body nuevo: NuevoPost): Post
}

// Ejemplo de Usuario que la guía supone incluido en el proyecto base.
interface UsuarioApi {
    @GET("users")
    suspend fun listar(): List<Usuario>
}
