package com.keikuethas.irlhideandseek.mvi.endscreen

import androidx.lifecycle.SavedStateHandle
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EndViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : MVI_HiltViewModel<EndState, EndIntent, EndEffect, EndResult>(
    initialState = EndState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "EndState"
) {

    val victory: Boolean = savedStateHandle["victory"] ?: error("victory is missing")
    val reason: DeathReason? = savedStateHandle["reason"]
    val hunterId: String? = savedStateHandle["hunterID"]

    init {
        dispatch(EndResult.Init(EndState(victory = victory, reason, hunterId)))
    }

    override fun onIntent(intent: EndIntent) = when (intent) {
        EndIntent.Quit -> sendEffect(EndEffect.Quit)
    }

    override fun reduce(
        state: EndState,
        result: EndResult
    ) = EndReducer.reduce(state, result)

}