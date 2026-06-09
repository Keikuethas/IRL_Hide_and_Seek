package com.keikuethas.irlhideandseek.mvi.endscreen

import android.os.Parcelable
import com.keikuethas.irlhideandseek.model.DeathReason
import kotlinx.parcelize.Parcelize

@Parcelize
data class EndState(
    val victory: Boolean = true,
    val reason: DeathReason? = null,
    val hunterName: String? = null
) : Parcelable

sealed interface EndIntent {
    data object Quit : EndIntent
}

sealed interface EndResult {
    data class Init(val state: EndState) : EndResult
}

sealed interface EndEffect {
    data object Quit: EndEffect
}