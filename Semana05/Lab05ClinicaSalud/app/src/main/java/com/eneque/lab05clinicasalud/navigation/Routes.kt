package com.eneque.lab05clinicasalud.navigation

object Routes {
    const val HOME = "inicio"
    const val PERFIL_MEDICO = "perfil_medico/{medicoId}"

    fun perfilMedico(medicoId: Int): String {
        return "perfil_medico/$medicoId"
    }
}
