package com.keikuethas.irlhideandseek.mvi.newGame.map

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
                    objects = state.yandexMapState.objects.map { obj ->
                        when (obj.id.trim()) {
                            "big" -> obj.copy(type = (obj.type as Zone).copy(radius = max.toFloat()))
                            "small" -> obj.copy(type = (obj.type as Zone).copy(radius = min.toFloat()))
                            else -> obj
                        }
                    }
                )
            )
        }

        MapResult.FollowStatusChanged -> with(state) {
            val newFollowState = !followCamera
            copy(
                followCamera = newFollowState,
                yandexMapState = yandexMapState.copy(
                    objects = yandexMapState.objects.map { it.copy(followCamera = newFollowState) }
                )
            )
        }

        is MapResult.QuitDialogStateSet -> state.copy(showQuitDialog = result.open)
        MapResult.StopCameraMovement -> state.copy(
            yandexMapState = state.yandexMapState.copy(
                shouldMoveCamera = false
            )
        )

        is MapResult.Initialized -> with (result.state) {
            copy(
                followCamera = location == null,
                yandexMapState = yandexMapState.copy(
                    shouldMoveCamera = true,
                    cameraPosition = location,
                    zoom = 15f,
                    objects = yandexMapState.objects.map { it.copy(followCamera = location == null) }
                )
            )
        }
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
                            followCamera = followCamera,
                            isVisible = true
                        ),
                        MapObjectState(
                            id = "small",
                            type = Zone(
                                strokeColor = Color.Red,
                                fillColor = Color.Red.copy(alpha = 0.05F),
                                radius = minSafeZoneRadius.toFloat()
                            ),
                            location = result.location,
                            followCamera = followCamera,
                            isVisible = true
                        )
                    )
                )
            )
        }

        is MapResult.CameraPositionChanged -> with(state) {
            copy(
                yandexMapState = yandexMapState.copy(
                    cameraPosition = result.position,
                    objects = yandexMapState.objects.map { if (it.followCamera) it.copy(location = result.position) else it }
                )
            )
        }

        is MapResult.LocationUpdated -> with(state) {
            val updatedObjects = if (followCamera) {
                yandexMapState.objects.map { it.copy(location = result.location) }
            } else yandexMapState.objects

            copy(
                location = result.location,
                yandexMapState = yandexMapState.copy(
                    cameraPosition = result.location,
                    objects = updatedObjects
                )
            )
        }
    }
}