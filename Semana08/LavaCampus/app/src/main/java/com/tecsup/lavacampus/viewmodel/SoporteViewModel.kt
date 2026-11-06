package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*

/** La vista recibe servicios como estado; el acceso a Room queda en Repository y DAO. */
class SoporteViewModel(repository: LavaRepository) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    val servicios = repository.observarServiciosActivos().catch { e ->
        if (e is CancellationException) throw e
        _error.value = "No se pudieron cargar los servicios"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<Servicio>())
    fun limpiarError() { _error.value = null }
    class Factory(private val repository: LavaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = SoporteViewModel(repository) as T
    }
}
