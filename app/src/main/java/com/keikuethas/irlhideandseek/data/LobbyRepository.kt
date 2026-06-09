package com.keikuethas.irlhideandseek.data

import android.util.Log
import com.keikuethas.irlhideandseek.websocket.WebSocketManager
import com.keikuethas.irlhideandseek.websocket.incoming.IncomingMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LobbyRepository @Inject constructor(
    private val webSocketManager: WebSocketManager
) {
    private val _lobbyEvents = MutableSharedFlow<LobbyEvent>(
        replay = 1,
        extraBufferCapacity = 64
    )
    val lobbyEvents: SharedFlow<LobbyEvent> = _lobbyEvents.asSharedFlow()

    fun connect(scope: CoroutineScope, gameId: String, playerId: String) {
        scope.launch {
            val observerJob = scope.launch { observeMessages() }

            val result = webSocketManager.connect(gameId, playerId)

            if (result.isFailure) {
                observerJob.cancel()
                _lobbyEvents.emit(
                    LobbyEvent.ConnectionFailed(
                        result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                    )
                )
            }
        }
    }


    // Инициализация подписки на сообщения
    suspend fun observeMessages(): Nothing =
        webSocketManager.incomingMessages.collect { message ->
            with(message) {
                Log.d("LobbyRepo", "Raw message: $this")

                when (this) {
                    is IncomingMessage.WebSocketConnectedPlayer ->
                        _lobbyEvents.emit(
                            LobbyEvent.InitState(
                                game = data.game_data,
                                player = data.player_data
                            )
                        )


                    is IncomingMessage.RoleChanged ->
                        _lobbyEvents.emit(
                            LobbyEvent.RoleChanged(
                                newRoleId = data.role_id
                            )
                        )


                    is IncomingMessage.PlayerRoleChanged ->
                        _lobbyEvents.emit(
                            LobbyEvent.PlayerRoleChanged(
                                playerId = data.player_id,
                                newRoleId = data.role_id
                            )
                        )


                    is IncomingMessage.ReadyStatusChanged ->
                        _lobbyEvents.emit(LobbyEvent.ReadyStatusChanged(data.ready_status))


                    is IncomingMessage.PlayerReadyStatusChanged -> {
                        _lobbyEvents.emit(
                            LobbyEvent.PlayerReadyStatusChanged(
                                data.player_id,
                                data.ready_status
                            )
                        )
                    }

                    is IncomingMessage.PlayerOnline -> {
                        _lobbyEvents.emit(
                            LobbyEvent.PlayerJoined(
                                playerId = data.player_id,
                                playerName = data.player_name,
                                role = data.role_id,
                                roleRef = data.role_ref
                            )
                        )
                    }

                    is IncomingMessage.PlayerOffline ->
                        _lobbyEvents.emit(LobbyEvent.PlayerQuit(data.player_id))


                    is IncomingMessage.Error ->
                        _lobbyEvents.emit(LobbyEvent.Error(toString()))


                    is IncomingMessage.StartTimerToHide ->
                        _lobbyEvents.emit(LobbyEvent.StartTimerToHide(data.duration_seconds))

                    else -> { /* Игнорируем сообщения не для лобби */
                    }
                }
            }
        }

    fun disconnect() =
        webSocketManager.disconnect()

    suspend fun changeRole(roleId: String) =
        webSocketManager.sendChangeRole(roleId)

    suspend fun changeReadyStatus(status: Boolean) =
        webSocketManager.sendChangeReadyStatus(status)
}
