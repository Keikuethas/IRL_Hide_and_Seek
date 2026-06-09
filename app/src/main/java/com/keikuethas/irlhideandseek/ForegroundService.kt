package com.keikuethas.irlhideandseek

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameLocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationJob: Job? = null

    companion object {
        // Горячий поток, за которым будет следить GameViewModel
        private val _currentLocation = MutableStateFlow<android.location.Location?>(null)
        val currentLocation: StateFlow<android.location.Location?> = _currentLocation.asStateFlow()

        private const val CHANNEL_ID = "game_location_channel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        var isRunning: Boolean = false
            private set

        fun start(context: Context) {
            val intent =
                Intent(context, GameLocationService::class.java).apply { action = ACTION_START }
            context.startForegroundService(intent)
            isRunning = true
        }

        fun stop(context: Context) {
            val intent =
                Intent(context, GameLocationService::class.java).apply { action = ACTION_STOP }
            context.startService(intent)
            isRunning = false
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Инициализируем провайдер, если это не было сделано в Application
        LocationProvider.init(this)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTracking()
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY // Просим систему перезапустить сервис, если его убьют
    }

    private fun startTracking() {
        if (locationJob?.isActive == true) return

        if (!LocationProvider.hasLocationPermission) {
            stopSelf()
            return
        }

        locationJob = serviceScope.launch {
            // ✅ Используем ваш существующий LocationProvider!
            LocationProvider.observeLocation().collectLatest { event ->
                when (event) {
                    is LocationEvent.Update -> {
                        val loc = event.location
                        _currentLocation.value = loc // Отправляем во ViewModel
                        // Кэш lastKnownLocation обновляется внутри observeLocation() автоматически
                    }

                    is LocationEvent.PermissionRevoked -> {
                        stopTracking()
                    }

                    is LocationEvent.ProvidersDisabled -> {
                        // GPS выключен пользователем. Можно отправить эффект во ViewModel, чтобы показать диалог
                    }
                }
            }
        }
    }

    private fun stopTracking() {
        locationJob?.cancel()
        locationJob = null
        _currentLocation.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopTracking()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Игровой трекер", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Отслеживает местоположение во время игры" }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Игра активна")
        .setContentText("Отслеживание местоположения включено")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Замените на свою иконку
        .setOngoing(true)
        .build()
}