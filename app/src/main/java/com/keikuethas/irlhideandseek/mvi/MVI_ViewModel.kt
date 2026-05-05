package com.keikuethas.irlhideandseek.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class MVI_ViewModel<State: Any, Intent: Any, Effect: Any, Result: Any>(
    initialState: State,
    private val reduce: (state: State, result: Result) -> State
): ViewModel() {
    private val _state = MutableStateFlow<State>(value = initialState)
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    abstract fun onIntent(intent: Intent)

    protected abstract fun sendEffect(effect: Effect)

    private fun dispatch(result: Result) {
        _state.value = reduce(state.value, result)
    }
}