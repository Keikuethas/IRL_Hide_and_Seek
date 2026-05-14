package com.keikuethas.irlhideandseek.Websocket

import com.google.gson.annotations.SerializedName

data class AbilityData(
    @SerializedName("id") val id: String,
    @SerializedName("ability_type") val abilityType: AbilityType,
    @SerializedName("number_uses") val numberUses: Int,
    @SerializedName("recharge_time") val rechargeTime: Int,
    @SerializedName("duration_seconds") val durationSeconds: Int?,
    @SerializedName("data") val additionalData: Map<String, Any>?
)

data class EventDataConfig(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: EventType,
    @SerializedName("activation_frequency") val activationFrequency: ActivationFrequency,
    @SerializedName("event_data") val eventData: Map<String, Any>?
)

data class RoleData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("health") val health: Int,
    @SerializedName("victory_condition") val victoryCondition: VictoryCondition,
    @SerializedName("abilities") val abilities: List<AbilityData>,
    @SerializedName("events") val events: List<EventDataConfig>
)

data class GameData(
    @SerializedName("id") val id: String,
    @SerializedName("game_code") val gameCode: String,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: GameStatus,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("safe_zone_center_lat") val safeZoneCenterLat: Double,
    @SerializedName("safe_zone_center_lng") val safeZoneCenterLng: Double,
    @SerializedName("safe_zone_radius") val safeZoneRadius: Double,
    @SerializedName("min_zone_radius") val minZoneRadius: Double,
    @SerializedName("zone_shrink_interval") val zoneShrinkInterval: Int,
    @SerializedName("time_to_hide") val timeToHide: Int,
    @SerializedName("game_duration") val gameDuration: Int,
    @SerializedName("zone_boundary_damage") val zoneBoundaryDamage: Int,
    @SerializedName("current_safe_zone_id") val currentSafeZoneId: String?,
    @SerializedName("last_shrink_at") val lastShrinkAt: String?,
    @SerializedName("roles") val roles: List<RoleData>
)

data class PlayerData(
    @SerializedName("id") val id: String,
    @SerializedName("game_id") val gameId: String,
    @SerializedName("role_id") val roleId: String,
    @SerializedName("name") val name: String,
    @SerializedName("health") val health: Int,
    @SerializedName("is_alive") val isAlive: Boolean,
    @SerializedName("location_lat") val locationLat: Double,
    @SerializedName("location_lng") val locationLng: Double,
    @SerializedName("last_location_update") val lastLocationUpdate: String,
    @SerializedName("is_player_ready") val isPlayerReady: Boolean,
    @SerializedName("trapped_until") val trappedUntil: String?
)