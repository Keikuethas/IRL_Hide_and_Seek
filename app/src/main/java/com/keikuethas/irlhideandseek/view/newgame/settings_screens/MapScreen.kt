package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keikuethas.irlhideandseek.view.map.YandexMapView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapSettingsScreen(localNavController: NavController) {

    val context = LocalContext.current

    val points = remember { mutableListOf<Point>() }

    // TODO установка круглой зоны произвольного радиуса в произвольном месте
    //  отображение хоста для удобства
MSUI()

}

@Composable
fun MSUI(
    preview: Boolean = false
) {
    val mapView = remember { mutableStateOf<MapView?>(null) }
    // vibecode
    val inputListener = remember {
        object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                Log.i("11map", "Long tap: ${point.latitude}, ${point.longitude}")

            }

            override fun onMapLongTap(map: Map, point: Point) {

            }
        }
    }

    // todo настройка радиуса зоны в bottomBar или topBar
    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Slider(
                    value = 1f,
                    onValueChange = {}
                )
            }
        }
    ) { innerPadding ->
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

@Preview
@Composable
fun MapSettingsScreenPreview() {
    MSUI(preview = true)
}