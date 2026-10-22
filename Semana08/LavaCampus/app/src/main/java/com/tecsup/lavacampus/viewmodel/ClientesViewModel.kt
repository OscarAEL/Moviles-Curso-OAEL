package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class ClientesViewModel(
    repository: LavaRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val clientes: StateFlow<List<ClienteResumen>> = combine(
        repository.observarClientes().onEach { _cargando.value = false },
        _busqueda
    ) { lista, queryText ->
        val queryLimpio = queryText.trim().lowercase()
        if (queryLimpio.isEmpty()) {
            lista
        } else {
            lista.filter { item ->
                item.nombre.lowercase().contains(queryLimpio) ||
                item.codigo.lowercase().contains(queryLimpio) ||
                item.correo.lowercase().contains(queryLimpio)
            }
        }
    }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar la lista de clientes"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
    }

    fun limpiarError() {
        _error.value = null
    }

    class Factory(private val repository: LavaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClientesViewModel::class.java)) {
                return ClientesViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
