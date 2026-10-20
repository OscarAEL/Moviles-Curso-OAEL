package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class InicioViewModel(
    private val repository: LavaRepository,
    val usuario: Usuario
) : ViewModel() {

    val nombreEstudiante: String = usuario.nombre
    val codigoEstudiante: String = usuario.codigo

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val todosLosPedidosFlow = repository.observarPedidosDeUsuario(usuario.id)
        .onEach { _cargando.value = false }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar pedidos del estudiante"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pedidoActivo: StateFlow<PedidoConDetalle?> = todosLosPedidosFlow
        .map { lista ->
            lista.firstOrNull { item ->
                item.pedido.estado == EstadoPedido.RECIBIDO.name ||
                item.pedido.estado == EstadoPedido.EN_PROCESO.name ||
                item.pedido.estado == EstadoPedido.LISTO.name
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recientes: StateFlow<List<PedidoConDetalle>> = todosLosPedidosFlow
        .map { lista ->
            lista.take(2)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun limpiarError() {
        _error.value = null
    }

    class Factory(
        private val repository: LavaRepository,
        private val usuario: Usuario
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InicioViewModel::class.java)) {
                return InicioViewModel(repository, usuario) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
