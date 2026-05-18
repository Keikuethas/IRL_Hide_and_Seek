package com.keikuethas.irlhideandseek.network.models

import kotlinx.serialization.Serializable

@Serializable
data class JoinGameRequest(
    val name: String,
    val player_location_lat: Double,
    val player_location_lng: Double
)