package com.keikuethas.irlhideandseek.mvi.lobby

object LobbyReducer {
    fun reduce(state: LobbyState, result: LobbyResult): LobbyState {
        return when (result) {
            LobbyResult.ReadyStatusChanged -> state.copy(isReady = !state.isReady)

            is LobbyResult.RoleChangeDialogStateSet -> state.copy(showRoleChangeDialog = result.open)

            is LobbyResult.PlayerJoined -> state.copy(
                players = state.players + (result.name to result.role)
            )

            is LobbyResult.PlayerQuit -> state.copy(
                // Оставляем игроков, имя которых не совпадает с именем ушедшего
                players = state.players.filterNot { it.first == result.name }
            )

            is LobbyResult.ReadyStatusSet -> state.copy(isReady = result.ready)

            is LobbyResult.RoleChanged ->
                if (state.players.any { it.first == result.name }) // Если такой игрок есть в списке
                state.copy(
                    players = state.players.filterNot { it.first == result.name }
                            + (result.name to result.role)
                )
            else state // Если нет - не меняем

            is LobbyResult.QuitDialogStateSet -> state.copy(showQuitDialog = result.open)
        }
    }
}