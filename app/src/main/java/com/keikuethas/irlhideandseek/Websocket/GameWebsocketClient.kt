package com.keikuethas.irlhideandseek.Websocket

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameWebsocketClient @Inject constructor() {
    private var webSocket: WebSocket? = null
    private val gson: Gson = GsonBuilder().create()
    private val scope = CoroutineScope(Dispatchers.IO)

    // Статус подключения для UI
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Поток событий: буфер 32, сбрасываем старые при переполнении
    private val _events = MutableSharedFlow<ServerEvent>(
        replay = 0,
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<ServerEvent> = _events.asSharedFlow()

    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    fun connect(url: String) {
        if (_isConnected.value) return
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    fun disconnect() {
        webSocket?.close(1000, "User exit")
        webSocket = null
        _isConnected.value = false
    }

    fun sendCommand(type: String, data: Any) {
        val envelope = WsEnvelope(
            type = type,
            data = gson.toJsonTree(data)
        )
        val json = gson.toJson(envelope)
        webSocket?.send(json)
        Log.d("WS_CLIENT", "⬆️ Sent: $json")
    }

    private inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WS_CLIENT", "🟢 Connected")
            _isConnected.value = true
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WS_CLIENT", "⬇️ Received: $text")
            // ❗ tryEmit не блокирует поток OkHttp
            try {
                val envelope = gson.fromJson(text, WsEnvelope::class.java)
                val event = decodeEvent(envelope)
                _events.tryEmit(event)
            } catch (e: Exception) {
                Log.e("WS_CLIENT", "❌ Error parsing message: $text", e)
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WS_CLIENT", "🔴 Closed: $reason")
            _isConnected.value = false
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WS_CLIENT", "💥 Failure", t)
            _isConnected.value = false
        }
    }

    private fun decodeEvent(env: WsEnvelope): ServerEvent {
        val data = env.data ?: return ServerEvent.Unknown(env.type)
        return when (env.type) {
            "pong" -> {
                val p = gson.fromJson(data, PongPayload::class.java)
                ServerEvent.Pong(p.serverTime)
            }
            "websocket_connected_player" -> {
                val p = gson.fromJson(data, InitialConnectedPayload::class.java)
                ServerEvent.InitialConnected(p.player, p.game)
            }
            "player_moved" -> {
                val p = gson.fromJson(data, PlayerMovedPayload::class.java)
                ServerEvent.PlayerMoved(p.playerId, p.lat, p.lng, p.timestamp)
            }
            "ability_used" -> {
                val p = gson.fromJson(data, AbilityUsedPayload::class.java)
                ServerEvent.AbilityUsed(p.ability, p.result)
            }
            "game_state" -> {
                val game = gson.fromJson(data, GameData::class.java)
                ServerEvent.FullGameState(game)
            }
            "role_changed" -> {
                val p = gson.fromJson(data, RoleChangedPayload::class.java)
                ServerEvent.RoleChanged(p.roleId)
            }
            "ready_status_changed" -> {
                val p = gson.fromJson(data, ReadyStatusPayload::class.java)
                ServerEvent.ReadyStatusChanged(p.status)
            }
            "create_zone" -> {
                val p = gson.fromJson(data, CreateZonePayload::class.java)
                ServerEvent.ZoneCreated(p.zoneId, p.zoneType, p.centerLat, p.centerLng, p.radius)
            }
            "delete_zone" -> {
                val p = gson.fromJson(data, DeleteZonePayload::class.java)
                ServerEvent.ZoneDeleted(p.zoneId)
            }
            "timer_to_hide_finished" -> ServerEvent.TimerToHideFinished
            else -> ServerEvent.Unknown(env.type)
        }
    }
}