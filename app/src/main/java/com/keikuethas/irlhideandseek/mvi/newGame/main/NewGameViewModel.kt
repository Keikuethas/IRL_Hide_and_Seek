package com.keikuethas.irlhideandseek.mvi.newGame.main

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.EmptyRoleCreated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.EventsUpdated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.PresetSelected
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.QuitDialogStateSet
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.ResetDialogStateSet
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.ResetState
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.RolesUpdated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.RoomNameChanged
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.network.ApiService
import com.keikuethas.irlhideandseek.network.models.AbilityParams
import com.keikuethas.irlhideandseek.network.models.CreateGameRequest
import com.keikuethas.irlhideandseek.network.models.EventConfig
import com.keikuethas.irlhideandseek.network.models.HostPlayer
import com.keikuethas.irlhideandseek.network.models.RoleParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository,
    private val apiService: ApiService
) : MVI_HiltViewModel<NewGameState, NewGameIntent, NewGameEffect, NewGameResult>(
    initialState = NewGameState(),
    savedStateKey = "NewGameState",
    savedStateHandle
) {

    init {
        // Подписываемся на изменения в репозитории, чтобы обновлять главный экран
        viewModelScope.launch {
            repository.newGameState.collect {
                val newRoles = it.rolesSettings
                dispatch(RolesUpdated(newRoles))
            }
        }
        viewModelScope.launch {
            repository.newGameState.collect {
                val newEvents = it.eventSettings
                dispatch(EventsUpdated(newEvents))
            }
        }

        viewModelScope.launch {
            repository.newGameState.collect {
                val newMap = it.mapSettings
                dispatch(NewGameResult.MapUpdated(newMap))
            }
        }
    }

    fun setHostName(name: String) {
        dispatch(NewGameResult.SetHostName(name))
    }

    private fun buildCreateGameRequest(): CreateGameRequest {
        Log.d("NewGameVM", "buildCreateGameRequest start")
        val currentState = state.value
        Log.d("NewGameVM", "currentState: $currentState")
        val gameRoles = currentState.rolesSettings.roles.associate { role: RoleState ->
            role.roleName to RoleParams(
                health = role.health,
                victory_condition = role.type.name
            )
        }
        Log.d("NewGameVM", "gameRoles: $gameRoles")
        val events =
            currentState.eventSettings.events.map { it.type.name } // предполагаем, что событие имеет поле type
        Log.d("NewGameVM", "events: $events")

        return with(currentState) {
            CreateGameRequest(
                name = roomName,
                center_lat = mapSettings.location!!.latitude,
                center_lng = mapSettings.location.longitude,
                safe_zone_radius = mapSettings.safeZoneRadius,
                min_zone_radius = mapSettings.minSafeZoneRadius,
                zone_shrink_interval = timeSettings.shrinkTime,
                game_duration = timeSettings.seekTime,
                time_to_hide = timeSettings.hideTime,
                host_player = HostPlayer(
                    host_name = currentState.hostName.ifEmpty { "Host" },
                    host_player_location_lat = mapSettings.location.latitude,
                    host_player_location_lng = mapSettings.location.longitude
                ),
                game_roles = gameRoles,
                roles_abilities = rolesSettings.roles.run {
                    val rolesMap = mutableMapOf<String, Map<String, AbilityParams>>()
                    forEach { roleState ->
                        rolesMap[roleState.roleName] = roleState.abilities.run {
                            val abilitiesMap = mutableMapOf<String, AbilityParams>()
                            forEach { abilityState ->
                                abilitiesMap[abilityState.type.toString()] =
                                    abilityState.ability.run {
                                        AbilityParams(
                                            duration_seconds = duration_seconds,
                                            number_uses = number_uses,
                                            recharge_time = recharge_time,
                                            addition_data = additional_data
                                        )
                                    }
                            }
                            abilitiesMap.toMap()
                        }
                    }
                    rolesMap.toMap()
                },
                roles_events = emptyMap(),
                events_configurations = eventSettings.events.run {
                    val eventConfigMap = mutableMapOf<String, EventConfig>()
                    forEach {
                        eventConfigMap[it.type.toString()] = EventConfig(
                            activation_frequency = it.frequency.toString(),
                            addition_data = it.additionData
                        )
                    }
                    eventConfigMap.toMap()
                }
            )
        }
    }

    override fun onIntent(intent: NewGameIntent) = when (intent) {
        NewGameIntent.CreateGame -> {
            viewModelScope.launch {
                Log.d("NewGameVM", "CreateGame intent received")
                val request = buildCreateGameRequest()
                try {
                    val response = apiService.createGame(request)
                    Log.d("NewGameVM", "Response created: $response")
                    sendEffect(NewGameEffect.JoinGame(response.game.id, response.host_player_id))
                } catch (e: Exception) {
                    Log.d("NewGameVM", "Response error: $e")
                    dispatch(NewGameResult.Error(e.message ?: "Ошибка создания игры"))
                }
            }
        }

        is NewGameIntent.QuitDialogRespond -> if (intent.confirmed) sendEffect(NewGameEffect.Quit)
        else dispatch(QuitDialogStateSet(false))

        NewGameIntent.QuitRequest -> dispatch(QuitDialogStateSet(true))

        is NewGameIntent.ChangeRoomName -> {
            dispatch(RoomNameChanged(intent.roomName))
            repository.updateRoomName(intent.roomName)
        }

        is NewGameIntent.SelectPreset -> dispatch(PresetSelected(intent.presetName))

        NewGameIntent.ResetSettings -> dispatch(ResetDialogStateSet(true))
        is NewGameIntent.ResetDialogRespond -> if (intent.confirmed) {
            dispatch(ResetState)
            // Также очищаем репозиторий при сбросе
            repository.resetAll()
        } else dispatch(ResetDialogStateSet(false))

        is NewGameIntent.CreateEmptyRole -> {
            dispatch(EmptyRoleCreated(intent.roleType))
            repository.updateRolesSettings(state.value.rolesSettings)
        }

        NewGameIntent.GoToEvents -> sendEffect(NewGameEffect.GoToEvents)
        NewGameIntent.GoToTime -> sendEffect(NewGameEffect.GoToTime)
        NewGameIntent.GoToMap -> sendEffect(NewGameEffect.GoToMap)
        NewGameIntent.GoToRoles -> sendEffect(NewGameEffect.GoToRoles)
        NewGameIntent.DismissError -> dispatch(NewGameResult.Error(""))
    }

    override fun reduce(state: NewGameState, result: NewGameResult) =
        NewGameReducer.reduce(state, result)
}