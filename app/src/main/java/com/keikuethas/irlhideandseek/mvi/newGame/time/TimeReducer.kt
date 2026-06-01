package com.keikuethas.irlhideandseek.mvi.newGame.time

object TimeReducer {
    fun reduce(state: TimeState, result: TimeResult): TimeState = when (result) {
        is TimeResult.TimeChanged ->
            with (result) {
                when (type) {
                    TimeType.Hide -> state.copy(hideTime = newValue)
                    TimeType.Seek -> state.copy(seekTime = newValue)
                    TimeType.Shrink -> state.copy(shrinkTime = newValue)
                }
            }

        TimeResult.PickerClosed -> state.copy(editingType = null)
        is TimeResult.PickerOpened -> state.copy(editingType = result.type)
        is TimeResult.QuitDialogStateChanged -> state.copy(showQuitDialog = result.open)
        is TimeResult.Initialized -> result.state
    }
}