package com.eneque.murocampus

import com.eneque.murocampus.data.remote.AcceptJsonInterceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class AcceptJsonInterceptorTest {
    @Test fun todasLasSolicitudesLlevanUnSoloAcceptJson() {
        val cliente = OkHttpClient.Builder()
            .addInterceptor(AcceptJsonInterceptor())
            .addInterceptor { chain ->
                val solicitud = chain.request()
                assertEquals(listOf("application/json"), solicitud.headers.values("Accept"))
                // Se termina la cadena en memoria; esta prueba no accede a internet.
                Response.Builder()
                    .request(solicitud)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("{}".toResponseBody())
                    .build()
            }
            .build()
        for (valor in listOf<String?>(null, "text/plain")) {
            val solicitud = Request.Builder().url("https://campus.example/posts").apply {
                valor?.let { header("Accept", it) }
            }.build()
            cliente.newCall(solicitud).execute().use {
                assertEquals(200, it.code)
            }
        }
    }
}
