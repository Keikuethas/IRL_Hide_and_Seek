package com.keikuethas.irlhideandseek.mvi.game

import android.os.Parcelable
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.model.ZoneType
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.view.map.YandexMapState
import com.yandex.mapkit.geometry.Point
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class PlayerState(
    val id: String,
    val name: String = "player",
    val roleType: RoleType = RoleType.SEEKER,
    val location: @RawValue Point,
    val isAlive: Boolean = true
): Parcelable

@Parcelize
data class GameState(
    val secondsRemain: Int = 600,
    val abilities: List<AbilityState> = emptyList(),
    val roleType: RoleType = RoleType.SEEKER,
    val players: List<PlayerState> = emptyList(),
    val playerHealth: Int = 100,
    val itsTimeToHide: Boolean = true,
    val usingAbilityOnMap: @RawValue AbilityType? = null,
    val abilityListOpen: Boolean = false,
    val playerListOpen: Boolean = false,
    val mapState: @RawValue YandexMapState = YandexMapState(),
    val usingAbilityLocation: @RawValue Point? = null,
    val error: String? = null,
    val showQuitDialog: Boolean = false
): Parcelable



sealed interface GameIntent {
    data object PlayerListOpen: GameIntent
    data object PlayerListClose: GameIntent
    data object AbilityListOpen: GameIntent
    data object AbilityListClose: GameIntent
    data class SelectAbility(val type: AbilityType): GameIntent
    data object UseAbility: GameIntent
    data object CancelUseAbility: GameIntent
    data class CatchPlayer(val playerId: String): GameIntent
    data object ReportCameraMoveFinished: GameIntent
    data object DismissError: GameIntent
    data object SelectCatch: GameIntent
    data class ReportCameraPositionChanged(val location: Point): GameIntent
data object RequestQuit: GameIntent
    data object QuitConfirmed: GameIntent
    data object QuitDeclined: GameIntent
}

sealed interface GameResult {

    data class AbilityListStateSet(val open: Boolean): GameResult
    data class PlayerListStateSet(val open: Boolean): GameResult
    data class AbilitySelected(val type: AbilityType): GameResult

    data class Initialized(
        val secondsRemain: Int,
        val abilities: List<AbilityState>,
        val roleType: RoleType,
        val players: List<PlayerState>,
        val playerHealth: Int,
        val safeZoneRadius: Float,
        val safeZoneCenter: Point
    ): GameResult
    data class ZoneAdded(
        val zoneId: String,
        val type: ZoneType,
        val location: Point,
        val radius: Float,
        ): GameResult
    data class ZoneDeleted(val zoneId: String): GameResult
    data object CameraStopped: GameResult

    data class Error(val message: String): GameResult

    data class PlayerDied(val playerId: String): GameResult

    data class PlayerQuit(val playerId: String): GameResult

    data class PlayerMoved(
        val playerId: String,
        val location: Point
    ): GameResult

    data class GameStarted(val duration: Int): GameResult

    data class AbilityUsed(val type: AbilityType): GameResult

    data object ErrorDismissed: GameResult

    data class DamageApplied(val damage: Int): GameResult

    data class LocationSet(val location: Point): GameResult

    data class LocationChanged(val location: Point): GameResult

    data class AbilityLocationUpdated(val location: Point): GameResult

    data object AbilityUseCancelled: GameResult

    data object CooldownUpdated: GameResult
    data object OpenPlayerList: GameResult
    data object TimerTick: GameResult
    data class QuitDialogStateSet(val open: Boolean): GameResult
    data class SafeZoneRadiusChanged(val radius: Float): GameResult

}

sealed interface GameEffect {
    data object GetDamage: GameEffect
    data class EndGame(
        val victory: Boolean,
        val reason: DeathReason? = null,
        val hunterName: String? = null
    ): GameEffect

    data object Quit: GameEffect
}