package com.keikuethas.irlhideandseek.mvi.game

import android.os.SystemClock.elapsedRealtime
import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.color
import com.keikuethas.irlhideandseek.utils.fillColor
import com.keikuethas.irlhideandseek.utils.strokeColor
import com.keikuethas.irlhideandseek.view.map.MapObjectState
import com.keikuethas.irlhideandseek.view.map.MapObjectType.Marker
import com.keikuethas.irlhideandseek.view.map.MapObjectType.Zone
import com.yandex.mapkit.geometry.Point

object GameReducer {
    fun reduce(state: GameState, result: GameResult): GameState = when (result) {

        is GameResult.AbilityListStateSet ->
            state.copy(
                abilityListOpen = result.open
            )

        is GameResult.AbilitySelected ->
            state.copy(
                usingAbilityOnMap = result.type,
                mapState = state.mapState.copy(
                    objects = state.mapState.objects +
                            MapObjectState(
                                id = "ZonePreview",
                                type = Zone(
                                    strokeColor = result.type.color,
                                    fillColor = result.type.color.copy(alpha = 0.05F),
                                    radius = state.abilities.find { it.type == result.type }!!
                                        .params.find { it.first == "radius" }!!
                                        .second.toFloat()
                                ),
                                location = Point(0.0, 0.0),
                                isVisible = true,
                                followCamera = true
                            )
                )
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
                                type = Zone(
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

        is GameResult.AbilityUsed -> state.copy(
            abilities = state.abilities.map { ability ->
                if (result.type == ability.type)
                    ability.copy(

                        cooldownEndTime = elapsedRealtime() + ability.rechargeTime,

                        params = ability.params.map {
                            if (it.first == "number_uses")
                                it.first to (it.second.toInt() - 1)
                            else it
                        }
                    )
                else ability
            }
                .filterNot { ability ->
                    ability.params.find { it.first == "number_uses" }!!
                        .second.toInt() == 0
                },

            mapState = state.mapState.copy(
                objects = state.mapState.objects.filterNot { it.id == "ZonePreview" }
            )
        )

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

        is GameResult.DamageApplied -> state.copy(
            playerHealth = state.playerHealth - result.damage
        )

        is GameResult.LocationSet -> state.copy(
            mapState = state.mapState.copy(
                shouldMoveCamera = true,
                cameraPosition = result.location,
                zoom = 15.25F,
                objects = state.mapState.objects +
                        MapObjectState(
                            id = "Player",
                            type = Marker(
                                strokeColor = Color.Green,
                                fillColor = state.roleType.color,
                                label = null
                            ),
                            location = result.location,
                            isVisible = true,
                            followCamera = false
                        )
            )
        )

        is GameResult.LocationChanged -> state.copy(
            mapState = state.mapState.copy(
                objects = state.mapState.objects.map {
                    if (it.id == "Player") it.copy(
                        location = result.location
                    ) else it
                }
            )
        )

        is GameResult.AbilityLocationUpdated -> state.copy(
            usingAbilityLocation = result.location
        )

        GameResult.AbilityUseCancelled -> state.copy(
            usingAbilityOnMap = null,
            usingAbilityLocation = null,
            abilityListOpen = false,
            playerListOpen = false,
            mapState = state.mapState.copy(
                objects = state.mapState.objects.filterNot { it.id == "ZonePreview" }
            )
        )

        is GameResult.CooldownUpdated -> state.copy(
            abilities = state.abilities.map {
                if (it.cooldownProgress < 1F)
                    it.copy(
                        remainingTime = maxOf(0L, it.cooldownEndTime - elapsedRealtime())
                    ) else it
            }
        )

        GameResult.OpenPlayerList -> state.copy(
            abilityListOpen = false,
            playerListOpen = true
        )
    }
}