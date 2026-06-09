package com.keikuethas.irlhideandseek.view

import com.keikuethas.irlhideandseek.model.DeathReason
import kotlinx.serialization.Serializable

@Serializable data object Home
@Serializable data class Lobby(
    val playerName: String,
    val roomName: String,
    val gameId: String,
    val playerId: String
)
@Serializable data class Game(val timeToHide: Int)
@Serializable data class EndScreen(
    val victory: Boolean,
    val reason: DeathReason? = null,
    val hunterId: String? = null
)

// --- экраны при создании игры ---
@Serializable data class NewGame(val playerName: String)
@Serializable data object RolesSettings
@Serializable data object MapSettings
@Serializable data object EventSettings

@Serializable data object TimeSettings
