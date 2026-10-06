package com.saludplus.citas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario

object Repositorio {

    // -------------------------
    // USUARIOS
    // -------------------------

    val usuarios = mutableStateListOf(
        Usuario(
            id = 1,
            nombre = "Juan Pérez",
            telefono = "987654321",
            correo = "juan@correo.com",
            contrasena = "123456"
        )
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun registrarUsuario(usuario: Usuario): Boolean {
        val correoExiste = usuarios.any { it.correo == usuario.correo }

        if (correoExiste) {
            return false
        }

        usuarios.add(usuario)
        usuarioActual = usuario
        return true
    }

    fun iniciarSesion(correo: String, contrasena: String): Boolean {
        val usuario = usuarios.find {
            it.correo == correo && it.contrasena == contrasena
        }

        usuarioActual = usuario
        return usuario != null
    }

    fun cerrarSesion() {
        usuarioActual = null
    }


    // -------------------------
    // ESPECIALIDADES
    // -------------------------

    val especialidades = listOf(
        Especialidad(
            id = 1,
            nombre = "Medicina General",
            descripcion = "Atención médica general"
        ),
        Especialidad(
            id = 2,
            nombre = "Pediatría",
            descripcion = "Atención médica para niños"
        ),
        Especialidad(
            id = 3,
            nombre = "Ginecología",
            descripcion = "Salud integral de la mujer"
        ),
        Especialidad(
            id = 4,
            nombre = "Cardiología",
            descripcion = "Atención del corazón"
        ),
        Especialidad(
            id = 5,
            nombre = "Dermatología",
            descripcion = "Cuidado de la piel"
        ),
        Especialidad(
            id = 6,
            nombre = "Traumatología",
            descripcion = "Lesiones de huesos y articulaciones"
        ),
        Especialidad(
            id = 7,
            nombre = "Oftalmología",
            descripcion = "Cuidado de la visión"
        )
    )

    fun buscarEspecialidades(texto: String): List<Especialidad> {
        return especialidades.filter {
            it.nombre.contains(texto, ignoreCase = true)
        }
    }

    fun especialidadesDestacadas(): List<Especialidad> {
        return especialidades.take(3)
    }

    fun obtenerEspecialidad(id: Int): Especialidad? {
        return especialidades.find { it.id == id }
    }


    // -------------------------
    // MÉDICOS
    // -------------------------

    val medicos = listOf(
        Medico(
            id = 1,
            nombre = "Dra. Ana Torres",
            especialidadId = 3,
            especialidad = "Ginecología",
            cmp = "CMP 12345",
            calificacion = 4.9
        ),
        Medico(
            id = 2,
            nombre = "Dr. Carlos Rojas",
            especialidadId = 3,
            especialidad = "Ginecología",
            cmp = "CMP 23456",
            calificacion = 4.8
        ),
        Medico(
            id = 3,
            nombre = "Dr. Luis Ramírez",
            especialidadId = 3,
            especialidad = "Ginecología",
            cmp = "CMP 34567",
            calificacion = 4.7
        ),
        Medico(
            id = 4,
            nombre = "Dra. Mariana Soto",
            especialidadId = 3,
            especialidad = "Ginecología",
            cmp = "CMP 45678",
            calificacion = 4.6
        ),
        Medico(
            id = 5,
            nombre = "Dr. Miguel Flores",
            especialidadId = 1,
            especialidad = "Medicina General",
            cmp = "CMP 56789",
            calificacion = 4.9
        ),
        Medico(
            id = 6,
            nombre = "Dra. Rosa Díaz",
            especialidadId = 2,
            especialidad = "Pediatría",
            cmp = "CMP 67890",
            calificacion = 4.8
        ),
        Medico(
            id = 7,
            nombre = "Dr. Pedro León",
            especialidadId = 4,
            especialidad = "Cardiología",
            cmp = "CMP 78901",
            calificacion = 4.7
        )
    )

    fun medicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return medicos
            .filter { it.especialidadId == especialidadId }
            .sortedByDescending { it.calificacion }
    }

    fun buscarMedicos(
        especialidadId: Int,
        texto: String
    ): List<Medico> {
        return medicos
            .filter {
                it.especialidadId == especialidadId &&
                        it.nombre.contains(texto, ignoreCase = true)
            }
            .sortedByDescending { it.calificacion }
    }

    fun obtenerMedico(id: Int): Medico? {
        return medicos.find { it.id == id }
    }


    // -------------------------
    // CITAS
    // -------------------------

    val citas = mutableStateListOf<Cita>()

    private val horariosBase = listOf(
        "08:00",
        "08:30",
        "09:00",
        "09:30",
        "10:00",
        "10:30",
        "11:00",
        "11:30",
        "14:00",
        "14:30",
        "15:00",
        "15:30"
    )

    fun horariosDisponibles(
        medicoId: Int,
        fecha: String
    ): List<String> {

        val horariosOcupados = citas
            .filter {
                it.medicoId == medicoId &&
                        it.fecha == fecha
            }
            .map { it.hora }

        return horariosBase.filter {
            it !in horariosOcupados
        }
    }

    fun agendarCita(cita: Cita): Boolean {

        val horarioOcupado = citas.any {
            it.medicoId == cita.medicoId &&
                    it.fecha == cita.fecha &&
                    it.hora == cita.hora
        }

        if (horarioOcupado) {
            return false
        }

        citas.add(cita)
        return true
    }

    fun citasDelUsuario(usuarioId: Int): List<Cita> {
        return citas
            .filter { it.usuarioId == usuarioId }
            .sortedWith(
                compareBy<Cita> { it.fecha }
                    .thenBy { it.hora }
            )
    }

    fun obtenerCita(id: Int): Cita? {
        return citas.find { it.id == id }
    }

    fun cancelarCita(id: Int): Boolean {
        return citas.removeIf { it.id == id }
    }
}