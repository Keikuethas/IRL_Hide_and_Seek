package com.keikuethas.irlhideandseek.websocket

import android.util.Log
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.websocket.incoming.IncomingMessage
import com.keikuethas.irlhideandseek.websocket.outgoing.ChangeReadyStatusData
import com.keikuethas.irlhideandseek.websocket.outgoing.ChangeRoleData
import com.keikuethas.irlhideandseek.websocket.outgoing.HunterFoundPlayerData
import com.keikuethas.irlhideandseek.websocket.outgoing.LocationData
import com.keikuethas.irlhideandseek.websocket.outgoing.OutgoingMessage
import com.keikuethas.irlhideandseek.websocket.outgoing.UseAbilityData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
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

    private val _connectionStatus =
        MutableStateFlow<ConnectionStatus>(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    enum class ConnectionStatus {
        CONNECTING, CONNECTED, DISCONNECTED, RECONNECTING
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
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
    // Вспомогательный метод, который возвращает Boolean (успех отправки)
    private suspend inline fun <reified T : Any> sendMessage(
        logTag: String? = null,
        crossinline buildMessage: () -> T,

        ): Result<Unit> {
        return try {
            val message = buildMessage()
            val jsonString = json.encodeToString(message)

            if (logTag != null) {
                Log.d("WebSocket", "Sending $logTag: $jsonString")
            }

            if (webSocket?.send(jsonString) == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("WebSocket is closed"))
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Failed to send ${T::class.simpleName}", e)
            Result.failure(e)
        }
    }

    // ---------- Публичные методы отправки ----------

    suspend fun sendPing() = sendMessage { OutgoingMessage.Ping() }

    suspend fun sendLocation(lat: Double, lng: Double) =
        sendMessage { OutgoingMessage.UpdateLocation(data = LocationData(lat, lng)) }

    suspend fun sendUseAbility(
        abilityType: AbilityType,
        centerLat: Double? = null,
        centerLng: Double? = null
    ) = sendMessage {
        OutgoingMessage.UseAbility(data = UseAbilityData(abilityType, centerLat, centerLng))
    }

    suspend fun sendChangeRole(roleId: String) =
        sendMessage("change_role") { OutgoingMessage.ChangeRole(data = ChangeRoleData(roleId)) }

    suspend fun sendChangeReadyStatus(status: Boolean) =
        sendMessage { OutgoingMessage.ChangeReadyStatus(data = ChangeReadyStatusData(status)) }

    suspend fun sendGetGameState() = sendMessage { OutgoingMessage.GetGameState() }

    suspend fun sendHunterFoundPlayer(foundedPlayerId: String) =
        sendMessage { OutgoingMessage.HunterFoundPlayer(data = HunterFoundPlayerData(foundedPlayerId)) }
}