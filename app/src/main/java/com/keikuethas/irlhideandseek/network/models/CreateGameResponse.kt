package com.keikuethas.irlhideandseek.network.models

import android.os.Parcelable
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.ActivationFrequency
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class GameInfo(
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
    val roles: List<RoleInfo> = emptyList(),
    val players: List<PlayerInfo> = emptyList()
)

@Parcelize
@Serializable
data class AbilityInfo(
    val id: String,
    val ability_type: AbilityType,
    val number_uses: Int,
    val recharge_time: Int,
    val duration_seconds: Int?,
    val data: Map<String, Double>
): Parcelable

@Parcelize
@Serializable
data class EventInfo(
    val id: String,
    val type: String,
    val activation_frequency: ActivationFrequency,
    val event_data: Map<String, Double>
): Parcelable