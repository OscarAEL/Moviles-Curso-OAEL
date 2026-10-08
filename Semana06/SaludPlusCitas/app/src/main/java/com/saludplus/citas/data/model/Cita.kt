package com.saludplus.citas.data.model

// Modelo de datos que representa una cita médica reservada por un usuario.
data class Cita(
    val id: Int,
    val usuarioId: Int,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val motivo: String = ""
)
