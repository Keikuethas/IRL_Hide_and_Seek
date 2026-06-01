package com.keikuethas.irlhideandseek.view.map

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.yandex.mapkit.geometry.Point
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.UUID

@Parcelize
sealed interface MapObjectType : Parcelable {
    data class Marker(
        val strokeColor: @RawValue Color,
        val fillColor: @RawValue Color = strokeColor,
        val label: String? = null
    ) : MapObjectType
    data class Zone(
        val strokeColor: @RawValue Color,
        val fillColor: @RawValue Color,
        val radius: Float,
    ) : MapObjectType
}

@Parcelize
data class MapObjectState(
    val id: String = UUID.randomUUID().toString(),
    val type: MapObjectType,
    val location: @RawValue Point,
    val isVisible: Boolean = true,
    val followCamera: Boolean = false
): Parcelable

@Parcelize
data class YandexMapState(
    val objects: List<MapObjectState> = emptyList(),
    val cameraPosition: @RawValue Point? = null,
    val zoom: Float = 15.0f,
    val shouldMoveCamera: Boolean = false,
): Parcelable