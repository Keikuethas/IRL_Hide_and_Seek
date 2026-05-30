package com.keikuethas.irlhideandseek.mvi.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.Websocket.GameSessionService
import com.keikuethas.irlhideandseek.Websocket.GameWebsocketClient
import com.keikuethas.irlhideandseek.Websocket.OutgoingRequests
import com.keikuethas.irlhideandseek.Websocket.ServerEvent
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.utils.toAbilityType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wsClient: GameWebsocketClient // ✅ 1. Инжектим через Hilt
) : MVI_HiltViewModel<GameState, GameIntent, GameEffect, GameResult>(
    initialState = GameState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "GameState"
) {

    override fun reduce(state: GameState, result: GameResult): GameState =
        GameReducer.reduce(state, result)

    override fun onIntent(intent: GameIntent) = when (intent) {
        GameIntent.AbilityListOpen ->
            dispatch(GameResult.AbilityListStateSet(true))

        GameIntent.AbilityListClose ->
            dispatch(GameResult.AbilityListStateSet(false))

        is GameIntent.CatchPlayer -> TODO()

        GameIntent.PlayerListOpen ->
            dispatch(GameResult.PlayerListStateSet(true))

        GameIntent.PlayerListClose ->
            dispatch(GameResult.PlayerListStateSet(false))

        is GameIntent.ScrollAbilityList -> TODO()

        is GameIntent.SelectAbility -> {
            when (intent.type) {
                Shield::class -> TODO() // отправка запроса
                else -> dispatch(GameResult.AbilitySelected(intent.type))
            }
            dispatch(GameResult.AbilityListStateSet(false))
        }

        is GameIntent.UseAbility ->
            state.value.usingAbilityOnMap?.toAbilityType()?.let { type ->
                wsClient.sendCommand(
                    "use_ability",
                    OutgoingRequests.useAbility(type, intent.lat, intent.lng)
                )
            } ?: Unit

        is GameIntent.Initialize ->
            with(intent) { dispatch(GameResult.Initialized(roleID, gameData)) }

        is GameIntent.UpdateLocation -> TODO()

        is GameIntent.AbilityUseRespond -> TODO()

        is GameIntent.AddZone ->
            with(intent) { dispatch(GameResult.ZoneAdded(type, id)) }

        is GameIntent.DeleteZone ->
            dispatch(GameResult.ZoneDeleted(intent.id))

        GameIntent.FinishHideTime ->
            dispatch(GameResult.HideTimeFinished)

        GameIntent.ReportCameraMoved ->
            dispatch(GameResult.CameraStopped)
    }

    // ✅ 2. Убрали ручное создание: private val wsClient = GameWebsocketClient()

    init {
        // ✅ 3. Подписка только для UI. Отменится при уходе с экрана, но сокет останется жив.
        viewModelScope.launch {
            wsClient.events.collect { event ->
                handleServerEvent(event)
            }
        }
    }

    // ✅ 4. Управление подключением теперь через ForegroundService
    fun startGameSession(context: Context, serverUrl: String) {
        GameSessionService.startService(context, serverUrl)
    }

    fun stopGameSession(context: Context) {
        GameSessionService.stopService(context)
    }

    // --- отправка команд (осталось без изменений) ---
    fun sendMyCurrentLocation(lat: Double, lng: Double) {
        wsClient.sendCommand("update_location", OutgoingRequests.updateLocation(lat, lng))
    }

    fun setReady(isReady: Boolean) {
        wsClient.sendCommand("change_ready_status", OutgoingRequests.changeReadyStatus(isReady))
    }

    // ==================================================
    // ПРИЕМ СОБЫТИЙ
    // ==================================================
    private fun handleServerEvent(event: ServerEvent) {
        when (event) {
            is ServerEvent.InitialConnected ->
                with(event) { onIntent(GameIntent.Initialize(player.roleId, game)) }

            is ServerEvent.PlayerMoved ->
                with(event) { onIntent(GameIntent.UpdateLocation(playerId, lat, lng)) }

            is ServerEvent.ZoneCreated ->
                with(event) { onIntent(GameIntent.AddZone(zoneId, zoneType)) }

            is ServerEvent.ZoneDeleted ->
                onIntent(GameIntent.DeleteZone(event.zoneId))

            is ServerEvent.AbilityUsed ->
                with(event) { onIntent(GameIntent.AbilityUseRespond(ability, result)) }

            is ServerEvent.FullGameState -> {
                // Пришло полное обновление игры
            }

            is ServerEvent.ReadyStatusChanged -> {
                // Обновить галочку "Готов" в интерфейсе
            }

            is ServerEvent.RoleChanged -> {
                // Игрок сменил роль
            }

            is ServerEvent.Pong -> {
                // Сервер ответил на пинг
            }

            ServerEvent.TimerToHideFinished ->
                onIntent(GameIntent.FinishHideTime)

            is ServerEvent.Unknown ->
                Log.wtf("Websocket", "Unknown event: ${event.rawType}")
        }
    }

    // ✅ 5. Убрали onCleared { wsClient.disconnect() }
    // Теперь сокет живет независимо от ViewModel и не рвётся при навигации
}