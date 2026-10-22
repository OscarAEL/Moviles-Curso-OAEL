package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AjustesUi(val oscuro: Boolean = false, val avisos: Boolean = true, val recibir: Boolean = true)
data class EstadisticasPerfil(val activos: Int, val totales: Int, val kilos: Double)

class PreferenciasViewModel(
    repository: LavaRepository,
    private val store: PreferenciasStore,
    usuarioId: Int
) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    private val _guardando = MutableStateFlow(false)
    val guardando = _guardando.asStateFlow()

    val ajustes = combine(store.modoOscuro, store.notificaciones, store.recibirPedidos) { oscuro, avisos, recibir ->
        AjustesUi(oscuro, avisos, recibir)
    }.catch { e ->
        if (e is CancellationException) throw e
        _error.value = "No se pudieron leer las preferencias"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AjustesUi())

    val estadisticas = repository.observarPedidosDeUsuario(usuarioId).map { pedidos ->
        EstadisticasPerfil(
            activos = pedidos.count { it.pedido.estado in setOf("RECIBIDO", "EN_PROCESO", "LISTO") },
            totales = pedidos.size,
            kilos = pedidos.filter { it.pedido.estado == "ENTREGADO" }.sumOf { pedido ->
                pedido.detalles.filter { it.servicio.unidad == "KG" }.sumOf { it.detalle.cantidad }
            }
        )
    }.catch { e ->
        if (e is CancellationException) throw e
        _error.value = "No se pudieron cargar las estadísticas"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun guardar(operacion: suspend () -> Unit) {
        if (_guardando.value) return
        _guardando.value = true
        viewModelScope.launch {
            try { operacion() }
            catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = "No se pudo guardar la preferencia"
            } finally { _guardando.value = false }
        }
    }

    fun modoOscuro(valor: Boolean) = guardar { store.guardarModoOscuro(valor) }
    fun notificaciones(valor: Boolean) = guardar { store.guardarNotificaciones(valor) }
    fun recibirPedidos(valor: Boolean) = guardar { store.guardarRecibirPedidos(valor) }
    fun limpiarError() { _error.value = null }

    class Factory(private val repository: LavaRepository, private val store: PreferenciasStore, private val usuarioId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(PreferenciasViewModel::class.java))
            return PreferenciasViewModel(repository, store, usuarioId) as T
        }
    }
}
