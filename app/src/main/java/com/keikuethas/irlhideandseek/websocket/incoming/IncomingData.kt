package com.keikuethas.irlhideandseek.websocket.incoming

import android.os.Parcelable
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.model.ZoneType
import com.keikuethas.irlhideandseek.network.models.AbilityInfo
import com.keikuethas.irlhideandseek.network.models.EventInfo
import com.keikuethas.irlhideandseek.network.models.GameInfo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// ---------- Data classes для полей входящих сообщений ----------
@Serializable
data class PongData(val server_time: String)

@Serializable
data class ConnectedPlayerData(
    val player_data: PlayerInfo,
    val game_data: GameInfo
)

@Parcelize
@Serializable
data class PlayerInfo(
    val id: String,
    val name: String,
    val health: Int,
    val is_alive: Boolean,
    val location_lat: Double,
    val location_lng: Double,
    val role_id: String,
    val is_player_ready: Boolean,
    val role_ref: RoleInfo? = null
): Parcelable

@Serializable
data class PlayerMovedData(
    val player_id: String,
    val location_lat: Double,
    val location_lng: Double,
    val timestamp: String
)

@Serializable
data class PlayerRoleChangedData(
    val player_id: String,
    val role_id: String
)

@Serializable
data class RoleChangedData(
    val role_id: String
)

@Serializable
data class ReadyStatusChangedData(
    val ready_status: Boolean
)

@Serializable
data class PlayerReadyStatusChangedData(
    val player_id: String,
    val ready_status: Boolean
)

@Serializable
data class GameStateData(
    val game_info: GameInfo,
    val player_info: PlayerInfo
)

@Serializable
data class ZoneData(
    val zone_id:String,
    val zone_type: ZoneType,
    val center_lat: Double,
    val center_lng: Double,
    val radius: Float
)

@Serializable
data class DeleteZoneData(val zone_id: String)

@Serializable
data class PlayerOnlineData(
    val player_id: String,
    val player_name: String,
    val role: String?
)

@Serializable
data class PlayerOfflineData(val player_id: String)

@Serializable
data class YouDiedData(
    val reason: DeathReason,
    val hunter_player_id: String? = null
)

@Serializable
data class PlayerDiedData(
    val reason: DeathReason,
    val player_id: String,
    val hunter_player_id: String? = null
)

@Serializable
data class TimerData(val duration_seconds: Int)

@Serializable
data class GameFinishedData(val is_victory: Boolean)

@Serializable
data class ErrorData(val message: String)

@Serializable
data class AbilityUsedData(
    val ability: AbilityType,
    val result: Int
)

//refactor
@Parcelize
@Serializable
data class RoleInfo(
    val id: String,
    val name: String,
    val health: Int,
    val victory_condition: RoleType,
    val abilities: List<AbilityInfo>,
    val events: List<EventInfo?> = emptyList()
): Parcelable

@Serializable
data class PlayerEnteredZoneData(
    val zone_id: String,
    val zone_type: ZoneType,
    val center_lat: Double,
    val center_lng: Double,
    val radius: Float
)

@Serializable
data class PlayerExitedZoneData(
    val zone_id: String,
    val zone_type: ZoneType
)

@Serializable
data class AirdropCollectedData(
    val ability: AbilityInfo
)
