package com.keikuethas.irlhideandseek.mvi

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

//describe
abstract class MVI_HiltViewModel<State: Parcelable, Intent: Any, Effect: Any, Result: Any> (
    initialState: State,
    savedStateKey: String,
    savedStateHandle: SavedStateHandle
)  : MVI_ViewModel<State, Intent, Effect, Result>(
    initialState = savedStateHandle[savedStateKey] ?: initialState
) {
    init {
        viewModelScope.launch {
            state.drop(1).collect { savedStateHandle[savedStateKey] = it }
        }
    }
}