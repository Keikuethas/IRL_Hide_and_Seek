package com.keikuethas.irlhideandseek.view.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

//vibecode
@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    state: MapState = MapState(),
    onMapCreated: (MapView) -> Unit = {},
    onCameraMoveFinished: () -> Unit = {},
    onCameraPositionChanged: (Point) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val mapObjectsMap = remember { mutableMapOf<String, MapObject>() }
    val cameraListener = remember { mutableStateOf<CameraListener?>(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                MapKitFactory.initialize(ctx)
                mapViewState.value = this
                onMapCreated(this)
            }
        },
        modifier = modifier
    )

    DisposableEffect(lifecycleOwner) {
        val mapView = mapViewState.value
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    mapView?.onStart()
                    MapKitFactory.getInstance().onStart()
                }

                Lifecycle.Event.ON_STOP -> {
                    mapView?.onStop()
                    MapKitFactory.getInstance().onStop()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView?.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    // Инициализация слушателя камеры
    mapViewState.value?.let { mapView ->
        val map = mapView.mapWindow.map

        // Создаём и подключаем слушатель камеры один раз
        LaunchedEffect(map) {
            val listener =
                CameraListener { map, cameraPosition, reason, finished ->
                    onCameraPositionChanged(cameraPosition.target)

                    // Сбрасываем флаг, если перемещение завершено
                    if (finished && state.shouldMoveCamera) {
                        onCameraMoveFinished()
                    }
                }

            map.addCameraListener(listener)
            cameraListener.value = listener
        }
    }

    // Синхронизация состояния с картой
    mapViewState.value?.let { mapView ->
        val map = mapView.mapWindow.map

        LaunchedEffect(state.objects) {
            syncMapObjects(map, state.objects, mapObjectsMap)
        }

        LaunchedEffect(state.cameraPosition) {
            if (state.shouldMoveCamera) {
                state.cameraPosition?.let { point ->
                    map.move(
                        /* cameraPosition */ CameraPosition(point, state.zoom, 0.0f, 0.0f),
                        /* animation */ Animation(
                            Animation.Type.SMOOTH,
                            1.0f
                        ),
                        /* callback */ null
                    )
                }
            }
        }
    }
        DisposableEffect(Unit) {
        onDispose {
            cameraListener.value?.let { mapViewState.value?.mapWindow?.map?.removeCameraListener(it) }
        }
    }

}

private fun syncMapObjects(
    map: Map,
    newState: List<MapObjectState>,
    existingObjectsMap: MutableMap<String, MapObject>
) {
    val collection = map.mapObjects.addCollection()
    val newIds = newState.map { it.id }.toSet()

    // Удаление объектов
    val idsToRemove = existingObjectsMap.keys - newIds
    idsToRemove.forEach { id ->
        existingObjectsMap[id]?.let { obj ->
            collection.remove(obj)
            existingObjectsMap.remove(id)
        }
    }

    // Обновление объектов
    newState.forEach { stateObj ->
        val existingObj = existingObjectsMap[stateObj.id]

        if (existingObj == null) {
            // Создаем новый объект
            val newObject = createMapObject(collection, stateObj)
            if (newObject != null) {
                existingObjectsMap[stateObj.id] = newObject
            }
        } else {
            // Обновляем существующий (например, видимость или позицию)
            updateMapObject(existingObj, stateObj)
        }
    }
}

private fun createMapObject(
    collection: MapObjectCollection,
    state: MapObjectState
): MapObject? {
    if (!state.isVisible) return null

    return when (state.type) {
        is MapObjectType.Marker -> {
            collection.addPlacemark().apply {
                setIcon(
                    ImageProvider.fromBitmap(
                        createMarkerBitmap(
                            strokeColor = state.type.strokeColor,
                            fillColor = state.type.fillColor
                        )
                    )
                )
                geometry = state.location
            }
        }

        is MapObjectType.Zone -> {

            collection.addCircle(
                Circle(state.location, state.type.radius)
            ).apply {
                strokeWidth = 2f
                strokeColor = state.type.strokeColor.toArgb()
                fillColor = state.type.fillColor.toArgb()
            }
        }
    }
}

private fun updateMapObject(existingObj: MapObject, state: MapObjectState) {
    existingObj.isVisible = state.isVisible

    // Обновляем позицию для объектов с followCamera
    when (existingObj) {
        is com.yandex.mapkit.map.PlacemarkMapObject -> {
            existingObj.geometry = state.location
        }
        is com.yandex.mapkit.map.CircleMapObject -> {
            existingObj.geometry = Circle(state.location, existingObj.geometry.radius)
        }
    }
}

/**
 * Создаёт Bitmap-иконку для маркера игрока.
 * @param fillColor Цвет внутренней области (отображает роль)
 * @param strokeColor Цвет внешнего кольца (отображает статус "Текущий игрок")
 * @param size Размер итогового Bitmap (рекомендуется 64 или 128 для Retina)
 */
fun createMarkerBitmap(
    fillColor: androidx.compose.ui.graphics.Color,
    strokeColor: androidx.compose.ui.graphics.Color,
    size: Int = 64
): Bitmap {
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val center = size / 2f

    // Настройки геометрии для максимальной видимости
    val strokeThickness = 8f
    val outerRadius = center - strokeThickness / 2f
    val innerRadius = center - strokeThickness - 2f // 2px технический зазор

    val paint = Paint().apply { isAntiAlias = true }

    // 1. Внешнее кольцо
    paint.color = strokeColor.toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = strokeThickness
    canvas.drawCircle(center, center, outerRadius, paint)

    // Внутренний круг
    paint.color = fillColor.toArgb()
    paint.style = Paint.Style.FILL
    canvas.drawCircle(center, center, innerRadius, paint)

    paint.color = Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawCircle(center, center, innerRadius + 1f, paint)

    return bitmap
}