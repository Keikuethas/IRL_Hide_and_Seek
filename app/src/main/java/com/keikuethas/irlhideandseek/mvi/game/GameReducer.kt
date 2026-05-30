package com.keikuethas.irlhideandseek.mvi.game

import kotlin.math.ceil

object GameReducer {
    fun reduce(state: GameState, result: GameResult): GameState = when (result) {
        is GameResult.AbilitiesScrolled -> {
            var newValue = state.abilityListPage!! +
                    if (result.right) 1 else -1
            newValue = when {
                newValue > ceil(state.abilities.size / 2.0) -> 0
                newValue < 0 -> ceil(state.abilities.size / 2.0).toInt()
                else -> newValue
            }
            state.copy(abilityListPage = newValue)
        }

        is GameResult.AbilityListStateSet ->
            state.copy(
                abilityListPage =
                    if (result.open) 0 else null
            )

        is GameResult.AbilitySelected -> TODO()
        GameResult.CameraStopped -> state.copy(mapState = state.mapState.copy(shouldMoveCamera = false))
        GameResult.HideTimeFinished -> TODO()
        is GameResult.Initialized -> TODO()
        is GameResult.PlayerListStateSet -> TODO()
        is GameResult.ZoneAdded -> TODO()
        is GameResult.ZoneDeleted -> TODO()
    }
}