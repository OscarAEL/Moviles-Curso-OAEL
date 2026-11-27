package com.eneque.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eneque.avisoscampus.data.AvisosRepository
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.Origen
import com.eneque.avisoscampus.model.PermisoUi
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {
    val avisos = repo.avisos
    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos = _eventos.asSharedFlow()
    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso = _permiso.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private val _necesitaAjustes = MutableStateFlow(false)
    val necesitaAjustes = _necesitaAjustes.asStateFlow()
    private val _escuchandoAvion = MutableStateFlow(false)
    val escuchandoAvion = _escuchandoAvion.asStateFlow()
    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan = _faltan.asStateFlow()

    fun cambiarEscucha(valor: Boolean) {
        _escuchandoAvion.value = valor
    }

    fun alCambiarModoAvion(activo: Boolean) {
        if (!_escuchandoAvion.value) return
        viewModelScope.launch {
            registrarYEmitir("Modo avión", if (activo) "Activado" else "Desactivado", Origen.MODO_AVION)
        }
    }

    fun recordarEn(segundos: Int = 10) {
        if (trabajo?.isActive == true) return
        require(segundos > 0)
        trabajo = viewModelScope.launch {
            try {
                for (segundo in segundos downTo 1) {
                    _faltan.value = segundo
                    delay(1000)
                }
                registrarYEmitir("Recordatorio", "Revisa tu laboratorio pendiente", Origen.RECORDATORIO)
            } finally {
                _faltan.value = null
            }
        }
    }

    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }

    fun avisarAhora() {
        viewModelScope.launch {
            registrarYEmitir(
                "Tarea lista",
                "Tu entrega fue registrada. Aviso ficticio del campus: recuerda revisar los requisitos " +
                    "del laboratorio, comprobar las capturas solicitadas y preparar la explicación de " +
                    "tu arquitectura MVVM para la próxima sesión.",
                Origen.BOTON
            )
        }
    }

    private suspend fun registrarYEmitir(titulo: String, texto: String, origen: Origen) {
        try {
            val aviso = repo.registrar(titulo, texto, origen)
            _mensaje.value = null
            _necesitaAjustes.value = false
            _eventos.emit(aviso)
        } catch (_: IOException) {
            _mensaje.value = "No se pudo guardar el aviso. Inténtalo de nuevo."
            _necesitaAjustes.value = false
        }
    }

    fun noSePudoMostrar() {
        _mensaje.value = SIN_PERMISO
        _necesitaAjustes.value = true
    }

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
        if (concedido) {
            if (_mensaje.value == SIN_PERMISO) _mensaje.value = null
            _necesitaAjustes.value = false
        }
    }

    private companion object {
        const val SIN_PERMISO = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
    }
}
