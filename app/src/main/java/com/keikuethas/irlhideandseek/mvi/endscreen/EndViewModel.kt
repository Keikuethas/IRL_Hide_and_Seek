package com.keikuethas.irlhideandseek.mvi.endscreen

import androidx.lifecycle.SavedStateHandle
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EndViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): MVI_HiltViewModel<EndState, EndIntent, EndEffect, EndResult>(
    initialState = EndState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "EndState"
) {
    override fun onIntent(intent: EndIntent) = when(intent) {
        else -> Unit
    }

    override fun reduce(
        state: EndState,
        result: EndResult
    ) = EndReducer.reduce(state, result)

}