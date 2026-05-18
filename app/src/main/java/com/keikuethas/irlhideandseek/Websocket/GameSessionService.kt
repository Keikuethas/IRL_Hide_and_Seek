package com.keikuethas.irlhideandseek.Websocket

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.keikuethas.irlhideandseek.LocationEvent
import com.keikuethas.irlhideandseek.LocationProvider
import com.keikuethas.irlhideandseek.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class GameSessionService : Service() {

    @Inject lateinit var wsClient: GameWebsocketClient
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var locationTrackingJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        // 1. Слушаем события сервера
        serviceScope.launch {
            wsClient.events.collect { event -> handleGameEvent(event) }
        }

        // 2. Запускаем фоновое отслеживание геолокации
        startLocationTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val serverUrl = intent.getStringExtra(EXTRA_SERVER_URL) ?: return START_NOT_STICKY
                if (!wsClient.isConnected.value) {
                    wsClient.connect(serverUrl)
                }
            }
            ACTION_STOP -> {
                wsClient.disconnect()
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        locationTrackingJob?.cancel()
        serviceScope.cancel()
    }

    // 🔹 Запуск трекинга локации в фоне
    private fun startLocationTracking() {
        if (!LocationProvider.hasLocationPermission) {
            // Если нет разрешений — сервис не должен работать в фоне
            stopSelf()
            return
        }

        locationTrackingJob = serviceScope.launch {
            LocationProvider.observeLocation(minTimeMs = 3000L, minDistanceM = 15f)
                .collect { event ->
                    when (event) {
                        is LocationEvent.Update -> {
                            // Отправляем координаты на сервер
                            wsClient.sendPlayerLocation(event.location.latitude, event.location.longitude)
                        }
                        LocationEvent.PermissionRevoked -> {
                            // Пользователь отозвал разрешение → останавливаем сервис
                            stopSelf()
                        }
                        LocationEvent.ProvidersDisabled -> {
                            // GPS/сеть выключены → можно показать уведомление или ждать
                        }
                    }
                }
        }
    }

    private fun handleGameEvent(event: ServerEvent) {
        when (event) {
            is ServerEvent.PlayerMoved -> savePlayerLocation(event.playerId, event.lat, event.lng)
            is ServerEvent.AbilityUsed -> playAbilitySound(event.ability.toString())
            is ServerEvent.TimerToHideFinished -> triggerVibration()
            is ServerEvent.ZoneCreated -> storeZone(event.zoneId, event.centerLat, event.centerLng, event.radius)
            else -> Unit
        }
    }

    // 🔔 Notification & Channel (без изменений, кроме типов)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Игровая сессия", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Фоновая работа игры «Прятки»"; lockscreenVisibility = Notification.VISIBILITY_PRIVATE }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🎮 Прятки: Игра активна")
            .setContentText("📍 Отслеживание местоположения")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    // 🔹 Заглушки (вынесите в репозитории/UseCases)
    private fun savePlayerLocation(playerId: String, lat: Double, lng: Double) = Unit
    private fun playAbilitySound(ability: String) = Unit
    private fun triggerVibration() {
        val vibrator = getSystemService(Vibrator::class.java) ?: return
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    private fun storeZone(zoneId: String, lat: Double, lng: Double, radius: Double) = Unit

    companion object {
        private const val CHANNEL_ID = "game_session_channel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.keikuethas.irlhideandseek.ACTION_START"
        const val ACTION_STOP = "com.keikuethas.irlhideandseek.ACTION_STOP"
        const val EXTRA_SERVER_URL = "server_url"

        fun startService(context: Context, serverUrl: String) {
            val intent = Intent(context, GameSessionService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_SERVER_URL, serverUrl)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, GameSessionService::class.java).apply { action = ACTION_STOP }
            context.startService(intent)
        }
    }
}