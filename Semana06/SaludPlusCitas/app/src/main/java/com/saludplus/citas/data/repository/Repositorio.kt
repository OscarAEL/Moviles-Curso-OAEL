package com.saludplus.citas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario

// Patrón Singleton (object): actúa como repositorio centralizado en memoria para toda la app.
object Repositorio {

    // -------------------------
    // USUARIOS
    // -------------------------

    // Lista observable de usuarios registrados en la sesión actual.
    val usuarios = mutableStateListOf(
        Usuario(
            id = 1,
            nombre = "Juan Pérez",
            telefono = "987654321",
            correo = "juan@correo.com",
            contrasena = "123456"
        )
    )

    // Almacena el usuario con sesión activa en la aplicación.
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Registra un nuevo usuario si el correo no ha sido registrado previamente.
    fun registrarUsuario(usuario: Usuario): Boolean {
        val correoExiste = usuarios.any { it.correo == usuario.correo }

        if (correoExiste) {
            return false
        }

        usuarios.add(usuario)
        usuarioActual = usuario
        return true
    }

    // Valida credenciales e inicia la sesión del usuario si coincide el correo y contraseña.
    fun iniciarSesion(correo: String, contrasena: String): Boolean {
        val usuario = usuarios.find {
            it.correo == correo && it.contrasena == contrasena
        }

        usuarioActual = usuario
        return usuario != null
    }

    // Cierra la sesión activa borrando el usuario actual.
    fun cerrarSesion() {
        usuarioActual = null
    }


    // -------------------------
    // ESPECIALIDADES
    // -------------------------

    // Lista estática de especialidades médicas disponibles.
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

    // Filtra las especialidades por el texto ingresado en el buscador.
    fun buscarEspecialidades(texto: String): List<Especialidad> {
        return especialidades.filter {
            it.nombre.contains(texto, ignoreCase = true)
        }
    }

    // Retorna las primeras especialidades para la sección destacada de Inicio.
    fun especialidadesDestacadas(): List<Especialidad> {
        return especialidades.take(3)
    }

    // Busca una especialidad por su ID único.
    fun obtenerEspecialidad(id: Int): Especialidad? {
        return especialidades.find { it.id == id }
    }


    // -------------------------
    // MÉDICOS
    // -------------------------

    // Lista de médicos del sistema asignados a cada especialidad.
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

    // Filtra y ordena médicos por calificación para una especialidad dada.
    fun medicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return medicos
            .filter { it.especialidadId == especialidadId }
            .sortedByDescending { it.calificacion }
    }

    // Busca médicos dentro de una especialidad que coincidan con la búsqueda.
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

    // Busca un médico específico por su ID.
    fun obtenerMedico(id: Int): Medico? {
        return medicos.find { it.id == id }
    }


    // -------------------------
    // CITAS
    // -------------------------

    // Lista observable que almacena las citas agendadas durante la ejecución.
    val citas = mutableStateListOf<Cita>()

    // Lista base de horarios disponibles en la clínica.
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

    // Retorna los horarios que aún no han sido reservados para un médico y fecha.
    fun horariosDisponibles(
        medicoId: Int,
        fecha: String
    ): List<String> {

        // Extrae las horas ya ocupadas para la fecha y médico elegidos.
        val horariosOcupados = citas
            .filter {
                it.medicoId == medicoId &&
                        it.fecha == fecha
            }
            .map { it.hora }

        // Filtra y excluye los horarios ocupados.
        return horariosBase.filter {
            it !in horariosOcupados
        }
    }

    // Guarda una nueva cita si el horario ingresado se encuentra libre.
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

    // Retorna las citas asociadas a un usuario ordenadas por fecha y hora.
    fun citasDelUsuario(usuarioId: Int): List<Cita> {
        return citas
            .filter { it.usuarioId == usuarioId }
            .sortedWith(
                compareBy<Cita> { it.fecha }
                    .thenBy { it.hora }
            )
    }

    // Obtiene el detalle de una cita específica por su ID.
    fun obtenerCita(id: Int): Cita? {
        return citas.find { it.id == id }
    }

    // Cancela y remueve una cita de la lista.
    fun cancelarCita(id: Int): Boolean {
        return citas.removeIf { it.id == id }
    }
}
