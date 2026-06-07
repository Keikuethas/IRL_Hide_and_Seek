package com.keikuethas.irlhideandseek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class LocationEvent {
    data class Update(val location: Location) : LocationEvent()
    object PermissionRevoked : LocationEvent()
    object ProvidersDisabled : LocationEvent()
}

object LocationProvider {
    private lateinit var appContext: Context
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /** Вычисляемое свойство: есть ли разрешение на геолокацию */
    val hasLocationPermission: Boolean
        get() = runCatching {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        appContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        }.getOrDefault(false)

    /** Кэшированная последняя локация */
    @Volatile
    var lastKnownLocation: Location? = null
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        updateLastKnownLocation()
    }

    @SuppressLint("MissingPermission")
    private fun updateLastKnownLocation() {
        if (!::appContext.isInitialized) return

        // Проверяем разрешения перед вызовом
        if (!hasLocationPermission) return

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    lastKnownLocation = location
                }
                .addOnFailureListener {
                    lastKnownLocation = null
                }
        } catch (e: SecurityException) {
            // На всякий случай ловим здесь, если разрешение отозвали между проверкой и вызовом
            lastKnownLocation = null
        }
    }

    /**
     * Поток событий геолокации.
     * Автоматически запускает/останавливает подписку на FusedLocationProvider при сборе Flow.
     */
    @SuppressLint("MissingPermission")
    fun observeLocation(
        minTimeMs: Long = 2000L,
        minDistanceM: Float = 5.0f,
        priority: Int = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    ): Flow<LocationEvent> = callbackFlow {
        // Проверяем разрешения до запуска
        if (!hasLocationPermission) {
            trySend(LocationEvent.PermissionRevoked)
            close()
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(priority, minTimeMs)
            .setMinUpdateIntervalMillis(minTimeMs / 2)
            .setMinUpdateDistanceMeters(minDistanceM)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return

                // Обновляем кэш
                lastKnownLocation = location
                trySend(LocationEvent.Update(location))

                // Проверяем, не отозвали ли разрешение между обновлениями
                if (!hasLocationPermission) {
                    trySend(LocationEvent.PermissionRevoked)
                    close()
                }
            }

            override fun onLocationAvailability(availability: com.google.android.gms.location.LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    trySend(LocationEvent.ProvidersDisabled)
                }
            }
        }

        // Запрашиваем обновления
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper() // Callbacks выполняются на main thread (легковесные)
            )
        } catch (e: SecurityException) {
            trySend(LocationEvent.PermissionRevoked)
            close()
            return@callbackFlow
        }

        // При отмене подписки автоматически убираем слушатель
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}