package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.utils.distanceTo
import com.keikuethas.irlhideandseek.utils.distanceToSegment
import com.keikuethas.irlhideandseek.utils.isBetween
import com.keikuethas.irlhideandseek.utils.projectOnLine
import com.keikuethas.irlhideandseek.view.YandexMapView
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun MapSettingsScreen(preview: Boolean = false) {

    val context = LocalContext.current
    val mapView = remember { mutableStateOf(MapView(context)) }
    val points = remember { mutableListOf<Point>() }

    // TODO установка круглой зоны произвольного радиуса в произвольном месте
    //  отображение хоста для удобства


    // vibecode
    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                Log.i("11map", "Long tap: ${point.latitude}, ${point.longitude}")
                //points.add(point)
                points.addPoint(point)
                drawPointsOnMap(mapView.value, points, context)
            }

            override fun onMapLongTap(map: Map, point: Point) {

            }
        }
    }

    Scaffold() { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            if (!preview) {
                YandexMapView(
                    modifier = Modifier.fillMaxSize(),
                    onMapCreated = { map ->
                        mapView.value = map

                        map.mapWindow.map.addInputListener(inputListener)
                    }
                )
            }
        }
    }
}

//NOTE: partly vibecode
private fun drawPointsOnMap(mapView: MapView, points: List<Point>, context: Context) {
    val map = mapView.mapWindow.map
    map.mapObjects.clear()
    val polygon = Polygon(LinearRing(points), listOf<LinearRing>())
    map.mapObjects.addPolygon(polygon)
    points.forEach { //refactor поменять иконку
        map.mapObjects.addPlacemark().apply {
            geometry = it
            setIcon(ImageProvider.fromResource(context, R.mipmap.test_placemark))
        }
    }
    Log.i("11map", "redraw")
}

private fun MutableList<Point>.addPoint(point: Point): Boolean {
    if (size <= 2) return add(point)

    // Расстояние от новой точки до отрезка, образованного i-ой и j-ой точками
    fun getDistance(i: Int, j: Int = i + 1): Double {

        val projection = point.projectOnLine(get(i), get(j))
        return if (projection.isBetween(get(i), get(j)))
            point.distanceTo(projection)
        else Double.POSITIVE_INFINITY
    }

    val minI = (0 until size - 1).minBy { point.distanceToSegment(get(it), get(it + 1)) }

    if (point.distanceToSegment(get(0), get(size - 1)) < point.distanceToSegment(
            get(minI),
            get(minI + 1)
        )
    )
        return add(point)

    add(minI + 1, point)
    return true
}

@Preview
@Composable
fun MapSettingsScreenPreview() {
    MapSettingsScreen(true)
}