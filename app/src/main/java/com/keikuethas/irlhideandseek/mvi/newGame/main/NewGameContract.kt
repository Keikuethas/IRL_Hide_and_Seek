package com.keikuethas.irlhideandseek.mvi.newGame.main

import android.os.Parcelable
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.mvi.newGame.events.ESState
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSState
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeState
import kotlinx.parcelize.Parcelize

// CONCERN: нужно ли хранить состояния настроек, если они уже хранятся в репозитории?

@Parcelize
data class NewGameState(
    val quitDialogOpen: Boolean = false,
    val resetDialogOpen: Boolean = false,
    val presetList: List<String> = listOf("<new>"),
    val selectedPreset: String = "<new>",
    val roomName: String = "New game",
    val timeSettings: TimeState = TimeState(),
    val rolesSettings: RSState = RSState(),
    val eventSettings: ESState = ESState(),
    val mapSettings: MapState = MapState()
) : Parcelable {
    val mapConfigured get() = mapSettings.location != null

    val eventsConfigured: Boolean get() = eventSettings.events.isNotEmpty()
    val missingRoles: List<RoleType> get() = RoleType.entries.filterNot {entry -> rolesSettings.roles.any {it.type == entry} }
    val canCreateGame: Boolean get() = missingRoles.isEmpty() && eventsConfigured && mapConfigured
}

sealed interface NewGameIntent {
    data object QuitRequest : NewGameIntent
    data class QuitDialogRespond(val confirmed: Boolean) : NewGameIntent
    data object CreateGame : NewGameIntent
    data class SelectPreset(val presetName: String) : NewGameIntent
    data class ChangeRoomName(val roomName: String) : NewGameIntent
    data object ResetSettings : NewGameIntent
    data class ResetDialogRespond(val confirmed: Boolean) : NewGameIntent
    data class CreateEmptyRole(val roleType: RoleType) : NewGameIntent

    data object GoToRoles: NewGameIntent
    data object GoToEvents: NewGameIntent
    data object GoToMap: NewGameIntent
    data object GoToTime: NewGameIntent
}

sealed interface NewGameResult {
    data class QuitDialogStateSet(val open: Boolean) : NewGameResult
    data class ResetDialogStateSet(val open: Boolean) : NewGameResult
    data class PresetSelected(val presetName: String) : NewGameResult
    data class RoomNameChanged(val roomName: String) : NewGameResult
    data class EmptyRoleCreated(val roleType: RoleType) : NewGameResult
    data object ResetState : NewGameResult

    data class RolesUpdated(val newState: RSState) : NewGameResult
    data class EventsUpdated(val newState: ESState) : NewGameResult
    data class MapUpdated(val newState: MapState) : NewGameResult
}

sealed interface NewGameEffect {
    data object Quit : NewGameEffect
    data class JoinGame(val gameID: String) : NewGameEffect
    data object GoToRoles: NewGameEffect
    data object GoToEvents: NewGameEffect
    data object GoToMap: NewGameEffect
    data object GoToTime: NewGameEffect
}