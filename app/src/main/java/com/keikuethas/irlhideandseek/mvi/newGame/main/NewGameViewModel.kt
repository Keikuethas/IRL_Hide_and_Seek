package com.keikuethas.irlhideandseek.mvi.newGame.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.repository.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.EmptyRoleCreated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.EventsUpdated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.PresetSelected
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.QuitDialogStateSet
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.ResetDialogStateSet
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.ResetState
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.RolesUpdated
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameResult.RoomNameChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository // Инъекция репозитория
) : MVI_HiltViewModel<NewGameState, NewGameIntent, NewGameEffect, NewGameResult>(
    initialState = NewGameState(),
    savedStateKey = "NewGameState",
    savedStateHandle
) {

    init {
        // Подписываемся на изменения в репозитории, чтобы обновлять главный экран
        viewModelScope.launch {
            repository.newGameState.collect { val newRoles = it.rolesSettings
                dispatch(RolesUpdated(newRoles))
            }
        }
        viewModelScope.launch {
            repository.newGameState.collect { val newEvents = it.eventSettings
                dispatch(EventsUpdated(newEvents))
            }
        }

        viewModelScope.launch {
            repository.newGameState.collect { val newMap = it.mapSettings
            dispatch(NewGameResult.MapUpdated(newMap))
            }
        }
    }

    override fun onIntent(intent: NewGameIntent) = when (intent) {
        NewGameIntent.CreateGame -> {
            // TODO: Логика создания игры
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
    }

    override fun reduce(state: NewGameState, result: NewGameResult) =
        NewGameReducer.reduce(state, result)
}