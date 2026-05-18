package com.keikuethas.irlhideandseek.mvi.lobby

import com.keikuethas.irlhideandseek.mvi.lobby.LobbyResult

object LobbyReducer {
    fun reduce(state: LobbyState, result: LobbyResult): LobbyState {
        return when (result) {
            LobbyResult.ReadyStatusChanged -> state.copy(isReady = !state.isReady)

            is LobbyResult.RoleChangeDialogStateSet -> state.copy(showRoleChangeDialog = result.open)

            is LobbyResult.PlayerJoined -> state.copy(
                players = state.players + (result.name to result.role)
            )

            is LobbyResult.PlayerQuit -> state.copy(
                players = state.players.filterNot { it.first == result.name }
            )

            is LobbyResult.ReadyStatusSet -> state.copy(isReady = result.ready)

            is LobbyResult.RoleChanged ->
                if (state.players.any { it.first == result.name })
                    state.copy(
                        players = state.players.filterNot { it.first == result.name } + (result.name to result.role)
                    )
                else state

            is LobbyResult.QuitDialogStateSet -> state.copy(showQuitDialog = result.open)

            is LobbyResult.InitState -> state.copy(
                roomName = result.roomName,
                playerName = result.playerName,
                playerRole = result.playerRole,
                players = result.players,
                roles = result.roles,
                isReady = result.isReady,
                isLoading = false,
                error = null
            )

            is LobbyResult.Loading -> state.copy(isLoading = result.isLoading)

            // --- ДОБАВЛЕННЫЕ ВЕТКИ ---
            is LobbyResult.Error -> state.copy(
                error = result.message,
                isLoading = false
            )

            is LobbyResult.PlayerRoleChanged -> state.copy(
                playerRole = result.newRoleId
            )

            is LobbyResult.SetPlayerInfo -> state.copy(
                playerName = result.playerName,
                roomName = result.roomName
            )
        }
    }
}