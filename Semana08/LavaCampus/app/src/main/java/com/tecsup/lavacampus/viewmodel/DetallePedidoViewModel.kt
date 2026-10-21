package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetallePedidoViewModel(
    private val repository: LavaRepository,
    val pedidoId: Int,
    val estudianteId: Int
) : ViewModel() {

    private val _pedido = MutableStateFlow<PedidoConDetalle?>(null)
    val pedido: StateFlow<PedidoConDetalle?> = _pedido.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _cancelando = MutableStateFlow(false)
    val cancelando: StateFlow<Boolean> = _cancelando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedidoNoDisponible = MutableStateFlow(false)
    val pedidoNoDisponible: StateFlow<Boolean> = _pedidoNoDisponible.asStateFlow()

    val puedeCancelar: StateFlow<Boolean> = _pedido.map { item ->
        item != null && item.pedido.usuarioId == estudianteId && item.pedido.estado == EstadoPedido.RECIBIDO.name
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        observarPedido()
    }

    private fun observarPedido() {
        viewModelScope.launch {
            repository.observarPedido(pedidoId)
                .onEach { _cargando.value = false }
                .catch { e ->
                    if (e is CancellationException) throw e
                    _cargando.value = false
                    _error.value = e.message ?: "Error al cargar el detalle del pedido"
                }
                .collect { item ->
                    if (item == null || item.pedido.usuarioId != estudianteId) {
                        _pedido.value = null
                        _pedidoNoDisponible.value = true
                    } else {
                        _pedido.value = item
                        _pedidoNoDisponible.value = false
                    }
                }
        }
    }

    fun cancelar() {
        if (_cancelando.value || !puedeCancelar.value) return

        _cancelando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val itemActual = repository.observarPedido(pedidoId).first()
                if (itemActual == null || itemActual.pedido.usuarioId != estudianteId || itemActual.pedido.estado != EstadoPedido.RECIBIDO.name) {
                    _error.value = "El pedido no se encuentra disponible o su estado ya cambió"
                    return@launch
                }

                repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, itemActual.pedido.casillero)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al cancelar el pedido"
            } finally {
                _cancelando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    class Factory(
        private val repository: LavaRepository,
        private val pedidoId: Int,
        private val estudianteId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetallePedidoViewModel::class.java)) {
                return DetallePedidoViewModel(repository, pedidoId, estudianteId) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
