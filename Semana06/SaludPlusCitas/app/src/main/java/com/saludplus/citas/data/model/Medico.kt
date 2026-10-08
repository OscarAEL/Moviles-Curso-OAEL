package com.saludplus.citas.data.model

// Modelo de datos que representa a un médico especialista disponible en la clínica.
data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val especialidad: String,
    val cmp: String,
    val calificacion: Double
)
