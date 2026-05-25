package com.keikuethas.irlhideandseek.view.map

import androidx.compose.ui.graphics.Color
import com.yandex.mapkit.geometry.Point
import java.util.UUID

sealed interface MapObjectType {
    data class Marker(
        val strokeColor: Color,
        val fillColor: Color = strokeColor,
        val label: String? = null
    ) : MapObjectType
    data class Zone(
        val strokeColor: Color,
        val fillColor: Color,
        val radius: Float,
    ) : MapObjectType
}


data class MapObjectState(
    val id: String = UUID.randomUUID().toString(),          // ✅ Уникальный ID для отслеживания
    val type: MapObjectType,
    val location: Point,
    val isVisible: Boolean = true
)

data class MapState(
    val objects: List<MapObjectState> = emptyList(),
    val cameraPosition: Point? = null,
    val zoom: Float = 15.0f,
    val shouldMoveCamera: Boolean = false,
)