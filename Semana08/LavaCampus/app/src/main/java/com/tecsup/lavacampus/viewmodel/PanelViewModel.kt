package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.DatoGrafico
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import com.tecsup.lavacampus.util.FechaUtils
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PanelViewModel(
    repository: LavaRepository
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val atrasados = FechaUtils.observarHoy()
        .flatMapLatest { repository.observarPedidosAtrasados(it) }
        .map { it.size }
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = "No se pudieron contar los pedidos atrasados"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val fechaHoyStr: String = LocalDate.now().toString()
    val fechaInicioSemanaStr: String = LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .toString()

    val pedidosHoy: StateFlow<Int> = repository.observarPedidosDeFecha(fechaHoyStr)
        .onEach { _cargando.value = false }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar pedidos de hoy"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val enProceso: StateFlow<Int> = repository.observarCantidadPorEstado(EstadoPedido.EN_PROCESO)
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar pedidos en proceso"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val porRecoger: StateFlow<Int> = repository.observarCantidadPorEstado(EstadoPedido.LISTO)
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar pedidos por recoger"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val ingresosSemana: StateFlow<Double> = repository.observarIngresosDesde(fechaInicioSemanaStr)
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar ingresos de la semana"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val pedidosPagadosSemana: StateFlow<Int> = repository.observarPedidos()
        .map { lista ->
            lista.count { item ->
                item.pedido.pagado && item.pedido.fechaRegistro >= fechaInicioSemanaStr
            }
        }
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al calcular pedidos pagados"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        repository.observarCantidadPorEstado(EstadoPedido.RECIBIDO),
        repository.observarCantidadPorEstado(EstadoPedido.EN_PROCESO),
        repository.observarCantidadPorEstado(EstadoPedido.LISTO),
        repository.observarCantidadPorEstado(EstadoPedido.ENTREGADO)
    ) { recibido, enProcesoCount, listo, entregado ->
        listOf(
            DatoGrafico("Recibido", recibido.toDouble()),
            DatoGrafico("En proceso", enProcesoCount.toDouble()),
            DatoGrafico("Listo", listo.toDouble()),
            DatoGrafico("Entregado", entregado.toDouble())
        )
    }
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar resumen por estado"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val porServicio: StateFlow<List<DatoGrafico>> = repository.observarIngresosPorServicio(fechaInicioSemanaStr)
        .map { lista ->
            lista.map { item ->
                DatoGrafico(
                    etiqueta = item.nombre,
                    valor = item.total
                )
            }
        }
        .catch { e ->
            if (e is CancellationException) throw e
            _error.value = e.message ?: "Error al cargar ingresos por servicio"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun limpiarError() {
        _error.value = null
    }

    class Factory(private val repository: LavaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PanelViewModel::class.java)) {
                return PanelViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
