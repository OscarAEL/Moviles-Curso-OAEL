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

class MisPedidosViewModel(
    private val repository: LavaRepository,
    val estudianteId: Int
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cancelando = MutableStateFlow(false)
    val cancelando: StateFlow<Boolean> = _cancelando.asStateFlow()

    private val basePedidosFlow: StateFlow<List<PedidoConDetalle>> = repository.observarPedidosDeUsuario(estudianteId)
        .onEach { _cargando.value = false }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al obtener pedidos"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activos: StateFlow<List<PedidoConDetalle>> = basePedidosFlow
        .map { lista ->
            lista.filter { item ->
                item.pedido.estado == EstadoPedido.RECIBIDO.name ||
                item.pedido.estado == EstadoPedido.EN_PROCESO.name ||
                item.pedido.estado == EstadoPedido.LISTO.name
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historial: StateFlow<List<PedidoConDetalle>> = basePedidosFlow
        .map { lista ->
            lista.filter { item ->
                item.pedido.estado == EstadoPedido.ENTREGADO.name ||
                item.pedido.estado == EstadoPedido.CANCELADO.name
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun puedeCancelar(item: PedidoConDetalle): Boolean {
        return item.pedido.usuarioId == estudianteId && item.pedido.estado == EstadoPedido.RECIBIDO.name
    }

    fun cancelar(item: PedidoConDetalle) {
        if (_cancelando.value) return

        val pedido = item.pedido
        if (pedido.usuarioId != estudianteId || pedido.estado != EstadoPedido.RECIBIDO.name) {
            _error.value = "Solo el estudiante propietario puede cancelar un pedido en estado RECIBIDO"
            return
        }

        _cancelando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val actual = repository.observarPedido(pedido.id).first()
                if (actual == null || actual.pedido.usuarioId != estudianteId || actual.pedido.estado != EstadoPedido.RECIBIDO.name) {
                    _error.value = "El pedido no se encuentra disponible o su estado ya cambió"
                    return@launch
                }
                repository.cambiarEstado(pedido.id, EstadoPedido.CANCELADO, actual.pedido.casillero)
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
        private val estudianteId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MisPedidosViewModel::class.java)) {
                return MisPedidosViewModel(repository, estudianteId) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
