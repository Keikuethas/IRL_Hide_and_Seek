package com.keikuethas.irlhideandseek.mvi.newGame.map

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.keikuethas.irlhideandseek.view.map.MapObjectState
import com.keikuethas.irlhideandseek.view.map.MapObjectType.Zone

object MapReducer {
    fun reduce(state: MapState, result: MapResult): MapState = when (result) {
        is MapResult.ZoneRangeChanged -> with(result) {
            state.copy(
                safeZoneRadius = max,
                minSafeZoneRadius = min,
                yandexMapState = state.yandexMapState.copy(
                    objects = state.yandexMapState.objects.map {
                        it.copy(
                            type = (it.type as Zone).copy(
                                radius = (if (it.id == "big") max else min).toFloat()
                            )
                        )
                    }
                )
            )
        }

        MapResult.FollowStatusChanged -> with(state) {
            Log.i("REDUCER", state.toString())
            copy(
                followCamera = !followCamera,
                yandexMapState = yandexMapState.copy(
                    objects = yandexMapState.objects.map { it.copy(followCamera = !followCamera) }
                )
            )
        }

        is MapResult.QuitDialogStateSet -> state.copy(showQuitDialog = result.open)
        MapResult.StopCameraMovement -> with(state) {
            copy(yandexMapState = yandexMapState.copy(shouldMoveCamera = false))
        }

        is MapResult.Initialized -> result.state
        is MapResult.LocationSet -> with(state) {
            copy(
                location = result.location,
                yandexMapState = yandexMapState.copy(
                    shouldMoveCamera = true,
                    cameraPosition = result.location,
                    zoom = 15f,
                    objects = listOf(
                        MapObjectState(
                            id = "big",
                            type = Zone(
                                strokeColor = Color.Blue,
                                fillColor = Color.Blue.copy(alpha = 0.05F),
                                radius = safeZoneRadius.toFloat()
                            ),
                            location = result.location,
                            followCamera = followCamera
                        ),

                        MapObjectState(
                            id = "small",
                            type = Zone(
                                strokeColor = Color.Red,
                                fillColor = Color.Red.copy(alpha = 0.05F),
                                radius = minSafeZoneRadius.toFloat()
                            ),
                            location = result.location,
                            followCamera = followCamera
                        )
                    )
                )
            )
        }

        is MapResult.LocationUpdated -> with(state) {
            copy(
                location = result.location,
                yandexMapState = yandexMapState.copy(cameraPosition = result.location)
            )
        }
    }
}