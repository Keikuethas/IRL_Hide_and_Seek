package com.keikuethas.irlhideandseek.mvi.newGame.time
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.data.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.PickerClosed
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.PickerOpened
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.QuitDialogStateChanged
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.TimeChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository
) : MVI_HiltViewModel<TimeState, TimeIntent, TimeEffect, TimeResult>(
    initialState = TimeState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "TimeState"
) {

    init {
        // Загружаем текущие настройки из репозитория при открытии экрана
        viewModelScope.launch {
            val initialState = repository.newGameState.value.timeSettings
            onIntent(TimeIntent.Initialize(initialState))
        }
    }

    override fun onIntent(intent: TimeIntent) = when (intent) {
        is TimeIntent.ChangeTime -> {
            dispatch(
                TimeChanged(type = state.value.editingType!!, newValue = intent.newValue)
            )
            dispatch(PickerClosed)
        }
        TimeIntent.ConfirmQuit -> {
            dispatch(QuitDialogStateChanged(false))
            sendEffect(TimeEffect.Quit)
        }
        TimeIntent.DeclineTimeChange -> dispatch(PickerClosed)
        TimeIntent.DenyQuit -> dispatch(QuitDialogStateChanged(false))
        TimeIntent.RequestQuit -> dispatch(QuitDialogStateChanged(true))
        is TimeIntent.RequestTimeChange -> dispatch(PickerOpened(type = intent.type))
        TimeIntent.Save -> {
            repository.updateTimeSettings(state.value)
            sendEffect(TimeEffect.Save)
        }

        is TimeIntent.Initialize -> dispatch(TimeResult.Initialized(intent.state))
    }

    override fun reduce(state: TimeState, result: TimeResult): TimeState =
        TimeReducer.reduce(state, result)
}