package com.keikuethas.irlhideandseek.mvi.newGame.main

import android.os.Parcelable
import com.keikuethas.irlhideandseek.GameSettings
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.mvi.newGame.events.ESState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSState
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeState
import kotlinx.parcelize.Parcelize

// Заглушка для состояния карты (пока не реализована) todo
@Parcelize
data class MSState(val isConfigured: Boolean = false) : Parcelable

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
    val mapSettings: MSState = MSState(),
    val hostName: String = "",
    val hostLocationLat: Double = 55.751244,
    val hostLocationLng: Double = 37.618423,
    val error: String? = null
) : Parcelable {

    val eventsConfigured: Boolean get() = eventSettings.events.isNotEmpty()
    val missingRoles: List<RoleType> get() = RoleType.entries.filterNot {entry -> rolesSettings.roles.any {it.type == entry} }
    val canCreateGame: Boolean get() = true// missingRoles.isEmpty() && eventsConfigured && mapSettings.isConfigured

    val gameSettings: GameSettings
        get() = GameSettings(
            name = roomName,
            center_lat = 0.0,
            center_lng = 0.0,
            safe_zone_radius = TODO(),
            min_zone_radius = TODO(),
            zone_shrink_interval = TODO(),
            game_duration = TODO(),
            time_to_hide = TODO(),
            host_name = TODO(),
            host_player_location_lat = TODO(),
            host_player_location_lng = TODO(),
            game_roles = TODO(),
            roles_abilities = TODO(),
            events_configurations = TODO(),
            roles_events = TODO()
        )
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

    data object DismissError : NewGameIntent
}

sealed interface NewGameResult {
    data class SetHostName(val hostName: String) : NewGameResult
    data class QuitDialogStateSet(val open: Boolean) : NewGameResult
    data class ResetDialogStateSet(val open: Boolean) : NewGameResult
    data class PresetSelected(val presetName: String) : NewGameResult
    data class RoomNameChanged(val roomName: String) : NewGameResult
    data class EmptyRoleCreated(val roleType: RoleType) : NewGameResult
    data object ResetState : NewGameResult

    data class RolesUpdated(val newState: RSState) : NewGameResult
    data class EventsUpdated(val newState: ESState) : NewGameResult
    data class MapUpdated(val newState: MSState) : NewGameResult

    data class Error(val message: String) : NewGameResult
}

sealed interface NewGameEffect {
    data object Quit : NewGameEffect
    data class JoinGame(val gameId: String, val playerId: String) : NewGameEffect
    data object GoToRoles: NewGameEffect
    data object GoToEvents: NewGameEffect
    data object GoToMap: NewGameEffect
    data object GoToTime: NewGameEffect
}

