package com.keikuethas.irlhideandseek.mvi.newGame.time

object TimeReducer {
    fun reduce(state: TimeState, result: TimeResult): TimeState = when (result) {
        is TimeResult.TimeChanged -> with(result)
        { if (hide) state.copy(hideTime = newValue) else state.copy(seekTime = newValue) }

        TimeResult.VIDClosed -> state.copy(VIDState = null)
        is TimeResult.VIDOpened -> with(result)
        {
            state.copy(
                VIDState = TimeVIDState(
                    hideTime = hide,
                    initialValue = if (hide) state.hideTime else state.seekTime
                )
            )
        }

        is TimeResult.QuitDialogStateChanged -> state.copy(showQuitDialog = result.open)
    }
}