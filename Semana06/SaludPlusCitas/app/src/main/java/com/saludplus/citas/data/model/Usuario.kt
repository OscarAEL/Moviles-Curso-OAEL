package com.saludplus.citas.data.model

// Modelo de datos que representa a un usuario o paciente registrado en la aplicación.
data class Usuario(
    val id: Int,
    val nombre: String,
    val telefono: String,
    val correo: String,
    val contrasena: String
)
