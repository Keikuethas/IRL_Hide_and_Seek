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
import androidx.compose.runtime.rememberUpdatedState
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
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun YandexMapView(
    modifier: Modifier = Modifier,
    state: YandexMapState = YandexMapState(),
    onMapCreated: (MapView) -> Unit = {},
    onCameraMoveFinished: () -> Unit = {},
    onCameraPositionChanged: (Point) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapViewState = remember { mutableStateOf<MapView?>(null) }
    val mapObjectsMap = remember { mutableMapOf<String, MapObject>() }
    val mapObjectsCollection = remember { mutableStateOf<MapObjectCollection?>(null) }
    val cameraListenerRef = remember { mutableStateOf<CameraListener?>(null) }

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

    mapViewState.value?.let { mapView ->
        val map = mapView.mapWindow.map

        if (mapObjectsCollection.value == null) {
            mapObjectsCollection.value = map.mapObjects.addCollection()
        }

        val shouldMoveCameraState = rememberUpdatedState(state.shouldMoveCamera)
        LaunchedEffect(map) {
            val listener = CameraListener { _, cameraPosition, _, finished ->
                onCameraPositionChanged(cameraPosition.target)
                if (finished && shouldMoveCameraState.value) {
                    onCameraMoveFinished()
                }
            }
            map.addCameraListener(listener)
            cameraListenerRef.value = listener
        }

        // Синхронизация объектов
        LaunchedEffect(state.objects) {
            val collection = mapObjectsCollection.value ?: return@LaunchedEffect
            syncMapObjects(collection, state.objects, mapObjectsMap)
        }

        // Движение камеры
        LaunchedEffect(state.cameraPosition, state.shouldMoveCamera, state.zoom) {
            if (state.shouldMoveCamera) {
                state.cameraPosition?.let { point ->
                    map.move(
                        CameraPosition(point, state.zoom, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1.0f),
                        null
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraListenerRef.value?.let {
                mapViewState.value?.mapWindow?.map?.removeCameraListener(it)
            }
        }
    }
}

private fun syncMapObjects(
    collection: MapObjectCollection,
    newState: List<MapObjectState>,
    existingObjectsMap: MutableMap<String, MapObject>
) {
    val newIds = newState.mapNotNull { it.id.takeIf { id -> id.isNotBlank() } }.toSet()
    val idsToRemove = existingObjectsMap.keys - newIds

    idsToRemove.forEach { id ->
        existingObjectsMap[id]?.let { obj ->
            collection.remove(obj)
            existingObjectsMap.remove(id)
        }
    }

    newState.forEach { stateObj ->
        val id = stateObj.id.takeIf { it.isNotBlank() } ?: return@forEach
        val existingObj = existingObjectsMap[id]

        if (existingObj == null) {
            createMapObject(collection, stateObj)?.let { newObject ->
                existingObjectsMap[id] = newObject
            }
        } else {
            updateMapObject(existingObj, stateObj)
        }
    }
}

private fun createMapObject(collection: MapObjectCollection, state: MapObjectState): MapObject? {
    if (!state.isVisible) return null
    return when (state.type) {
        is MapObjectType.Marker -> {
            collection.addPlacemark().apply {
                setIcon(ImageProvider.fromBitmap(createMarkerBitmap(state.type.strokeColor, state.type.fillColor)))
                geometry = state.location
                isVisible = state.isVisible
            }
        }
        is MapObjectType.Zone -> {
            collection.addCircle(Circle(state.location, state.type.radius)).apply {
                strokeWidth = 2f
                strokeColor = state.type.strokeColor.toArgb()
                fillColor = state.type.fillColor.toArgb()
                isVisible = state.isVisible // Явно задаём видимость при создании
            }
        }
    }
}

private fun updateMapObject(existingObj: MapObject, state: MapObjectState) {
    existingObj.isVisible = state.isVisible
    when (existingObj) {
        is com.yandex.mapkit.map.PlacemarkMapObject -> existingObj.geometry = state.location
        is com.yandex.mapkit.map.CircleMapObject -> {
            val zoneType = state.type as? MapObjectType.Zone
            val newRadius = zoneType?.radius ?: existingObj.geometry.radius
            if (zoneType != null && newRadius > 0f) {
                existingObj.geometry = Circle(state.location, newRadius)
            }
        }
    }
}

fun createMarkerBitmap(
    fillColor: androidx.compose.ui.graphics.Color,
    strokeColor: androidx.compose.ui.graphics.Color,
    size: Int = 64
): Bitmap {
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val center = size / 2f
    val strokeThickness = 8f
    val outerRadius = center - strokeThickness / 2f
    val innerRadius = center - strokeThickness - 2f
    val paint = Paint().apply { isAntiAlias = true }

    paint.color = strokeColor.toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = strokeThickness
    canvas.drawCircle(center, center, outerRadius, paint)

    paint.color = fillColor.toArgb()
    paint.style = Paint.Style.FILL
    canvas.drawCircle(center, center, innerRadius, paint)

    paint.color = Color.WHITE
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 2f
    canvas.drawCircle(center, center, innerRadius + 1f, paint)
    return bitmap
}