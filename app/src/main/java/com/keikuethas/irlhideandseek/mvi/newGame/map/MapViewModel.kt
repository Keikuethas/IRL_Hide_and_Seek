package com.keikuethas.irlhideandseek.mvi.newGame.map

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.keikuethas.irlhideandseek.LocationEvent
import com.keikuethas.irlhideandseek.LocationProvider
import com.keikuethas.irlhideandseek.data.NewGameRepository
import com.keikuethas.irlhideandseek.mvi.MVI_HiltViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.CameraPositionChanged
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.FollowStatusChanged
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.Initialized
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.LocationSet
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.LocationUpdated
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.QuitDialogStateSet
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.StopCameraMovement
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapResult.ZoneRangeChanged
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NewGameRepository
) : MVI_HiltViewModel<MapState, MapIntent, MapEffect, MapResult>(
    initialState = MapState(),
    savedStateHandle = savedStateHandle,
    savedStateKey = "MapState"
) {
    init {
        viewModelScope.launch {
            val initialMapState = repository.newGameState.value.mapSettings
            onIntent(MapIntent.Initialize(initialMapState))

            if (state.value.location == null) {
                LocationProvider.lastKnownLocation?.let { loc ->
                    Log.d("MapVM", "✅ Cache hit: ${loc.latitude}, ${loc.longitude}")
                    onIntent(MapIntent.SetLocation(Point(loc.latitude, loc.longitude)))
                    return@launch
                }

                try {
                    LocationProvider.observeLocation()
                        .filterIsInstance<LocationEvent.Update>()
                        .timeout(10.seconds)
                        .first()
                        .let { event ->
                            Log.d("MapVM", "✅ Flow emitted: ${event.location.latitude}, ${event.location.longitude}")
                            onIntent(MapIntent.SetLocation(Point(event.location.latitude, event.location.longitude)))
                        }
                } catch (e: TimeoutCancellationException) {
                    Log.w("MapVM", "⏳ Timeout: Location not received in 10s.")
                } catch (e: NoSuchElementException) {
                    Log.w("MapVM", "❌ Flow closed without emitting.")
                }
            }
        }
    }

    override fun onIntent(intent: MapIntent) = with(intent) {
        when (this) {
            is MapIntent.ChangeZoneRange -> dispatch(ZoneRangeChanged(min, max))
            MapIntent.ChangeFollowStatus -> dispatch(FollowStatusChanged)
            MapIntent.ReportCameraMoveFinished -> dispatch(StopCameraMovement)
            is MapIntent.ReportCameraPositionChanged -> {
                dispatch(CameraPositionChanged(pos))
                if (state.value.followCamera) dispatch(LocationUpdated(pos))
            }
            MapIntent.RequestQuit -> dispatch(QuitDialogStateSet(true))
            MapIntent.Save -> {
                repository.updateMapSettings(state.value)
                sendEffect(MapEffect.Quit)
            }
            MapIntent.ConfirmQuit -> {
                dispatch(QuitDialogStateSet(false))
                sendEffect(MapEffect.Quit)
            }
            MapIntent.DeclineQuit -> dispatch(QuitDialogStateSet(false))
            is MapIntent.Initialize -> dispatch(Initialized(state))
            is MapIntent.SetLocation -> dispatch(LocationSet(location))
        }
    }

    override fun reduce(state: MapState, result: MapResult) = MapReducer.reduce(state, result)
}