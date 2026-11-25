package com.eneque.murocampus.data

import java.io.IOException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

// Los detalles de transporte se interpretan fuera del paquete viewmodel.
fun mensajeDe(error: Throwable): String = when (error) {
    is IOException -> "Sin conexión a internet.\nRevisa tu red e inténtalo otra vez."
    is HttpException -> "Error del servidor (${error.code()})"
    is SerializationException -> "Respuesta inesperada"
    else -> "Error desconocido"
}
