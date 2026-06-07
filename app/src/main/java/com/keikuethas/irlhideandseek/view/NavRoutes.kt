package com.keikuethas.irlhideandseek.view

import kotlinx.serialization.Serializable

@Serializable data object Home
@Serializable data class Lobby(
    val playerName: String,
    val roomName: String,
    val gameId: String,
    val playerId: String
)
@Serializable data class Game(val timeToHide: Int)
@Serializable data object EndScreen

// --- экраны при создании игры ---
@Serializable data class NewGame(val playerName: String)
@Serializable data object RolesSettings
@Serializable data object MapSettings
@Serializable data object EventSettings

@Serializable data object TimeSettings
