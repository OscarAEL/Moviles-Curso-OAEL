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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import com.tecsup.lavacampus.util.FechaUtils
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class FiltroBandeja(val etiqueta: String, val estadoName: String?) {
    TODOS("Todos", null),
    RECIBIDO("Recibido", EstadoPedido.RECIBIDO.name),
    EN_PROCESO("En proceso", EstadoPedido.EN_PROCESO.name),
    LISTO("Listo", EstadoPedido.LISTO.name)
}

@OptIn(ExperimentalCoroutinesApi::class)
class BandejaViewModel(
    private val repository: LavaRepository,
    filtroInicialStr: String? = null
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _filtroSeleccionado = MutableStateFlow(mapearFiltro(filtroInicialStr))
    val filtroSeleccionado: StateFlow<FiltroBandeja> = _filtroSeleccionado.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val pedidosAtrasados = FechaUtils.observarHoy()
        .flatMapLatest { repository.observarPedidosAtrasados(it) }
        .map { it.toSet() }
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = "No se pudieron consultar los pedidos atrasados"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val pedidos: StateFlow<List<PedidoConDetalle>> = combine(
        repository.observarPedidos().onEach { _cargando.value = false },
        _busqueda,
        _filtroSeleccionado
    ) { lista, queryText, filtro ->
        val queryLimpio = queryText.trim().lowercase().removePrefix("#")
        val idNum = queryLimpio.toIntOrNull()

        lista.filter { item ->
            val pedido = item.pedido
            val usuario = item.usuario

            val coincideFiltro = when (filtro) {
                FiltroBandeja.TODOS -> true
                else -> pedido.estado == filtro.estadoName
            }

            val coincideBusqueda = if (queryLimpio.isEmpty()) {
                true
            } else {
                val numPedidoStr = String.format(Locale.US, "%03d", pedido.id)
                usuario.nombre.lowercase().contains(queryLimpio) ||
                usuario.codigo.lowercase().contains(queryLimpio) ||
                pedido.id.toString().contains(queryLimpio) ||
                numPedidoStr.contains(queryLimpio) ||
                (idNum != null && pedido.id == idNum)
            }

            coincideFiltro && coincideBusqueda
        }
    }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar los pedidos"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun aplicarFiltroNavegacion(filtroStr: String?) {
        _filtroSeleccionado.value = mapearFiltro(filtroStr)
    }

    fun actualizarBusqueda(query: String) {
        _busqueda.value = query
    }

    fun actualizarFiltro(filtro: FiltroBandeja) {
        _filtroSeleccionado.value = filtro
    }

    fun limpiarError() {
        _error.value = null
    }

    private fun mapearFiltro(f: String?): FiltroBandeja {
        if (f.isNullOrBlank()) return FiltroBandeja.TODOS
        return when (f.trim().uppercase()) {
            "LISTO" -> FiltroBandeja.LISTO
            "RECIBIDO" -> FiltroBandeja.RECIBIDO
            "EN_PROCESO", "EN PROCESO" -> FiltroBandeja.EN_PROCESO
            else -> FiltroBandeja.TODOS
        }
    }

    class Factory(
        private val repository: LavaRepository,
        private val filtroInicialStr: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BandejaViewModel::class.java)) {
                return BandejaViewModel(repository, filtroInicialStr) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
