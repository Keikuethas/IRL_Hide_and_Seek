package com.keikuethas.irlhideandseek.mvi.lobby

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.network.ApiService
import com.keikuethas.irlhideandseek.Websocket_V2.WebSocketManager
import com.keikuethas.irlhideandseek.Websocket_V2.IncomingMessage
import com.keikuethas.irlhideandseek.network.models.GameInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class LobbyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiService: ApiService,
    private val webSocketManager: WebSocketManager
) : MVI_HiltViewModel<LobbyState, LobbyIntent, LobbyEffect, LobbyResult>(
    initialState = LobbyState(),
    savedStateKey = "lobbyState",
    savedStateHandle = savedStateHandle
) {

    // Параметры из навигации (должны быть переданы при создании экрана)
    private val playerName: String = savedStateHandle["playerName"] ?: error("playerName missing")
    private val roomName: String = savedStateHandle["roomName"] ?: error("roomName missing")
    private val gameId: String = savedStateHandle["gameId"] ?: error("gameId missing")
    private val playerId: String = savedStateHandle["playerId"] ?: error("playerId missing")

    init {
        // Устанавливаем имя игрока и название комнаты в состояние (опционально)
        dispatch(LobbyResult.SetPlayerInfo(playerName, roomName))
        connectWebSocket()
    }

    private fun connectWebSocket() {
        viewModelScope.launch {
            observeMessages()          // сначала подписываемся
            val result = webSocketManager.connect(gameId, playerId)
            if (result.isFailure) {
                dispatch(LobbyResult.Error("Ошибка подключения", "Не удалось подключиться к серверу"))
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            try {
                webSocketManager.incomingMessages.collect { message ->
                    Log.d("LobbyVM", "Raw message: $message")
                    when (message) {
                            is IncomingMessage.WebSocketConnectedPlayer -> {
                                // Инициализируем состояние из приветственных данных
                                Log.d("LobbyVM", "Received WebSocketConnectedPlayer")
                                val game = message.data.game_data
                                val player = message.data.player_data
                                val roles = game.roles.map { it.name } // список названий ролей
                                val playersList = extractPlayersFromGame(game) // список пар (имя, роль)
                                Log.d("WebSocketConnectedPlayer", "Raw message: $roles")
                                dispatch(
                                    LobbyResult.InitState(
                                        roomName = game.name,
                                        playerName = player.name,
                                        playerRole = player.role_id,
                                        players = playersList,
                                        roles = roles,
                                        isReady = player.is_player_ready
                                    )
                                )
                        }

                        is IncomingMessage.RoleChanged -> {
                            val newRoleId = message.data.role_id
                            // Предполагаем, что роль меняется у текущего игрока
                            dispatch(LobbyResult.RoleChanged(state.value.playerName, newRoleId))
                        }

                        is IncomingMessage.ReadyStatusChanged -> {
                            // Аналогично – обновляем статус готовности игрока в списке
                            dispatch(LobbyResult.ReadyStatusChanged)
                        }

                        is IncomingMessage.PlayerOnline -> {
                            dispatch(
                                LobbyResult.PlayerJoined(
                                    message.data.player_name,
                                    message.data.role ?: "Без роли"
                                )
                            )
                        }

                        is IncomingMessage.PlayerOffline -> {
                            dispatch(LobbyResult.PlayerQuit(message.data.player_id))
                        }

                        is IncomingMessage.Error -> {
                            dispatch(LobbyResult.Error("Ошибка сервера", message.toString()))
                        }
                        // Другие сообщения (game_state, create_zone и т.д.) – для лобби не нужны
                        else -> { /* игнорируем */
                        }
                    }
                }
            }catch (e: Exception) {
                Log.e("LobbyVM", "Fatal error in message collection", e)
                dispatch(LobbyResult.Error("Ошибка", e.message ?: "Unknown"))
            }
        }
    }

    override fun onIntent(intent: LobbyIntent) {
        when (intent) {
            LobbyIntent.ChangeReadyStatus -> {
                val newStatus = !state.value.isReady
                webSocketManager.sendChangeReadyStatus(newStatus)
                // Оптимистичное обновление (сервер подтвердит)
                dispatch(LobbyResult.ReadyStatusSet(newStatus))
            }
            is LobbyIntent.ChangeRole -> {
                Log.d("LobbyVM", "Sending change_role for: ${intent.newRole}")
                webSocketManager.sendChangeRole(intent.newRole)
                dispatch(LobbyResult.RoleChangeDialogStateSet(false))
            }
            LobbyIntent.DeclineRoleChange -> {
                dispatch(LobbyResult.RoleChangeDialogStateSet(false))
            }
            is LobbyIntent.QuitDialogRespond -> {
                if (intent.confirmed) {
                    webSocketManager.disconnect()
                    sendEffect(LobbyEffect.Quit)
                } else {
                    dispatch(LobbyResult.QuitDialogStateSet(false))
                }
            }
            LobbyIntent.QuitRequest -> {
                dispatch(LobbyResult.QuitDialogStateSet(true))
            }
            LobbyIntent.RequestRoleChangeDialog -> {
                dispatch(LobbyResult.RoleChangeDialogStateSet(true))
            }
        }
    }

    override fun reduce(state: LobbyState, result: LobbyResult): LobbyState {
        return LobbyReducer.reduce(state, result)
    }

    // Вспомогательная функция для извлечения списка игроков из GameInfo
    private fun extractPlayersFromGame(game: GameInfo): List<Pair<String, String>> {
        // В вашем GameInfo из ответа сервера есть поле players? Если нет – можно временно вернуть пустой список,
        // а реальных игроков будут добавлять сообщения player_online.
        // Для простоты – возвращаем пустой список.
        return emptyList()
    }
}