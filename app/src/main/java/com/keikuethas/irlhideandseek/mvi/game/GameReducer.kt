package com.keikuethas.irlhideandseek.mvi.game

import com.keikuethas.irlhideandseek.utils.fillColor
import com.keikuethas.irlhideandseek.utils.strokeColor
import com.keikuethas.irlhideandseek.view.map.MapObjectState
import com.keikuethas.irlhideandseek.view.map.MapObjectType

object GameReducer {
    fun reduce(state: GameState, result: GameResult): GameState = when (result) {

        is GameResult.AbilityListStateSet ->
            state.copy(
                abilityListOpen = result.open
            )

        is GameResult.AbilitySelected ->
            state.copy(
                usingAbilityOnMap = result.type
            )

        GameResult.CameraStopped ->
            state.copy(
                mapState = state.mapState.copy(shouldMoveCamera = false)
            )

        is GameResult.Initialized ->
            TODO()

        is GameResult.PlayerListStateSet ->
            state.copy(
                playerListOpen = result.open
            )

        is GameResult.ZoneAdded ->
            state.copy(
                mapState = state.mapState.copy(
                    objects = state.mapState.objects +
                            MapObjectState(
                                id = result.zoneId,
                                type = MapObjectType.Zone(
                                    strokeColor = result.type.strokeColor,
                                    fillColor = result.type.fillColor,
                                    radius = result.radius
                                ),
                                location = result.location,
                                isVisible = true,
                                followCamera = false
                            )
                )
            )

        is GameResult.ZoneDeleted ->
            state.copy(
                mapState = state.mapState.copy(
                    objects = state.mapState.objects.filterNot { it.id == result.zoneId }
                )
            )

        is GameResult.AbilityUsed -> TODO()

        is GameResult.Error ->
            state.copy(
                error = result.message
            )

        is GameResult.GameStarted ->
            state.copy(
                itsTimeToHide = false,
                secondsRemain = result.duration
            )

        is GameResult.PlayerDied ->
            state.copy(
                players = state.players.map {
                    if (it.id == result.playerId)
                        it.copy(isAlive = false)
                    else it
                }
            )

        is GameResult.PlayerMoved ->
            state.copy(
                players = state.players.map {
                    if (it.id == result.playerId)
                        it.copy(location = result.location)
                    else it
                }
            )

        is GameResult.PlayerQuit ->
            state.copy(
                players = state.players.filterNot { it.id == result.playerId }
            )

        GameResult.ErrorDismissed -> state.copy(
            error = null
        )
    }
}