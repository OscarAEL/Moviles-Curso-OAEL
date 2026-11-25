package com.eneque.murocampus.viewmodel

import com.eneque.murocampus.model.Comentario
import com.eneque.murocampus.model.Post

data class DetalleUi(
    val post: Post,
    val comentarios: List<Comentario>
)
