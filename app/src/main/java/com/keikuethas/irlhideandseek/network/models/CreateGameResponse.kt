package com.keikuethas.irlhideandseek.network.models

import com.keikuethas.irlhideandseek.Websocket_V2.RoleFull
import kotlinx.serialization.Serializable

@Serializable
data class CreateGameResponse(
    val game: GameInfo,
    val host_player_id: String
)

@Serializable
data class GameInfo(
    val id: String,
    val game_code: String,
    val name: String,
    val status: String,
    val created_at: String,
    val zone_boundary_damage: Int,
    val safe_zone_center_lat: Double,
    val safe_zone_center_lng: Double,
    val safe_zone_radius: Float,
    val min_zone_radius: Float,
    val zone_shrink_interval: Int,
    val game_duration: Int,
    val time_to_hide: Int,
    val current_safe_zone_id: String?,
    val last_shrink_at: String?,
    val roles: List<RoleFull> = emptyList()  // ← обязате= emptyList()льно добавить
)

@Serializable
data class RoleInfo(
    val id: String,
    val name: String,
    val health: Int,
    val victory_condition: String,
    val abilities: List<AbilityInfo>,
    val events: List<EventInfo>
)

@Serializable
data class AbilityInfo(
    val id: String,
    val ability_type: String,
    val number_uses: Int,
    val recharge_time: Int,
    val duration_seconds: Int?,
    val data: Map<String, Double>
)

@Serializable
data class EventInfo(
    val id: String,
    val type: String,
    val activation_frequency: String,
    val event_data: Map<String, Double>
)