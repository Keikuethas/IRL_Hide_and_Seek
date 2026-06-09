package com.keikuethas.irlhideandseek.mvi.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.GameLocationService
import com.keikuethas.irlhideandseek.LocationProvider
import com.keikuethas.irlhideandseek.data.GameEvent
import com.keikuethas.irlhideandseek.data.GameRepository
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.game.GameEffect.EndGame
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityListStateSet
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityLocationUpdated
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilitySelected
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityUseCancelled
import com.keikuethas.irlhideandseek.mvi.game.GameResult.AbilityUsed
import com.keikuethas.irlhideandseek.mvi.game.GameResult.CameraStopped
import com.keikuethas.irlhideandseek.mvi.game.GameResult.DamageApplied
import com.keikuethas.irlhideandseek.mvi.game.GameResult.Error
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ErrorDismissed
import com.keikuethas.irlhideandseek.mvi.game.GameResult.GameStarted
import com.keikuethas.irlhideandseek.mvi.game.GameResult.Initialized
import com.keikuethas.irlhideandseek.mvi.game.GameResult.OpenPlayerList
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerDied
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerListStateSet
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerMoved
import com.keikuethas.irlhideandseek.mvi.game.GameResult.PlayerQuit
import com.keikuethas.irlhideandseek.mvi.game.GameResult.QuitDialogStateSet
import com.keikuethas.irlhideandseek.mvi.game.GameResult.SafeZoneRadiusChanged
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ZoneAdded
import com.keikuethas.irlhideandseek.mvi.game.GameResult.ZoneDeleted
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: GameRepository,
    @ApplicationContext private val context: Context // ✅ Внедряем Context для управления сервисом
) : MVI_HiltViewModel<GameState, GameIntent, GameEffect, GameResult>(
    initialState = GameState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "GameState"
) {
    val timeToHide: Int = savedStateHandle["timeToHide"] ?: error("timeToHide missing")

    init {
        repository.connect(viewModelScope)
        observeGameEvents()

        // ✅ Исправлена опечатка: читаем кэш, который провайдер обновил сам
        val loc = LocationProvider.lastKnownLocation
        loc?.let {
            dispatch(
                GameResult.LocationSet(
                    location = Point(it.latitude, it.longitude)
                )
            )
        }

        sendGetGameState()
        startCooldownTicker()
        startTimer()

        // ✅ Начинаем слушать координаты от Foreground Service
        observeServiceLocation()
    }

    private fun observeServiceLocation() {
        viewModelScope.launch {
            GameLocationService.currentLocation.collect { location ->
                location?.let {
                    // Отправляем координаты на сервер через ваш репозиторий
                    sendLocation(it.latitude, it.longitude)

                    // Обновляем UI (если нужно дублировать LocationSet, или используйте отдельный Result)
                    dispatch(
                        GameResult.LocationSet(
                            location = Point(it.latitude, it.longitude)
                        )
                    )
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                dispatch(GameResult.TimerTick)
            }
        }
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
                    Log.d("GameVM", "Event received: $event")
                    handleEvent(event)
                }
            } catch (e: Exception) {
                Log.e("GameVM", "Error collecting events", e)
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
                        location = Point(centerLat, centerLng), // ✅ Исправлена опечатка centerL ng
                        radius = radius
                    )
                )

            is GameEvent.DeleteZone ->
                dispatch(ZoneDeleted(zoneId))

            is GameEvent.Error ->
                dispatch(Error(message))

            is GameEvent.GameFinished -> {
                stopLocationTracking() // ✅ Останавливаем сервис
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

            is GameEvent.StartTimerForGame -> {
                dispatch(GameStarted(duration))
                startLocationTracking() // ✅ Запускаем сервис, когда игра началась
            }

            is GameEvent.YouDied -> {
                stopLocationTracking() // ✅ Останавливаем сервис
                repository.disconnect()
                sendEffect(
                    EndGame(
                        victory = false,
                        reason = reason,
                        hunterName = state.value.players.find { it.id == hunterId }?.name
                    )
                )
            }

            is GameEvent.AbilityUsed ->
                if (result == 0)
                    dispatch(AbilityUsed(ability))

            is GameEvent.GameState -> {
                dispatch(
                    Initialized(
                        secondsRemain = timeToHide,
                        abilities = player.role_ref!!
                            .abilities.map {
                                AbilityState(
                                    type = it.ability_type,
                                    params = it.data.toList() +
                                            ("recharge_time" to it.recharge_time) +
                                            ("number_uses" to it.number_uses) +
                                            ("duration_seconds" to (it.duration_seconds ?: 0)),
                                )
                            },
                        roleType = player.role_ref.victory_condition,
                        players = game.players.map { pl ->
                            PlayerState(
                                id = pl.id,
                                name = pl.name,
                                roleType = pl.role_ref!!.victory_condition,
                                location = Point(pl.location_lat, pl.location_lng),
                                isAlive = pl.is_alive
                            )
                        },
                        playerHealth = player.health,
                        safeZoneRadius = game.safe_zone_radius,
                        safeZoneCenter = Point(game.safe_zone_center_lat, game.safe_zone_center_lng)
                    )
                )
            }

            GameEvent.LocationPermissionRevoked -> {
                stopLocationTracking()
                dispatch(Error("Разрешение на геолокацию отозвано"))
            }

            GameEvent.LocationProvidersDisabled -> {
                dispatch(Error("Включите GPS для продолжения игры"))
            }

            // CONCERN
            // Примечание: GameEvent.LocationUpdated теперь обрабатывается сервером,
            // а локально мы получаем координаты из GameLocationService.currentLocation
            is GameEvent.LocationUpdated -> {
                // Можно оставить для совместимости, если сервер присылает подтверждение
            }

            is GameEvent.AirdropCollected -> TODO()
            is GameEvent.PlayerEnteredZone -> {}
            is GameEvent.PlayerExitedZone -> {}

            is GameEvent.ApplyDamage ->
                dispatch(DamageApplied(damage))

            is GameEvent.SafeZoneUpdated ->
                dispatch(SafeZoneRadiusChanged(radius))
        }
    }

    // ✅ Методы управления службой
    private fun startLocationTracking() {
        if (!(GameLocationService.currentLocation.value != null || !GameLocationService.isRunning)) {
            // Простая проверка, можно добавить флаг isRunning в companion object сервиса
            GameLocationService.start(context)
        }
    }

    private fun stopLocationTracking() {
        GameLocationService.stop(context)
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
                    sendUseAbility(intent.type)

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
            dispatch(AbilityUseCancelled)

        GameIntent.SelectCatch ->
            dispatch(OpenPlayerList)

        is GameIntent.ReportCameraPositionChanged ->
            dispatch(AbilityLocationUpdated(intent.location))

        GameIntent.QuitConfirmed -> {
            stopLocationTracking() // ✅ Останавливаем сервис при выходе
            repository.disconnect()
            dispatch(QuitDialogStateSet(false))
            sendEffect(GameEffect.Quit)
        }

        GameIntent.QuitDeclined ->
            dispatch(QuitDialogStateSet(false))

        GameIntent.RequestQuit ->
            dispatch(QuitDialogStateSet(true))
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

    fun sendGetGameState() =
        viewModelScope.launch {
            repository.getGameState()
        }
}