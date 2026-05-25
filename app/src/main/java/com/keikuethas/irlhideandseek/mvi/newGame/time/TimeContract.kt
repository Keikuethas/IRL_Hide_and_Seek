package com.keikuethas.irlhideandseek.mvi.newGame.time

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimeVIDState(
    // Value Input Dialog State
    val initialValue: Int,
    val hideTime: Boolean,
) : Parcelable

@Parcelize
data class TimeState(
    val hideTime: Int = 60,
    val seekTime: Int = 300,
    val VIDState: TimeVIDState? = null,
    val showQuitDialog: Boolean = false
) : Parcelable

sealed interface TimeIntent {
    data class RequestTimeChange(val hide: Boolean) : TimeIntent
    data object DeclineTimeChange : TimeIntent
    data class ChangeTime(val newValue: Int) : TimeIntent
    data object RequestQuit : TimeIntent
    data object ConfirmQuit : TimeIntent
    data object DenyQuit : TimeIntent
    data object Save: TimeIntent
}

sealed interface TimeResult {
    data object VIDClosed : TimeResult
    data class VIDOpened(val hide: Boolean) : TimeResult
    data class TimeChanged(val hide: Boolean, val newValue: Int) : TimeResult
    data class QuitDialogStateChanged(val open: Boolean): TimeResult
}

sealed interface TimeEffect {
    data object Quit : TimeEffect
    data object Save: TimeEffect
}