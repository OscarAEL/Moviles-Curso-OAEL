package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _eliminando = MutableStateFlow(false)
    val eliminando: StateFlow<Boolean> = _eliminando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _exitoGuardado = MutableStateFlow(false)
    val exitoGuardado: StateFlow<Boolean> = _exitoGuardado.asStateFlow()

    private val _exitoEliminado = MutableStateFlow(false)
    val exitoEliminado: StateFlow<Boolean> = _exitoEliminado.asStateFlow()

    val servicios: StateFlow<List<Servicio>> = repository.observarServicios()
        .onEach { _cargando.value = false }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar servicios"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun alternar(servicio: Servicio) {
        viewModelScope.launch {
            try {
                repository.cambiarActivo(servicio.id, !servicio.activo)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al cambiar estado"
            }
        }
    }

    fun guardar(s: Servicio, precioInputStr: String) {
        if (_guardando.value || _eliminando.value || _exitoGuardado.value) return

        val nombreTrim = s.nombre.trim()
        if (nombreTrim.isEmpty()) {
            _error.value = "El nombre del servicio es obligatorio"
            return
        }

        val pStr = precioInputStr.trim().replace(',', '.')
        val pVal = pStr.toDoubleOrNull()
        if (pVal == null || pVal.isNaN() || pVal.isInfinite() || pVal <= 0.0) {
            _error.value = "El precio debe ser un número mayor a cero"
            return
        }

        val uUpper = s.unidad.uppercase()
        if (uUpper != "KG" && uUpper != "PRENDA" && uUpper != "UNIDAD") {
            _error.value = "La unidad debe ser Kilo (KG), Prenda o Unidad"
            return
        }

        if (s.horasEntrega != 24 && s.horasEntrega != 48 && s.horasEntrega != 72) {
            _error.value = "El tiempo de entrega debe ser 24, 48 o 72 horas"
            return
        }

        if (s.icono != "wash" && s.icono != "iron" && s.icono != "shirt") {
            _error.value = "El ícono debe ser lavadora, plancha o camisa"
            return
        }

        _guardando.value = true
        _error.value = null

        val servicioAInsertar = s.copy(
            nombre = nombreTrim,
            precio = pVal,
            unidad = uUpper
        )

        viewModelScope.launch {
            try {
                repository.guardarServicio(servicioAInsertar)
                _exitoGuardado.value = true
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al guardar el servicio"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun eliminar(servicio: Servicio) {
        if (_eliminando.value || _guardando.value || _exitoEliminado.value) return

        _eliminando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val tieneDetalles = repository.servicioTieneDetalles(servicio.id)
                if (tieneDetalles) {
                    _error.value = "Este servicio tiene pedidos asociados. Puedes desactivarlo"
                    return@launch
                }

                repository.eliminarServicio(servicio)
                _exitoEliminado.value = true
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al eliminar el servicio"
            } finally {
                _eliminando.value = false
            }
        }
    }

    fun resetearEstadoOperacion() {
        _exitoGuardado.value = false
        _exitoEliminado.value = false
    }

    fun limpiarError() {
        _error.value = null
    }

    class Factory(private val repository: LavaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ServiciosViewModel::class.java)) {
                return ServiciosViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
