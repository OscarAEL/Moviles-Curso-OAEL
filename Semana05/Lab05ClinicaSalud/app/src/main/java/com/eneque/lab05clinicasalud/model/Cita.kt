package com.eneque.lab05clinicasalud.model

data class Cita(
    val id: Int,
    val medicoId: Int,
    val fecha: String,
    val hora: String,
    val estado: String
)
