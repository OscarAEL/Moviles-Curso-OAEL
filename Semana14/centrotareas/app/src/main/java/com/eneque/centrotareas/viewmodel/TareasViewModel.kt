package com.eneque.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import com.eneque.centrotareas.data.TareasRepository
import com.eneque.centrotareas.model.CargaUi
import com.eneque.centrotareas.model.PrimosUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Paso 1: bloqueo intencional para observar el problema del hilo principal.
    // Se reemplaza por una corrutina en el paso 2.
    fun tareaLargaMala() {
        _mensaje.value = "Trabajando..."
        Thread.sleep(4000)
        _mensaje.value = "Terminó"
    }

    // TODO Paso 2: reemplazar por tareaLarga() con corrutinas.
    // TODO Paso 3: exponer el progreso.
    // TODO Paso 4: agregar Job, corriendo y cancelar().

    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    // TODO Paso 5: contar(hasta) y cancelarPrimos().

    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    // TODO Paso 6: cargarUnaPorUna() y cargarALaVez().
    // TODO Paso 7: simularError y manejo de errores.
}
