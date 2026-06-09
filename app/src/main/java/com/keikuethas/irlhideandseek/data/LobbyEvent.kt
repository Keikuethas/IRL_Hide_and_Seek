package com.keikuethas.irlhideandseek.data

import com.keikuethas.irlhideandseek.network.models.GameInfo
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo

sealed interface LobbyEvent {
    data class InitState(val game: GameInfo, val player: PlayerInfo) : LobbyEvent
    data class PlayerRoleChanged(val playerId: String, val newRoleId: String) : LobbyEvent
    data class ReadyStatusChanged(val ready: Boolean) : LobbyEvent
    data class PlayerReadyStatusChanged(val playerId: String, val ready: Boolean) : LobbyEvent
    data class PlayerJoined(val playerId: String, val playerName: String, val role: String?, val roleRef: RoleInfo) :
        LobbyEvent

    data class PlayerQuit(val playerId: String) : LobbyEvent
    data class Error(val message: String) : LobbyEvent
    data class RoleChanged(val newRoleId: String) : LobbyEvent
    data class ConnectionFailed(val message: String) : LobbyEvent

    data class StartTimerToHide(val duration: Int): LobbyEvent
}