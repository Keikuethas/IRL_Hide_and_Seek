package com.keikuethas.irlhideandseek.mvi.newGame.map

import android.os.Parcelable
import com.keikuethas.irlhideandseek.view.map.YandexMapState
import com.yandex.mapkit.geometry.Point
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class MapState(
    val yandexMapState: YandexMapState = YandexMapState(),
    val safeZoneRadius: Int = 300,
    val minSafeZoneRadius: Int = 50,
    val showQuitDialog: Boolean = false,
    val followCamera: Boolean = true,
    val location: @RawValue Point? = null
) : Parcelable {
}

sealed interface MapIntent {
    data class ChangeZoneRange(val min: Int, val max: Int) : MapIntent
    data object ChangeFollowStatus : MapIntent
    data object RequestQuit : MapIntent
    data object Save : MapIntent
    data object ReportCameraMoveFinished : MapIntent
    data class ReportCameraPositionChanged(val pos: Point) : MapIntent
    data object ConfirmQuit : MapIntent
    data object DeclineQuit : MapIntent
    data class Initialize(val state: MapState) : MapIntent
    data class SetLocation(val location: Point): MapIntent
}

sealed interface MapResult {
    data class ZoneRangeChanged(val min: Int, val max: Int) : MapResult
    data object FollowStatusChanged : MapResult
    data object StopCameraMovement : MapResult
    data class QuitDialogStateSet(val open: Boolean) : MapResult
    data class Initialized(val state: MapState) : MapResult
    data class LocationSet(val location: Point) : MapResult
    data class LocationUpdated(val location: Point) : MapResult
}

sealed interface MapEffect {
    data object Quit : MapEffect
}