package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val servicios: StateFlow<List<Servicio>> = repository.observarServicios()
        .catch { e ->
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
                _error.value = e.message ?: "Error al cambiar estado"
            }
        }
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
