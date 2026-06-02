package com.keikuethas.irlhideandseek.mvi.game

import android.os.Parcelable
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.Websocket.AbilityType
import com.keikuethas.irlhideandseek.Websocket.GameData
import com.keikuethas.irlhideandseek.Websocket.ZoneType
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.view.map.YandexMapState
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlin.reflect.KClass

@Parcelize
data class PlayerState(
    val name: String = "player",
    val roleName: String = "new role",
    val roleType: RoleType = RoleType.Seeker,
    val lat: Double = 0.0,
    val lng: Double = 0.0
): Parcelable

@Parcelize
data class ZoneState(
    val id: String,
    val boundaryColor: Int,
    val fillColor: Int,
): Parcelable

@Parcelize
data class GameState(
    val secondsRemain: Int = 600,
    val abilities: List<AbilityState> = emptyList(),
    val roleType: RoleType = RoleType.Seeker,
    val players: List<PlayerState> = emptyList(),
    val playerName: String = "me",
    val itsTimeToHide: Boolean = false,
    val usingAbilityOnMap: @RawValue KClass<out Ability>? = null,
    val abilityListPage: Int? = null, //concern
    val playerListOpen: Boolean = false,
    val mapState: @RawValue YandexMapState = YandexMapState()
): Parcelable



sealed interface GameIntent {
    data object PlayerListOpen: GameIntent
    data object PlayerListClose: GameIntent
    data object AbilityListOpen: GameIntent
    data object AbilityListClose: GameIntent
    data class ScrollAbilityList(val right: Boolean): GameIntent
    data class SelectAbility(val type: KClass<out Ability>): GameIntent
    data class UseAbility(val lat: Double, val lng: Double): GameIntent
    data class CatchPlayer(val name: String): GameIntent
    data object ReportCameraMoved: GameIntent

    // --- VM Intents (Websocket) ---
    data class Initialize(val roleID: String, val gameData: GameData): GameIntent
    data class UpdateLocation(val playerID: String, val lat: Double, val lng: Double): GameIntent
    data class AddZone(val id:String, val type: ZoneType): GameIntent
    data class DeleteZone(val id: String): GameIntent
    data class AbilityUseRespond(val type: AbilityType, val result: Int): GameIntent
    data object FinishHideTime: GameIntent
}

sealed interface GameResult {
    data class AbilitiesScrolled(val right: Boolean): GameResult
    data class AbilityListStateSet(val open: Boolean): GameResult
    data class PlayerListStateSet(val open: Boolean): GameResult
    data class AbilitySelected(val type: KClass<out Ability>): GameResult
    data class Initialized(val roleID: String, val gameData: GameData): GameResult
    data class ZoneAdded(val type: ZoneType, val zoneID: String): GameResult
    data class ZoneDeleted(val zoneID: String): GameResult
    data object HideTimeFinished: GameResult
    data object CameraStopped: GameResult
}

sealed interface GameEffect {
    data object GetDamage: GameEffect
}