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
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

//vibecode
@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    state: MapState = MapState(), // ✅ Входные данные из ViewModel
    onMapCreated: (MapView) -> Unit = {},
    onCameraMoveFinished: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    // ✅ Хранилище объектов карты: ID -> Объект Yandex Map
    // Позволяет управлять объектами без пересоздания коллекции
    val mapObjectsMap = remember { mutableMapOf<String, MapObject>() }

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

    // ✅ Синхронизация состояния с картой
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
                onCameraMoveFinished()
            }
        }
    }
}

// ✅ Логика синхронизации (Diffing)
private fun syncMapObjects(
    map: com.yandex.mapkit.map.Map,
    newState: List<MapObjectState>,
    existingObjectsMap: MutableMap<String, MapObject>
) {
    val collection = map.mapObjects.addCollection()
    val newIds = newState.map { it.id }.toSet()

    // 1. ❌ УДАЛЕНИЕ: Объекты, которые есть в карте, но нет в новом состоянии
    val idsToRemove = existingObjectsMap.keys - newIds
    idsToRemove.forEach { id ->
        existingObjectsMap[id]?.let { obj ->
            collection.remove(obj)
            existingObjectsMap.remove(id)
        }
    }

    // 2. ✅ ДОБАВЛЕНИЕ / ОБНОВЛЕНИЕ: Новые или измененные объекты
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
                setIcon(ImageProvider.fromBitmap(createMarkerBitmap(state.type.color)))
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
    // Можно добавить логику обновления свойств без пересоздания
    existingObj.isVisible = state.isVisible
}

private fun createMarkerBitmap(color: androidx.compose.ui.graphics.Color): Bitmap {
    val size = 64
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        this.color = color.toArgb()
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    paint.color = Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 4f
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, paint)
    return bitmap
}