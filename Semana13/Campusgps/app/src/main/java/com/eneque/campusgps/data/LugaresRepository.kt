package com.eneque.campusgps.data

import com.eneque.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
