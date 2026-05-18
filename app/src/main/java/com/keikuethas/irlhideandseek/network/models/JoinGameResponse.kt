package com.keikuethas.irlhideandseek.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinGameResponse(
    @SerialName("game_id")
    val game_id: String,
    @SerialName("player_id")
    val player_id: String,
    @SerialName("player_name")
    val player_name: String,
    @SerialName("game_status")
    val game_status: String,
    @SerialName("ws_url")
    val ws_url: String
)