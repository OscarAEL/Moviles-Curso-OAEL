package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.model.LineaCalculo
import com.tecsup.lavacampus.model.CalculoPedido
import com.tecsup.lavacampus.model.TotalesPedido
import com.tecsup.lavacampus.util.FechaUtils
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class LineaResumenItem(
    val servicio: Servicio,
    val cantidad: Double,
    val subtotal: Double
)

class NuevoPedidoViewModel(
    private val repository: LavaRepository,
    preferenciasStore: PreferenciasStore,
    val usuario: Usuario
) : ViewModel() {

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedidoCreadoExito = MutableStateFlow(false)
    val pedidoCreadoExito: StateFlow<Boolean> = _pedidoCreadoExito.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _cantidades = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val cantidades: StateFlow<Map<Int, Double>> = _cantidades.asStateFlow()

    val recibirPedidos: StateFlow<Boolean?> = preferenciasStore.recibirPedidos
        .map<Boolean, Boolean?> { it }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .onEach { lista ->
            _cargando.value = false
            val activosIds = lista.map { it.id }.toSet()
            _cantidades.update { mapActual ->
                mapActual.filterKeys { it in activosIds }
            }
        }
        .catch { e ->
            if (e is CancellationException) throw e
            _cargando.value = false
            _error.value = e.message ?: "Error al cargar servicios activos"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lineasResumen: StateFlow<List<LineaResumenItem>> = combine(serviciosActivos, _cantidades) { servicios, mapCantidades ->
        val activosMap = servicios.associateBy { it.id }
        mapCantidades.mapNotNull { (servicioId, cant) ->
            val servicio = activosMap[servicioId]
            if (servicio != null && cant > 0.0) {
                LineaResumenItem(
                    servicio = servicio,
                    cantidad = cant,
                    subtotal = CalculoPedido.importe(cant, servicio.precio)
                )
            } else null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totales = lineasResumen.map { lineas ->
        CalculoPedido.calcular(lineas.map { LineaCalculo(it.servicio.unidad, it.cantidad, it.servicio.precio) })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TotalesPedido())

    val totalCalculado: StateFlow<Double> = totales.map {
        it.total
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val fechaEstimadaIso: StateFlow<String?> = lineasResumen.map { lineas ->
        if (lineas.isEmpty()) {
            null
        } else {
            val horasMax = lineas.maxOf { it.servicio.horasEntrega }
            FechaUtils.calcularFechaEntrega(LocalDate.now(), horasMax)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun cambiarCantidad(servicioId: Int, delta: Double) {
        if (_guardando.value || _pedidoCreadoExito.value) return

        val servicio = serviciosActivos.value.find { it.id == servicioId } ?: return

        _cantidades.update { mapActual ->
            val mutableMap = mapActual.toMutableMap()
            val cantActual = mutableMap[servicioId] ?: 0.0

            val nuevaCant = if (servicio.unidad == "KG") {
                if (cantActual == 0.0 && delta > 0) {
                    1.0
                } else if (cantActual == 1.0 && delta < 0) {
                    0.0
                } else {
                    val res = (cantActual + delta).coerceIn(0.0, 10.0)
                    if (res < 1.0 && res > 0.0) 0.0 else res
                }
            } else {
                val res = (cantActual + delta).coerceIn(0.0, 30.0)
                if (res < 1.0 && res > 0.0) 0.0 else res
            }

            if (nuevaCant <= 0.0) {
                mutableMap.remove(servicioId)
            } else {
                mutableMap[servicioId] = nuevaCant
            }

            mutableMap
        }
    }

    fun actualizarNota(texto: String) {
        if (_guardando.value || _pedidoCreadoExito.value) return
        _nota.value = texto
    }

    fun confirmar() {
        if (_guardando.value || _pedidoCreadoExito.value) return

        val estaRecibiendo = recibirPedidos.value
        if (estaRecibiendo != true) {
            _error.value = if (estaRecibiendo == false) {
                "La lavandería no está recibiendo pedidos"
            } else {
                "Cargando preferencias de lavandería..."
            }
            return
        }

        val seleccionValida = validarYObtenerSeleccion()
        if (seleccionValida == null || seleccionValida.isEmpty()) {
            _error.value = "Selección de servicios no válida"
            return
        }

        _guardando.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val activosCount = repository.contarActivos(usuario.id)
                if (activosCount >= 3) {
                    _error.value = "Ya tienes 3 pedidos activos"
                    return@launch
                }

                val horasMax = seleccionValida.maxOf { it.first.horasEntrega }
                val fechaIso = FechaUtils.calcularFechaEntrega(LocalDate.now(), horasMax)

                val lineas = seleccionValida.map { (servicio, cantidad) ->
                    LineaPedido(servicioId = servicio.id, cantidad = cantidad)
                }

                repository.crearPedido(
                    usuarioId = usuario.id,
                    lineas = lineas,
                    fechaEntrega = fechaIso,
                    nota = _nota.value.trim()
                )

                _pedidoCreadoExito.value = true
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _error.value = e.message ?: "Error al registrar el pedido"
            } finally {
                _guardando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    private fun validarYObtenerSeleccion(): List<Pair<Servicio, Double>>? {
        val activosMap = serviciosActivos.value.associateBy { it.id }
        val mapCantidades = _cantidades.value

        if (mapCantidades.isEmpty()) return null

        val listaValida = mutableListOf<Pair<Servicio, Double>>()

        for ((servicioId, cant) in mapCantidades) {
            val servicio = activosMap[servicioId] ?: return null

            if (!cant.isFinite() || cant <= 0.0) return null

            if (servicio.unidad == "KG") {
                if (cant < 1.0 || cant > 10.0) return null
                val doble = cant * 2.0
                if (doble != doble.toLong().toDouble()) return null
            } else {
                if (cant < 1.0 || cant > 30.0) return null
                if (cant != cant.toLong().toDouble()) return null
            }

            listaValida.add(servicio to cant)
        }

        return if (listaValida.isNotEmpty()) listaValida else null
    }

    class Factory(
        private val repository: LavaRepository,
        private val preferenciasStore: PreferenciasStore,
        private val usuario: Usuario
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NuevoPedidoViewModel::class.java)) {
                return NuevoPedidoViewModel(repository, preferenciasStore, usuario) as T
            }
            throw IllegalArgumentException("ViewModel class no reconocida")
        }
    }
}
