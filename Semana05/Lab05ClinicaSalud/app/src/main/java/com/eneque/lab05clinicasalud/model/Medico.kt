package com.eneque.lab05clinicasalud.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val calificacion: Double,
    val experiencia: Int,
    val resenas: Int,
    val descripcion: String
)

val listaMedicos = listOf(
    Medico(
        id = 1,
        nombre = "Dra. Ana Torres",
        especialidad = "Cardiología",
        calificacion = 4.9,
        experiencia = 12,
        resenas = 128,
        descripcion = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
    ),
    Medico(
        id = 2,
        nombre = "Dr. Luis Vega",
        especialidad = "Pediatría",
        calificacion = 4.7,
        experiencia = 10,
        resenas = 96,
        descripcion = "Especialista en atención pediátrica y seguimiento del desarrollo infantil."
    ),
    Medico(
        id = 3,
        nombre = "Dra. Rosa Díaz",
        especialidad = "Dermatología",
        calificacion = 4.8,
        experiencia = 9,
        resenas = 110,
        descripcion = "Especialista en dermatología clínica, cuidado y tratamiento de la piel."
    )
)
