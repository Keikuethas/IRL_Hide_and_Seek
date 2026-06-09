package com.keikuethas.irlhideandseek.mvi.lobby

import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo

object LobbyReducer {
    fun reduce(state: LobbyState, result: LobbyResult): LobbyState = when (result) {
        LobbyResult.ReadyStatusChanged -> state.copy(isReady = !state.isReady)

        is LobbyResult.RoleChangeDialogStateSet -> state.copy(showRoleChangeDialog = result.open)

        is LobbyResult.PlayerJoined -> state.copy(
            players = state.players + PlayerInfo(
                id = result.id,
                name = result.playerName,
                health = 100,
                is_alive = true,
                location_lat = 0.0,
                location_lng = 0.0,
                role_id = result.roleId,
                is_player_ready = false
            )
        )

        is LobbyResult.PlayerQuit -> state.copy(
            players = state.players.filterNot { it.id == result.id }
        )

        is LobbyResult.ReadyStatusSet -> state.copy(isReady = result.ready)

        is LobbyResult.RoleChanged -> state.copy(
            playerRole = getRoleById(state.roles, result.role)?.name ?: "?",
            showRoleChangeDialog = false
        )

        is LobbyResult.QuitDialogStateSet -> state.copy(showQuitDialog = result.open)

        is LobbyResult.InitState -> state.copy(
            roomName = result.roomName,
            roomCode = result.roomCode,
            playerName = result.playerName,
            playerRole = result.playerRole,
            players = result.players,
            roles = result.roles,
            isReady = result.isReady,
            isLoading = false,
            error = null
        )

        is LobbyResult.Loading -> state.copy(isLoading = result.isLoading)

        is LobbyResult.Error -> state.copy(
            error = result.message,
            isLoading = false
        )

        is LobbyResult.PlayerRoleChanged -> state.copy(
            players = state.players.map {
                if (it.id == result.id) it.copy(
                    role_id = result.newRoleId
                ) else it
            },
        )

        is LobbyResult.SetPlayerInfo -> state.copy(
            playerName = result.playerName,
            roomName = result.roomName
        )

        is LobbyResult.PlayerReadyStatusSet -> state.copy(
            players = state.players.map {
                if (it.id == result.playerId)
                    it.copy(is_player_ready = result.ready)
                else it
            }
        )
    }
}

private fun getRoleById(roles: List<RoleInfo>, id: String) =
    roles.find { it.id == id }