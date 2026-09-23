package com.eneque.lab05clinicasalud.navigation

object Routes {
    const val HOME = "inicio"
    const val PERFIL_MEDICO = "perfil_medico/{medicoId}"
    const val AGENDAR_CITA = "agendar_cita/{medicoId}"

    fun perfilMedico(medicoId: Int): String {
        return "perfil_medico/$medicoId"
    }

    fun agendarCita(medicoId: Int): String {
        return "agendar_cita/$medicoId"
    }
}
