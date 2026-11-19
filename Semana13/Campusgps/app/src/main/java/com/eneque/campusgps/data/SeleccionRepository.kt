package com.eneque.campusgps.data

import kotlinx.coroutines.flow.Flow

interface SeleccionRepository {
    val ultimoLugarId: Flow<Int?>
    suspend fun guardarLugar(id: Int)
}
