package com.keikuethas.irlhideandseek.data

import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.model.ZoneType
import com.keikuethas.irlhideandseek.network.models.GameInfo
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo

sealed interface GameEvent {

    data class ConnectionFailed(val message: String) : GameEvent

    data class PlayerQuit(val playerId: String) : GameEvent

    data class Error(val message: String) : GameEvent

    data class CreateZone(
        val zoneId: String,
        val zoneType: ZoneType,
        val centerLat: Double,
        val centerLng: Double,
        val radius: Float
    ) : GameEvent

    data class DeleteZone(
        val zoneId: String
    ) : GameEvent

    data class GameFinished(
        val victory: Boolean
    ) : GameEvent

    data class PlayerDied(
        val playerId: String,
        val hunterId: String?,
        val reason: DeathReason
    ) : GameEvent

    data class PlayerMoved(
        val playerId: String,
        val locationLat: Double,
        val locationLng: Double,
        val timestamp: String
    ) : GameEvent

    data class StartTimerForGame(
        val duration: Int
    ) : GameEvent

    data class YouDied(
        val hunterId: String?,
        val reason: DeathReason
    ) : GameEvent

    data class AbilityUsed(
        val ability: AbilityType,
        val result: Int
    ) : GameEvent

    data class LocationUpdated(
        val latitude: Double,
        val longitude: Double
    ) : GameEvent

    data object LocationPermissionRevoked: GameEvent

    data object LocationProvidersDisabled: GameEvent

    data class GameState(
        val game: GameInfo,
        val player: PlayerInfo
    ): GameEvent

    data class PlayerEnteredZone(
        val zoneId: String,
        val zoneType: ZoneType,
        val centerLat: Double,
        val centerLng: Double,
        val radius: Float
    ): GameEvent

    data class PlayerExitedZone(
        val zoneId: String,
        val zoneType: ZoneType
    ): GameEvent

    data class AirdropCollected(
        val abilityId: String,
        val ability: AbilityType,
        val numberUses: Int,
        val rechargeTime: Int,
        val durationSeconds: Int?,
        val data: Map<String, Double>

    ): GameEvent

    data class ApplyDamage(
        val damage: Int
    ): GameEvent

    data class SafeZoneUpdated(
        val zoneId: String,
        val radius: Float
    ): GameEvent
}