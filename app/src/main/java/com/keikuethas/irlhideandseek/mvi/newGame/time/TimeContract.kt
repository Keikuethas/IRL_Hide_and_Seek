package com.keikuethas.irlhideandseek.mvi.newGame.time
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeState(
    val hideTime: Int = 60,
    val seekTime: Int = 300,
    val isPickerOpen: Boolean = false,
    val editingHideTime: Boolean = false,
    val showQuitDialog: Boolean = false
) : Parcelable

sealed interface TimeIntent {
    data class RequestTimeChange(val hide: Boolean) : TimeIntent
    data object DeclineTimeChange : TimeIntent
    data class ChangeTime(val newValue: Int) : TimeIntent
    data object RequestQuit : TimeIntent
    data object ConfirmQuit : TimeIntent
    data object DenyQuit : TimeIntent
    data object Save : TimeIntent
    data class Initialize(val state: TimeState): TimeIntent
}

sealed interface TimeResult {
    data object PickerClosed : TimeResult
    data class PickerOpened(val hide: Boolean) : TimeResult
    data class TimeChanged(val hide: Boolean, val newValue: Int) : TimeResult
    data class QuitDialogStateChanged(val open: Boolean) : TimeResult
    data class Initialized(val state: TimeState): TimeResult
}

sealed interface TimeEffect {
    data object Quit : TimeEffect
    data object Save : TimeEffect
}