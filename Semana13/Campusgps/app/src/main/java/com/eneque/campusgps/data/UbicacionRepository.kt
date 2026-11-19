package com.eneque.campusgps.data

import com.eneque.campusgps.model.Posicion

interface UbicacionRepository {
    fun playServicesDisponible(): Boolean
    suspend fun obtenerPosicion(): Result<Posicion>
}

// Fallos del Repository, sin depender de tipos de presentación.
class UbicacionNoDisponibleException : Exception("Ubicación no disponible")
class PlayServicesNoDisponibleException : Exception("Google Play Services no disponible")
