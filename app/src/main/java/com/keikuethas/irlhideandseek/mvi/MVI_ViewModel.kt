package com.keikuethas.irlhideandseek.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
//describe
abstract class MVI_ViewModel<State: Any, Intent: Any, Effect: Any, Result: Any>(
    initialState: State
): ViewModel() {
    private val _state = MutableStateFlow(value = initialState)
    val state = _state.asStateFlow()
    private val _effect = MutableSharedFlow<Effect>(extraBufferCapacity = 1)
    val effect = _effect.asSharedFlow()

    abstract fun onIntent(intent: Intent)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.emit(effect) }
    }

    protected abstract fun reduce(state: State, result: Result): State

    protected fun dispatch(result: Result) {
        _state.value = reduce(state.value, result)
    }
}