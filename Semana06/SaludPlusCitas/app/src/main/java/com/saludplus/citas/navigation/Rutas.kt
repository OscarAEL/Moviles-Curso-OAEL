package com.saludplus.citas.navigation

// Objeto que define las constantes y generadores de rutas para Navigation Compose.
object Rutas {
    // Rutas estáticas del flujo de autenticación e inicio
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val HOME = "home"

    // Rutas del flujo de agendamiento con parámetros dinámicos
    const val ESPECIALIDADES = "especialidades"
    const val MEDICOS = "medicos/{especialidadId}" // Recibe el ID de la especialidad seleccionada
    const val FECHA_HORA = "fecha_hora/{medicoId}" // Recibe el ID del médico seleccionado
    const val CONFIRMAR_CITA = "confirmar_cita/{medicoId}/{fecha}/{hora}" // Parámetros para la confirmación
    const val CITA_EXITOSA = "cita_exitosa/{citaId}" // Recibe el ID de la cita creada

    // Rutas de gestión de citas
    const val MIS_CITAS = "mis_citas"
    const val DETALLE_CITA = "detalle_cita/{citaId}" // Recibe el ID de la cita a consultar

    // Otras rutas de la aplicación
    const val PERFIL = "perfil"
    const val RESULTADOS = "resultados"
    const val NOTIFICACIONES = "notificaciones"
    const val TERMINOS = "terminos"

    // Helper para construir la ruta hacia la lista de médicos de una especialidad
    fun medicos(especialidadId: Int): String {
        return "medicos/$especialidadId"
    }

    // Helper para construir la ruta hacia la selección de fecha y hora de un médico
    fun fechaHora(medicoId: Int): String {
        return "fecha_hora/$medicoId"
    }

    // Helper para construir la ruta de confirmación enviando datos de la cita
    fun confirmarCita(
        medicoId: Int,
        fecha: String,
        hora: String
    ): String {
        return "confirmar_cita/$medicoId/$fecha/$hora"
    }

    // Helper para construir la ruta hacia el detalle de una cita por su ID
    fun detalleCita(citaId: Int): String {
        return "detalle_cita/$citaId"
    }

    // Helper para construir la ruta del comprobante de éxito al agendar
    fun citaExitosa(citaId: Int): String {
        return "cita_exitosa/$citaId"
    }
}
