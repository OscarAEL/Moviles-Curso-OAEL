package com.eneque.campusgps.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.eneque.campusgps.model.Posicion
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(context: Context) : UbicacionRepository {
    private val contexto = context.applicationContext
    private val cliente by lazy { LocationServices.getFusedLocationProviderClient(contexto) }

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(contexto) ==
            ConnectionResult.SUCCESS

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
            if (!playServicesDisponible()) {
                return Result.failure(PlayServicesNoDisponibleException())
            }
            val gestor = contexto.getSystemService(LocationManager::class.java)
            if (gestor == null || !LocationManagerCompat.isLocationEnabled(gestor)) {
                return Result.failure(UbicacionNoDisponibleException())
            }
            val prioridad = if (permisoFino) Priority.PRIORITY_HIGH_ACCURACY
                else Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val ubicacion = cliente.getCurrentLocation(prioridad, null).await()
            if (ubicacion != null) {
                Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
            } else {
                Result.failure(UbicacionNoDisponibleException())
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
