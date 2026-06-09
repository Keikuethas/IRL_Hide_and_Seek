package com.keikuethas.irlhideandseek.mvi.endscreen

object EndReducer {
    fun reduce(state: EndState, result: EndResult): EndState = when(result) {
        is EndResult.Init -> result.state
    }
}