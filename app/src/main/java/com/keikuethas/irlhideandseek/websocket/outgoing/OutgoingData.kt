package com.keikuethas.irlhideandseek.websocket.outgoing

import com.keikuethas.irlhideandseek.model.AbilityType
import kotlinx.serialization.Serializable

@Serializable
data class LocationData(
    val lat: Double,
    val lng: Double
)

@Serializable
data class UseAbilityData(
    val ability_type: AbilityType,
    val center_lat: Double? = null,
    val center_lng: Double? = null
)

@Serializable
data class ChangeRoleData(
    val role_id: String
)

@Serializable
data class ChangeReadyStatusData(
    val status: Boolean
)

@Serializable
data class HunterFoundPlayerData(
    val founded_player_id: String
)