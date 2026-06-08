package com.keikuethas.irlhideandseek.mvi.game

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.LocationProvider
import com.keikuethas.irlhideandseek.data.GameEvent
import com.keikuethas.irlhideandseek.data.GameRepository
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.game.GameEffect.EndGame
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityListStateSet
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilitySelected
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityUsed
import com.keikuethas.irlhideandseek.mvi.game.GameResult.CameraStopped
import com.keikuethas.irlhideandseek.mvi.game.GameResult.Error
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ErrorDismissed
import com.keikuethas.irlhideandseek.mvi.game.GameResult.GameStarted
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerDied
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerListStateSet
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerMoved
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerQuit
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ZoneAdded
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ZoneDeleted
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GameRepository
) : MVI_HiltViewModel<GameState, GameIntent, GameEffect, GameResult>(
    initialState = GameState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "GameState"
) {

    init {
        repository.connect(viewModelScope)
        observeGameEvents()

        // refactor получать локацию в любом случае (как в map)
        val loc = LocationProvider.lastKnownLocation
        loc?.let {
            dispatch(
                GameResult.LocationSet(
                    location = Point(it.latitude, it.longitude)
                )
            )
        }

        startCooldownTicker()
    }

    private fun startCooldownTicker() {
        viewModelScope.launch {
            while (isActive) {
                delay(100)
                if (state.value.abilities.any { it.cooldownProgress != 1F })
                    dispatch(GameResult.CooldownUpdated)
            }
        }
    }

    private fun observeGameEvents() {
        viewModelScope.launch {
            try {
                repository.gameEvents.collect { event ->
                    Log.d("LobbyVM", "Event received: $event")
                    handleEvent(event)
                }
            } catch (e: Exception) {
                Log.e("LobbyVM", "Error collecting events", e)
                //dispatch(Error("Ошибка подключения", e.message ?: "Unknown"))
            }
        }
    }

    private fun handleEvent(event: GameEvent) = with(event) {
        when (this) {
            is GameEvent.ConnectionFailed ->
                dispatch(Error("Ошибка подключения"))

            is GameEvent.CreateZone ->
                dispatch(
                    ZoneAdded(
                        type = zoneType,
                        zoneId = zoneId,
                        location = Point(centerLat, centerLng),
                        radius = radius
                    )
                )

            is GameEvent.DeleteZone ->
                dispatch(ZoneDeleted(zoneId))

            is GameEvent.Error ->
                dispatch(Error(message))

            is GameEvent.GameFinished -> {
                repository.disconnect()
                sendEffect(EndGame(victory))
            }

            is GameEvent.PlayerDied ->
                dispatch(PlayerDied(playerId))

            is GameEvent.PlayerQuit ->
                dispatch(PlayerQuit(playerId))

            is GameEvent.PlayerMoved ->
                dispatch(
                    PlayerMoved(
                        playerId = playerId,
                        location = Point(locationLat, locationLng)
                    )
                )

            is GameEvent.StartTimerForGame ->
                dispatch(GameStarted(duration))

            is GameEvent.YouDied -> {
                repository.disconnect()
                sendEffect(
                    EndGame(
                        victory = false,
                        reason = reason,
                        hunterId = hunterId
                    )
                )
            }

            is GameEvent.AbilityUsed ->
                if (result == 0)
                    dispatch(AbilityUsed(ability))

            is GameEvent.GameState -> {/*idk*/
            }

            GameEvent.LocationPermissionRevoked -> {/*todo*/
            }

            GameEvent.LocationProvidersDisabled -> {/*todo*/
            }

            is GameEvent.LocationUpdated -> {
                sendLocation(
                    latitude = latitude,
                    longitude = longitude
                )

                dispatch(
                    GameResult.LocationChanged(
                        location =
                            Point(latitude, longitude)
                    )
                )
            }

            is GameEvent.AirdropCollected -> TODO()
            is GameEvent.PlayerEnteredZone -> {}
            is GameEvent.PlayerExitedZone -> {}

            is GameEvent.ApplyDamage ->
                dispatch(GameResult.DamageApplied(damage))
        }
    }

    override fun reduce(state: GameState, result: GameResult): GameState =
        GameReducer.reduce(state, result)

    override fun onIntent(intent: GameIntent) = when (intent) {
        GameIntent.AbilityListOpen ->
            dispatch(AbilityListStateSet(true))

        GameIntent.AbilityListClose ->
            dispatch(AbilityListStateSet(false))

        is GameIntent.CatchPlayer ->
            sendCatchPlayer(playerId = intent.playerId)

        GameIntent.PlayerListOpen ->
            dispatch(PlayerListStateSet(true))

        GameIntent.PlayerListClose ->
            dispatch(PlayerListStateSet(false))

        is GameIntent.SelectAbility -> {
            when (intent.type) {
                AbilityType.SHIELD ->
                    sendUseAbility(intent.type)// отправка запроса

                else -> dispatch(AbilitySelected(intent.type))
            }
            dispatch(AbilityListStateSet(false))
        }

        is GameIntent.UseAbility ->
            sendUseAbility(
                type = state.value.usingAbilityOnMap!!,
                location = state.value.usingAbilityLocation!!
            )

        GameIntent.ReportCameraMoveFinished ->
            dispatch(CameraStopped)

        GameIntent.DismissError ->
            dispatch(ErrorDismissed)

        GameIntent.CancelUseAbility ->
            dispatch(GameResult.AbilityUseCancelled)

        GameIntent.SelectCatch ->
            dispatch(GameResult.OpenPlayerList)

        is GameIntent.ReportCameraPositionChanged ->
            if (state.value.usingAbilityOnMap != null)
                dispatch(GameResult.AbilityLocationUpdated(intent.location))
            else Unit
    }

    fun sendCatchPlayer(playerId: String) =
        viewModelScope.launch {
            repository.catchPlayer(playerId = playerId)
        }

    fun sendUseAbility(type: AbilityType, location: Point? = null) =
        viewModelScope.launch {
            repository.useAbility(
                type = type,
                centerLat = location?.latitude,
                centerLng = location?.longitude
            )
        }

    fun sendLocation(latitude: Double, longitude: Double) =
        viewModelScope.launch {
            repository.updateLocation(
                latitude = latitude,
                longitude = longitude
            )
        }

}