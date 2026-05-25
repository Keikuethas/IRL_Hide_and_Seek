package com.keikuethas.irlhideandseek

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
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
    private val locationManager: LocationManager by lazy {
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    /** Вычисляемое свойство: есть ли разрешение на геолокацию */
    val hasLocationPermission: Boolean
        get() = runCatching {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }.getOrDefault(false)

    /** Кэшированная последняя локация. Thread-safe благодаря @Volatile */
    @Volatile
    var lastKnownLocation: Location? = null
        private set

    /** Инициализация. Вызывать один раз в Application.onCreate() */
    fun init(context: Context) {
        appContext = context.applicationContext
        updateLastKnownLocation()
    }

    private fun updateLastKnownLocation() {
        if (!::appContext.isInitialized) return

        // Проверяем разрешения перед вызовом
        if (!hasLocationPermission) return

        try {
            val gps: Location? = runCatching {
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }.getOrElse { null } // SecurityException

            val network: Location? = runCatching {
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }.getOrElse { null } // SecurityException

            lastKnownLocation = listOfNotNull(gps, network).maxByOrNull { it.time }
        } catch (e: SecurityException) {
            // На всякий случай ловим здесь, если разрешение отозвали между проверкой и вызовом
            lastKnownLocation = null
        }
    }

    /**
     * Поток событий геолокации.
     * Автоматически запускает/останавливает подписку на LocationManager при сборе Flow.
     */
    fun observeLocation(
        minTimeMs: Long = 2000L,
        minDistanceM: Float = 5.0f
    ): Flow<LocationEvent> = callbackFlow {
        // Проверяем разрешения до запуска
        if (!hasLocationPermission) {
            trySend(LocationEvent.PermissionRevoked)
            close()
            return@callbackFlow
        }

        val listener = LocationListener { location ->
            // Обновляем кэш
            lastKnownLocation = location
            trySend(LocationEvent.Update(location))

            // Проверяем, не отозвали ли разрешение между обновлениями
            if (!hasLocationPermission) {
                trySend(LocationEvent.PermissionRevoked)
                close()
            }
        }

        // Выбираем доступные провайдеры
        val activeProviders = listOfNotNull(
            LocationManager.GPS_PROVIDER.takeIf { locationManager.isProviderEnabled(it) },
            LocationManager.NETWORK_PROVIDER.takeIf { locationManager.isProviderEnabled(it) }
        )

        if (activeProviders.isEmpty()) {
            trySend(LocationEvent.ProvidersDisabled)
            close()
            return@callbackFlow
        }

        // Запрашиваем обновления
        activeProviders.forEach { provider ->
            try {
                locationManager.requestLocationUpdates(
                    provider,
                    minTimeMs,
                    minDistanceM,
                    listener,
                    Looper.getMainLooper() // Callbacks выполняются на main thread (легковесные)
                )
            } catch (e: SecurityException) {
                trySend(LocationEvent.PermissionRevoked)
                close()
            }
        }

        // При отмене подписки автоматически убираем слушатель
        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }
}