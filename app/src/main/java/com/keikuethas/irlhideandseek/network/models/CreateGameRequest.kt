package com.keikuethas.irlhideandseek.network.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateGameRequest(
    val name: String,
    val center_lat: Double,
    val center_lng: Double,
    val safe_zone_radius: Int, //concern int
    val min_zone_radius: Int, //concern int
    val zone_shrink_interval: Int,
    val game_duration: Int,
    val time_to_hide: Int,
    val host_player: HostPlayer,
    val game_roles: Map<String, RoleParams>,
    val roles_abilities: Map<String, Map<String, AbilityParams>>,
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
    val victory_condition: String
)

@Serializable
data class AbilityParams(
    val duration_seconds: Int,
    val number_uses: Int,
    val recharge_time: Int,
    val addition_data: Map<String, Double>
)

@Serializable
data class EventConfig(
    val activation_frequency: String,
    val addition_data: Map<String, Double>
)

@Serializable
data class CreateGameResponse(
    val game: GameInfo,
    val host_player_id: String
)