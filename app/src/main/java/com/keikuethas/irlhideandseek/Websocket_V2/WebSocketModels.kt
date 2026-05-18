package com.keikuethas.irlhideandseek.Websocket_V2
import com.keikuethas.irlhideandseek.network.models.AbilityInfo
import com.keikuethas.irlhideandseek.network.models.EventInfo
import com.keikuethas.irlhideandseek.network.models.GameInfo

// WebSocketModels.kt
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonElement

// ---------- Исходящие сообщения (клиент -> сервер) ----------
@Serializable
data class OutgoingPing(
    val type: String = "ping",
    val data: JsonObject = JsonObject(emptyMap())
)

@Serializable
data class OutgoingUpdateLocation(
    val type: String = "update_location",
    val data: LocationData
)

@Serializable
data class LocationData(
    val lat: Double,
    val lng: Double
)

@Serializable
data class OutgoingUseAbility(
    val type: String = "use_ability",
    val data: UseAbilityData
)

@Serializable
data class UseAbilityData(
    val ability_type: String,
    val center_lat: Double? = null,
    val center_lng: Double? = null
)

@Serializable
data class OutgoingChangeRole(
    val type: String = "change_role",
    val data: ChangeRoleData
)
@Serializable
data class ChangeRoleData(
    val role_id: String
)

@Serializable
data class OutgoingChangeReadyStatus(
    val type: String = "change_ready_status",
    val data: ChangeReadyStatusData
)

@Serializable
data class ChangeReadyStatusData(
    val status: Boolean
)

@Serializable
data class OutgoingGetGameState(
    val type: String = "get_game_state",
    val data: JsonObject = JsonObject(emptyMap())
)

@Serializable
data class OutgoingHunterFoundPlayer(
    val type: String = "hunter_found_player",
    val data: HunterFoundPlayerData
)

@Serializable
data class HunterFoundPlayerData(
    val founded_player_id: String
)

// ---------- Входящие сообщения (сервер -> клиент) ----------
@Serializable
sealed class IncomingMessage {
    @Serializable
    @SerialName("pong")
    data class Pong(val data: PongData) : IncomingMessage()

    @Serializable
    @SerialName("websocket_connected_player")
    data class WebSocketConnectedPlayer(val data: ConnectedPlayerData) : IncomingMessage()

    @Serializable
    @SerialName("player_moved")
    data class PlayerMoved(val data: PlayerMovedData) : IncomingMessage()

    @Serializable
    @SerialName("role_changed")
    data class RoleChanged(val data: RoleChangedData) : IncomingMessage()

    @Serializable
    @SerialName("ready_status_changed")
    data class ReadyStatusChanged(val data: ReadyStatusChangedData) : IncomingMessage()

    @Serializable
    @SerialName("game_state")
    data class GameState(val data: GameStateData) : IncomingMessage()

    @Serializable
    @SerialName("create_zone")
    data class CreateZone(val data: ZoneData) : IncomingMessage()

    @Serializable
    @SerialName("delete_zone")
    data class DeleteZone(val data: DeleteZoneData) : IncomingMessage()

    @Serializable
    @SerialName("player_online")
    data class PlayerOnline(val data: PlayerOnlineData) : IncomingMessage()

    @Serializable
    @SerialName("player_offline")
    data class PlayerOffline(val data: PlayerOfflineData) : IncomingMessage()

    @Serializable
    @SerialName("you_died")
    data class YouDied(val data: YouDiedData) : IncomingMessage()

    @Serializable
    @SerialName("player_died")
    data class PlayerDied(val data: PlayerDiedData) : IncomingMessage()

    @Serializable
    @SerialName("start_timer_for_game")
    data class StartTimerForGame(val data: TimerData) : IncomingMessage()

    @Serializable
    @SerialName("game_finished")
    data class GameFinished(val data: GameFinishedData) : IncomingMessage()

    @Serializable
    @SerialName("error")
    data class Error(
        val message: String
    ) : IncomingMessage()
}

// ---------- Data classes для полей входящих сообщений ----------
@Serializable
data class PongData(val server_time: String)

@Serializable
data class ConnectedPlayerData(
    val player_data: PlayerInfo,
    val game_data: GameInfo
)

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
    val role_ref: RoleFull? = null
)

@Serializable
data class GameInfo(
    val id: String,
    val game_code: String,
    val name: String,
    val status: String,
    val safe_zone_center_lat: Double,
    val safe_zone_center_lng: Double,
    val safe_zone_radius: Float,
    // ... другие поля по необходимости
)

@Serializable
data class PlayerMovedData(
    val player_id: String,
    val location_lat: Double,
    val location_lng: Double,
    val timestamp: String
)

@Serializable
data class RoleChangedData(
    val role_id: String
)

@Serializable
data class ReadyStatusChangedData(
    val status: Boolean
)

@Serializable
data class GameStateData(
    val game_info: GameInfo,
    val player_info: PlayerInfo
)

@Serializable
data class ZoneData(
    val zone_id: String,
    val zone_type: String,
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
    val reason: String,
    val hunter_player_id: String? = null
)

@Serializable
data class PlayerDiedData(
    val reason: String,
    val player_id: String,
    val hunter_player_id: String? = null
)

@Serializable
data class TimerData(val duration_seconds: Int)

@Serializable
data class GameFinishedData(val is_victory: Boolean)

@Serializable
data class ErrorData(val message: String)
// WebSocketModels.kt (дополнение)

@Serializable
data class GameInfoFull(
    val id: String,
    val game_code: String,
    val name: String,
    val status: String,
    val created_at: String,
    val safe_zone_center_lat: Double,
    val safe_zone_center_lng: Double,
    val safe_zone_radius: Float,
    val min_zone_radius: Float,
    val zone_shrink_interval: Int,
    val game_duration: Int,
    val time_to_hide: Int,
    val zone_boundary_damage: Int,
    val current_safe_zone_id: String?,
    val last_shrink_at: String?,
    val roles: List<RoleFull>
)

@Serializable
data class RoleFull(
    val id: String,
    val name: String,
    val health: Int,
    val victory_condition: String,
    val abilities: List<AbilityInfo>,
    val events: List<EventInfo?> = emptyList()
)

@Serializable
data class PlayerInfoFull(
    val id: String,
    val game_id: String,
    val name: String,
    val role_id: String,
    val health: Int,
    val is_alive: Boolean,
    val location_lat: Double,
    val location_lng: Double,
    val last_location_update: String,
    val trapped_until: String?,
    val player_data: JsonElement? = null,
    val is_player_ready: Boolean,
    val is_online: Boolean,
    val role_ref: RoleFull?
)

// Вспомогательный объект для пустого JSON
internal val emptyJsonObject = JsonObject(emptyMap())