package com.eneque.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eneque.campusgps.data.LugaresRepository
import com.eneque.campusgps.data.UbicacionRepository
import com.eneque.campusgps.data.SeleccionRepository

class CampusViewModelFactory(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val seleccionRepo: SeleccionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            "ViewModel no soportado: ${modelClass.name}"
        }
        @Suppress("UNCHECKED_CAST")
        return MapaViewModel(ubicacionRepo, lugaresRepo, seleccionRepo) as T
    }
}
