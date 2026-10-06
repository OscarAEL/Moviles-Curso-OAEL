package com.saludplus.citas.data.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val especialidad: String,
    val cmp: String,
    val calificacion: Double
)