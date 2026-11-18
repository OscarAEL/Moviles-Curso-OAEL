package com.eneque.campusgps.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.eneque.campusgps.model.Posicion
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(context: Context) : UbicacionRepository {
    private val contexto = context.applicationContext
    private val cliente by lazy { LocationServices.getFusedLocationProviderClient(contexto) }

    override suspend fun obtenerPosicion(): Result<Posicion> {
        val permisoFino = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val permisoAproximado = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Defensa del Repository: nunca consultar Fused sin permiso.
        if (!permisoFino && !permisoAproximado) {
            return Result.failure(SecurityException("Falta el permiso de ubicación"))
        }

        return try {
            val prioridad = if (permisoFino) Priority.PRIORITY_HIGH_ACCURACY
                else Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val ubicacion = cliente.getCurrentLocation(prioridad, null).await()
            if (ubicacion != null) {
                Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
            } else {
                Result.failure(IllegalStateException("No se pudo obtener la ubicación"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
