package com.eneque.avisoscampus.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import com.eneque.avisoscampus.model.Origen
import androidx.lifecycle.ViewModel
import com.eneque.avisoscampus.data.AvisosRepository
import com.eneque.avisoscampus.model.Aviso
import com.eneque.avisoscampus.model.PermisoUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {
    val avisos = repo.avisos
    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos = _eventos.asSharedFlow()
    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso = _permiso.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()
    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan = _faltan.asStateFlow()

    fun recordarEn(segundos: Int = 10) {
        if (trabajo?.isActive == true) return
        require(segundos > 0)
        trabajo = viewModelScope.launch {
            try {
                for (segundo in segundos downTo 1) {
                    _faltan.value = segundo
                    delay(1000)
                }
                val aviso = repo.registrar(
                    "Recordatorio", "Revisa tu laboratorio pendiente", Origen.RECORDATORIO
                )
                _eventos.emit(aviso)
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
            val aviso = repo.registrar("Tarea lista", "Tu entrega fue registrada", Origen.BOTON)
            _eventos.emit(aviso)
        }
    }

    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
    }
}
