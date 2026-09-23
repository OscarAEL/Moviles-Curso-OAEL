package com.eneque.lab05clinicasalud.navigation

object Routes {
    const val HOME = "inicio"
    const val PERFIL_MEDICO = "perfil_medico/{medicoId}"
    const val AGENDAR_CITA = "agendar_cita/{medicoId}"
    const val CONFIRMACION_CITA = "confirmacion_cita/{medicoId}/{fecha}/{hora}"
    const val MIS_CITAS = "mis_citas"

    fun perfilMedico(medicoId: Int): String {
        return "perfil_medico/$medicoId"
    }

    fun agendarCita(medicoId: Int): String {
        return "agendar_cita/$medicoId"
    }

    fun confirmacionCita(medicoId: Int, fecha: String, hora: String): String {
        return "confirmacion_cita/$medicoId/$fecha/$hora"
    }
}
