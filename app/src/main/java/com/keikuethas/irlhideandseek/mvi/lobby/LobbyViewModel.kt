package com.keikuethas.irlhideandseek.mvi.lobby

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.LobbyEvent
import com.keikuethas.irlhideandseek.data.LobbyRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.Error
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.InitState
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.PlayerJoined
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.PlayerQuit
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.PlayerReadyStatusSet
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.PlayerRoleChanged
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.QuitDialogStateSet
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.ReadyStatusSet
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.RoleChangeDialogStateSet
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.RoleChanged
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult.SetPlayerInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: LobbyRepository,
) : MVI_HiltViewModel<LobbyState, LobbyIntent, LobbyEffect, LobbyResult>(
    initialState = LobbyState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "LobbyState"
) {

    // Параметры из навигации (должны быть переданы при создании экрана)
    private val playerName: String = savedStateHandle["playerName"] ?: error("playerName missing")
    private val roomName: String = savedStateHandle["roomName"] ?: error("roomName missing")
    private val gameId: String = savedStateHandle["gameId"] ?: error("gameId missing")
    private val playerId: String = savedStateHandle["playerId"] ?: error("playerId missing")


    init {
        dispatch(SetPlayerInfo(playerName, roomName))

        // Запускаем наблюдение за событиями
        observeLobbyEvents()

        repository.connect(
            scope = viewModelScope,
            gameId = gameId,
            playerId = playerId
        )
    }

    private fun observeLobbyEvents() {
        viewModelScope.launch {
            try {
                repository.lobbyEvents.collect { event ->
                    Log.d("LobbyVM", "Event received: $event")
                    handleEvent(event)
                }
            } catch (e: Exception) {
                Log.e("LobbyVM", "Error collecting events", e)
                dispatch(Error("Ошибка подключения", e.message ?: "Unknown"))
            }
        }
    }

    private fun handleEvent(event: LobbyEvent) = with(event) {
        when (this) {
            is LobbyEvent.InitState -> {
                val myId = player.id
                dispatch(
                    InitState(
                        roomName = game.name,
                        playerName = player.name,
                        playerRole = player.role_id,
                        players = game.players.filterNot { it.id == myId },
                        roles = game.roles,
                        isReady = player.is_player_ready,
                        roomCode = game.game_code
                    )
                )
            }

            is LobbyEvent.RoleChanged -> {
                dispatch(RoleChanged(newRoleId))
            }

            is LobbyEvent.ReadyStatusChanged -> {
                dispatch(ReadyStatusSet(ready))
            }

            is LobbyEvent.PlayerJoined -> {
                dispatch(
                    PlayerJoined(
                        id = playerId,
                        playerName = playerName,
                        roleId = role ?: "Без роли"
                    )
                )
            }

            is LobbyEvent.PlayerQuit -> {
                dispatch(PlayerQuit(playerId))
            }

            is LobbyEvent.Error -> {
                dispatch(Error("Ошибка сервера", message))
            }

            is LobbyEvent.ConnectionFailed ->
                dispatch(
                    Error(
                        title = "Ошибка подключения",
                        message = message
                    )
                )

            is LobbyEvent.PlayerRoleChanged -> dispatch(
                PlayerRoleChanged(
                    id = playerId,
                    newRoleId = newRoleId
                )
            )

            is LobbyEvent.PlayerReadyStatusChanged ->
                dispatch(
                    PlayerReadyStatusSet(
                        playerId = playerId,
                        ready = ready
                    )
                )

            is LobbyEvent.StartTimerToHide ->
                sendEffect(LobbyEffect.StartGame(duration))

        }
    }


    override fun onIntent(intent: LobbyIntent) {
        when (intent) {
            LobbyIntent.RequestRoleChangeDialog -> {
                Log.d("LobbyVM", "RequestRoleChangeDialog received")
                dispatch(RoleChangeDialogStateSet(true))
            }

            LobbyIntent.ChangeReadyStatus -> {
                val newStatus = !state.value.isReady

                viewModelScope.launch {
                    repository.changeReadyStatus(newStatus)
                }
            }

            is LobbyIntent.ChangeRole -> {
                Log.d("LobbyVM", "Sending change_role for roleId: ${intent.roleId}")
                viewModelScope.launch {
                    repository.changeRole(intent.roleId)
                }
            }

            LobbyIntent.DeclineRoleChange -> {
                dispatch(RoleChangeDialogStateSet(false))
            }

            is LobbyIntent.QuitDialogRespond -> {
                if (intent.confirmed) {
                    repository.disconnect()
                    sendEffect(LobbyEffect.Quit)
                } else {
                    dispatch(QuitDialogStateSet(false))
                }
            }

            LobbyIntent.QuitRequest -> {
                dispatch(QuitDialogStateSet(true))
            }

            LobbyIntent.DismissError -> dispatch(Error("", ""))
        }
    }

    override fun reduce(state: LobbyState, result: LobbyResult) =
        LobbyReducer.reduce(state, result)

}