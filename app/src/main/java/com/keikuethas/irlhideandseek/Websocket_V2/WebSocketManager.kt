package com.keikuethas.irlhideandseek.Websocket_V2

// WebSocketManager.kt
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WebSocketManager @Inject constructor() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var gameId: String? = null
    private var playerId: String? = null
    private var reconnectJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _incomingMessages = MutableSharedFlow<IncomingMessage>(
        replay = 1,
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages: SharedFlow<IncomingMessage> = _incomingMessages.asSharedFlow()

    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>(replay = 1)
    val connectionStatus: SharedFlow<ConnectionStatus> = _connectionStatus.asSharedFlow()

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, RECONNECTING
    }

    private val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    suspend fun connect(gameId: String, playerId: String): Result<Unit> {
        this.gameId = gameId
        this.playerId = playerId
        _connectionStatus.emit(ConnectionStatus.CONNECTING)

        return suspendCancellableCoroutine { continuation ->
            val url = "ws://shrunk-scrambler-gauze.ngrok-free.dev/ws/$gameId/$playerId"
            val request = Request.Builder().url(url).build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WebSocket", "Connected")
                    scope.launch {
                        _connectionStatus.emit(ConnectionStatus.CONNECTED)
                    }
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit))
                    }
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WebSocket", "Received: $text")
                    try {
                        val message = json.decodeFromString<IncomingMessage>(text)
                        scope.launch {
                            _incomingMessages.emit(message)
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Failed to parse message: $text", e)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WebSocket", "Failure", t)
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(t))
                    } else {
                        scheduleReconnect()
                    }
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WebSocket", "Closed: $reason")
                    scope.launch {
                        _connectionStatus.emit(ConnectionStatus.DISCONNECTED)
                    }
                }
            })

            continuation.invokeOnCancellation {
                webSocket?.close(1000, null)
                webSocket = null
            }
        }
    }

    private fun scheduleReconnect() {
        if (reconnectJob?.isActive == true) return
        reconnectJob = scope.launch {
            _connectionStatus.emit(ConnectionStatus.RECONNECTING)
            delay(5000)
            val g = gameId ?: return@launch
            val p = playerId ?: return@launch
            connect(g, p)
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        scope.launch {
            _connectionStatus.emit(ConnectionStatus.DISCONNECTED)
        }
    }

    // ---------- Отправка сообщений ----------
    private inline fun <reified T : Any> send(message: T) {
        try {
            val jsonString = json.encodeToString(message)
            webSocket?.send(jsonString)
        } catch (e: Exception) {
            Log.e("WebSocket", "Failed to serialize ${message::class.simpleName}: $message", e)
        }
    }

    fun sendPing() {
        send(OutgoingPing())
    }

    fun sendLocation(lat: Double, lng: Double) {
        send(OutgoingUpdateLocation(data = LocationData(lat, lng)))
    }

    fun sendUseAbility(abilityType: String, centerLat: Double? = null, centerLng: Double? = null) {
        send(OutgoingUseAbility(data = UseAbilityData(abilityType, centerLat, centerLng)))
    }

    fun sendChangeRole(roleId: String) {
        val message = OutgoingChangeRole(data = ChangeRoleData(roleId))
        val jsonString = json.encodeToString(message)
        Log.d("WebSocket", "Sending change_role: $jsonString")
        send(jsonString)
    }

    fun sendChangeReadyStatus(status: Boolean) {
        send(OutgoingChangeReadyStatus(data = ChangeReadyStatusData(status)))
    }

    fun sendGetGameState() {
        send(OutgoingGetGameState())
    }

    fun sendHunterFoundPlayer(foundedPlayerId: String) {
        send(OutgoingHunterFoundPlayer(data = HunterFoundPlayerData(foundedPlayerId)))
    }
}