package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Casilleros
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestionPedidoViewModel(
    private val repository: LavaRepository,
    val pedidoId: Int
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _operando = MutableStateFlow(false)
    val operando: StateFlow<Boolean> = _operando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _exitoOperacion = MutableStateFlow(false)
    val exitoOperacion: StateFlow<Boolean> = _exitoOperacion.asStateFlow()

    private val _casilleroElegido = MutableStateFlow<String?>(null)
    val casilleroElegido: StateFlow<String?> = _casilleroElegido.asStateFlow()

    val pedido: StateFlow<PedidoConDetalle?> = repository.observarPedido(pedidoId)
        .onEach { item ->
            _cargando.value = false
            if (item != null && _casilleroElegido.value == null && !item.pedido.casillero.isNullOrEmpty()) {
                _casilleroElegido.value = item.pedido.casillero
            }
        }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar el detalle del pedido"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val casillerosOcupados: StateFlow<List<String>> = repository.observarCasillerosOcupados()
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar casilleros ocupados"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun elegirCasillero(casillero: String) {
        if (_operando.value) return
        val actualItem = pedido.value ?: return
        val pedidoActual = actualItem.pedido

        if (pedidoActual.estado == EstadoPedido.LISTO.name ||
            pedidoActual.estado == EstadoPedido.ENTREGADO.name ||
            pedidoActual.estado == EstadoPedido.CANCELADO.name) {
            return
        }

        if (!Casilleros.todos.contains(casillero)) {
            _error.value = "Casillero inválido"
            return
        }

        val ocupados = casillerosOcupados.value
        if (ocupados.contains(casillero) && casillero != pedidoActual.casillero) {
            _error.value = "El casillero $casillero ya se encuentra ocupado"
            return
        }

        _casilleroElegido.value = casillero
    }

    fun avanzar() {
        if (_operando.value) return

        val itemMostrado = pedido.value ?: return
        val estadoCapturadoStr = itemMostrado.pedido.estado
        val estadoCapturadoEnum = try {
            EstadoPedido.valueOf(estadoCapturadoStr)
        } catch (_: Exception) {
            return
        }

        val siguienteCapturado = estadoCapturadoEnum.siguiente() ?: return

        _operando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val actual = repository.observarPedido(pedidoId).first()
                if (actual == null) {
                    _error.value = "El pedido ya no existe"
                    return@launch
                }

                if (actual.pedido.estado != estadoCapturadoStr) {
                    _error.value = "El estado del pedido cambió y no coincide con la acción solicitada"
                    return@launch
                }

                var casilleroAAsignar = actual.pedido.casillero

                if (siguienteCapturado == EstadoPedido.LISTO) {
                    val casilleroSel = _casilleroElegido.value
                    if (casilleroSel.isNullOrBlank()) {
                        _error.value = "Debes seleccionar un casillero para el recojo"
                        return@launch
                    }
                    if (!Casilleros.todos.contains(casilleroSel)) {
                        _error.value = "Formato de casillero inválido. Debe ser de C-01 a C-30"
                        return@launch
                    }
                    val ocupadosActuales = repository.observarCasillerosOcupados().first()
                    if (ocupadosActuales.contains(casilleroSel) && actual.pedido.casillero != casilleroSel) {
                        _error.value = "El casillero $casilleroSel ya se encuentra ocupado por otro pedido"
                        return@launch
                    }
                    casilleroAAsignar = casilleroSel
                }

                if (siguienteCapturado == EstadoPedido.ENTREGADO) {
                    if (!actual.pedido.pagado) {
                        _error.value = "No se puede entregar un pedido sin cobrar. Marca el pedido como cobrado primero."
                        return@launch
                    }
                }

                repository.cambiarEstado(pedidoId, siguienteCapturado, casilleroAAsignar)
                _exitoOperacion.value = true
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al actualizar el estado del pedido"
            } finally {
                _operando.value = false
            }
        }
    }

    fun cancelar() {
        if (_operando.value) return

        _operando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val actual = repository.observarPedido(pedidoId).first()
                if (actual == null) {
                    _error.value = "El pedido ya no existe"
                    return@launch
                }

                val estadoStr = actual.pedido.estado
                if (estadoStr != EstadoPedido.RECIBIDO.name && estadoStr != EstadoPedido.EN_PROCESO.name) {
                    _error.value = "Solo se pueden cancelar pedidos en estado Recibido o En proceso"
                    return@launch
                }

                repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, actual.pedido.casillero)
                _exitoOperacion.value = true
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al cancelar el pedido"
            } finally {
                _operando.value = false
            }
        }
    }

    fun marcarPagado(pagado: Boolean) {
        if (_operando.value) return

        _operando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val actual = repository.observarPedido(pedidoId).first()
                if (actual == null) {
                    _error.value = "El pedido ya no existe"
                    return@launch
                }

                val estadoStr = actual.pedido.estado
                if (estadoStr == EstadoPedido.ENTREGADO.name || estadoStr == EstadoPedido.CANCELADO.name) {
                    _error.value = "No se puede cambiar el cobro de un pedido finalizado"
                    return@launch
                }

                repository.marcarPagado(pedidoId, pagado)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al actualizar estado de cobro"
            } finally {
                _operando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    class Factory(
        private val repository: LavaRepository,
        private val pedidoId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GestionPedidoViewModel::class.java)) {
                return GestionPedidoViewModel(repository, pedidoId) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
