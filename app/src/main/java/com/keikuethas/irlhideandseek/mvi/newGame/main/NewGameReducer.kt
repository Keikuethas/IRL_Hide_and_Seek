package com.keikuethas.irlhideandseek.mvi.newGame.main

import com.keikuethas.irlhideandseek.mvi.newGame.events.ESState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState

object NewGameReducer {
    fun reduce(state: NewGameState, result: NewGameResult): NewGameState =
        when (result) {
            is NewGameResult.PresetSelected -> state.copy(selectedPreset = result.presetName)
            is NewGameResult.QuitDialogStateSet -> state.copy(quitDialogOpen = result.open)
            is NewGameResult.ResetDialogStateSet -> state.copy(resetDialogOpen = result.open)
            is NewGameResult.RoomNameChanged -> state.copy(roomName = result.roomName)
            is NewGameResult.EmptyRoleCreated -> with (state.rolesSettings) {
                state.copy(
                    rolesSettings = copy(
                        roles = roles + RoleState(
                            roleName = result.roleType.toString(),
                            result.roleType
                        )
                    )
                )
            }

            is NewGameResult.EventsUpdated -> state.copy(eventSettings = result.newState)
            is NewGameResult.MapUpdated -> state.copy(mapSettings = result.newState)
            is NewGameResult.RolesUpdated -> state.copy(rolesSettings = result.newState)
            NewGameResult.ResetState -> state.copy(
                rolesSettings = RSState(),
                eventSettings = ESState(),
                mapSettings = MSState()
            )
            is NewGameResult.Error -> state.copy(error = result.message)
            is NewGameResult.SetHostName -> state.copy(hostName = result.hostName)
        }
}