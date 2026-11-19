package com.eneque.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eneque.campusgps.data.LugaresRepository
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.data.UbicacionNoDisponibleException
import com.eneque.campusgps.data.PlayServicesNoDisponibleException
import com.eneque.campusgps.model.TipoError
import com.eneque.campusgps.model.Lugar
import com.eneque.campusgps.model.LugarConDistancia
import com.eneque.campusgps.model.Posicion
import com.eneque.campusgps.util.distanciaMetros
import com.eneque.campusgps.util.crearContornoRadio
import com.eneque.campusgps.util.estaDentroDelRadio
import com.eneque.campusgps.model.UbicacionUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    val categorias: List<String> = _lugares.value.map { it.lugar.categoria }.distinct()
    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada.asStateFlow()

    val lugaresFiltrados: StateFlow<List<LugarConDistancia>> =
        combine(lugares, categoriaSeleccionada) { lista, categoria ->
            if (categoria == null) lista
            else lista.filter { it.lugar.categoria == categoria }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    val radiosDisponibles: List<Int> = listOf(100, 200, 500)
    private val _radioMetros = MutableStateFlow(100)
    val radioMetros: StateFlow<Int> = _radioMetros.asStateFlow()

    val lugaresDentroDelRadio: StateFlow<Set<Int>> =
        combine(lugaresFiltrados, radioMetros) { lista, radio ->
            lista.filter { estaDentroDelRadio(it.metros, radio) }
                .map { it.lugar.id }
                .toSet()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val contornoRadio: StateFlow<List<Posicion>> =
        combine(estado, radioMetros) { estadoActual, radio ->
            val posicion = (estadoActual as? UbicacionUi.Lista)?.posicion
            if (posicion == null) emptyList() else crearContornoRadio(posicion, radio)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarRadio(metros: Int) {
        if (metros in radiosDisponibles) _radioMetros.value = metros
    }

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    fun buscar() {
        if (_estado.value == UbicacionUi.Buscando) return
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = errorDe(PlayServicesNoDisponibleException())
            return
        }
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it)
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    private fun actualizarDistancias(pos: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { lugar -> LugarConDistancia(lugar, distanciaMetros(pos, lugar)) }
            .sortedBy { it.metros }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    fun filtrarPorCategoria(categoria: String?) {
        if (categoria != null && categoria !in categorias) return
        _categoriaSeleccionada.value = categoria
        val lugarSeleccionado = _lugares.value.firstOrNull { it.lugar.id == _seleccionado.value }
        if (categoria != null && lugarSeleccionado?.lugar?.categoria != categoria) {
            _seleccionado.value = null
        }
    }

    val masCercano: StateFlow<Lugar?> = lugaresFiltrados
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás",
            TipoError.SIN_PERMISO
        )
        is UbicacionNoDisponibleException -> UbicacionUi.Error(
            "Activa el GPS e intenta de nuevo",
            TipoError.GPS_APAGADO
        )
        is PlayServicesNoDisponibleException -> UbicacionUi.Error(
            "Este dispositivo no tiene Google Play Services",
            TipoError.SIN_PLAY_SERVICES
        )
        else -> UbicacionUi.Error(
            "No se pudo obtener la ubicación. Intenta de nuevo",
            TipoError.OTRO
        )
    }
}
