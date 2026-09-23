package com.eneque.lab05tecsupfit.model

data class ClaseFit(
    val id: Int,
    val nombre: String,
    val horario: String,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cuposDisponibles: String,
    val filtro: String
)

object ClaseRepository {
    val listaClases = listOf(
        ClaseFit(
            id = 1,
            nombre = "Yoga funcional",
            horario = "7:00 am",
            sala = "Sala 2",
            duracion = "45 min",
            descripcion = "Clase enfocada en movilidad, respiración y fortalecimiento corporal.",
            cuposDisponibles = "10 de 15",
            filtro = "Hoy"
        ),
        ClaseFit(
            id = 2,
            nombre = "Cross Training",
            horario = "6:00 pm",
            sala = "Sala 1",
            duracion = "45 min",
            descripcion = "Entrenamiento funcional de alta intensidad. Cupos limitados.",
            cuposDisponibles = "8 de 12",
            filtro = "Hoy"
        ),
        ClaseFit(
            id = 3,
            nombre = "Spinning",
            horario = "7:30 pm",
            sala = "Sala 3",
            duracion = "50 min",
            descripcion = "Entrenamiento cardiovascular sobre bicicleta con intensidad progresiva.",
            cuposDisponibles = "6 de 10",
            filtro = "Hoy"
        )
    )

    fun getClaseById(id: Int): ClaseFit? {
        return listaClases.find { it.id == id }
    }
}
