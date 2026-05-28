package com.keikuethas.irlhideandseek.mvi.newGame.time

object TimeReducer {
    fun reduce(state: TimeState, result: TimeResult): TimeState = when (result) {
        is TimeResult.TimeChanged -> {
            if (result.hide) state.copy(hideTime = result.newValue)
            else state.copy(seekTime = result.newValue)
        }
        TimeResult.PickerClosed -> state.copy(isPickerOpen = false, editingHideTime = false)
        is TimeResult.PickerOpened -> state.copy(isPickerOpen = true, editingHideTime = result.hide)
        is TimeResult.QuitDialogStateChanged -> state.copy(showQuitDialog = result.open)
    }
}