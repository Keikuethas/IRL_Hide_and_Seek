package com.keikuethas.irlhideandseek.mvi.newGame.time
import androidx.lifecycle.SavedStateHandle
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.PickerClosed
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.PickerOpened
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.QuitDialogStateChanged
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeResult.TimeChanged
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MVI_HiltViewModel<TimeState, TimeIntent, TimeEffect, TimeResult>(
    initialState = TimeState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "TimeState"
) {
    override fun onIntent(intent: TimeIntent) = when (intent) {
        is TimeIntent.ChangeTime -> dispatch(
            TimeChanged(hide = state.value.editingHideTime, newValue = intent.newValue)
        )
        TimeIntent.ConfirmQuit -> {
            dispatch(QuitDialogStateChanged(false))
            sendEffect(TimeEffect.Quit)
        }
        TimeIntent.DeclineTimeChange -> dispatch(PickerClosed)
        TimeIntent.DenyQuit -> dispatch(QuitDialogStateChanged(false))
        TimeIntent.RequestQuit -> dispatch(QuitDialogStateChanged(true))
        is TimeIntent.RequestTimeChange -> dispatch(PickerOpened(intent.hide))
        TimeIntent.Save -> sendEffect(TimeEffect.Save)
    }

    override fun reduce(state: TimeState, result: TimeResult): TimeState =
        TimeReducer.reduce(state, result)
}