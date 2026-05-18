// network/models/CreateGameRequest.kt
package com.keikuethas.irlhideandseek.network.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(
    val name: String,
    val center_lat: Double,
    val center_lng: Double,
    val safe_zone_radius: Double,
    val min_zone_radius: Double,
    val zone_shrink_interval: Int,
    val game_duration: Int,
    val time_to_hide: Int,
    val host_player: HostPlayer,
    val game_roles: Map<String, RoleParams>,
    val roles_abilities: Map<String, Map<String, AbilityParams>>,
    val events: List<String>,
    val roles_events: Map<String, List<String>>,
    val events_configurations: Map<String, EventConfig>
)

@Serializable
data class HostPlayer(
    val host_name: String,
    val host_player_location_lat: Double,
    val host_player_location_lng: Double
)

@Serializable
data class RoleParams(
    val health: Int,
    val victory_condition: String  // "SEEKER" или "HIDER"
)

@Serializable
data class AbilityParams(
    val duration_seconds: Int,
    val number_uses: Int,
    val recharge_time: Int,
    val addition_data: Map<String, Double> = emptyMap()
)

@Serializable
data class EventConfig(
    val activation_frequency: String, // "FREQUENT", "RARE", "COMMON"
    val addition_data: Map<String, Double> = emptyMap()
)