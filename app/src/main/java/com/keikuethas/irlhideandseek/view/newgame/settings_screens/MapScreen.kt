package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import android.util.Log
import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapIntent
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapState
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapViewModel
import com.keikuethas.irlhideandseek.view.map.YandexMapView
import com.keikuethas.irlhideandseek.view.topbar.SettingsTopAppBar
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapSettingsScreen(
    localNavController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    // TODO установка круглой зоны произвольного радиуса в произвольном месте
    //  отображение хоста для удобства
    MSUI(
        state = state.value,
        onIntent = { viewModel.onIntent(it) }
    )

    // todo effect
}

@Composable
fun MSUI(
    preview: Boolean = false,
    state: MapState = MapState(),
    onIntent: (MapIntent) -> Unit = {}
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
        topBar = {
            SettingsTopAppBar(
                "Карта",
                onBackClick = { TODO() },
                onSaveClick = { TODO() },
                bottomContentSpacing = 5.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Перемещать зону",
                        style = typography.bodyLarge
                    )

                    Spacer(Modifier.width(24.dp))

                    Switch(
                        checked = true, //TODO
                        onCheckedChange = {} //TODO
                    )
                }
            }
        },
        bottomBar = { BottomBar() }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (!preview)
                YandexMapView(
                    modifier = Modifier.fillMaxSize(),
                    state = state.yandexMapState,
                    onMapCreated = { map ->
                        mapView.value = map
                        map.mapWindow.map.addInputListener(inputListener)
                    },
                    onCameraMoveFinished = { TODO() },
                    onCameraPositionChanged = { TODO() }
                )
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun BottomBar() {

    // temp
    val value = remember { mutableIntStateOf(100) }

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        RadiusSlider(
            label = "Радиус зоны",
            value = value.intValue,
            valueRange = 10..300,
            step = 10,
            onValueChange = {value.intValue = it}
        )
    }
}

@Composable
private fun RadiusSlider(
    label: String,
    value: Int,
    valueRange: ClosedRange<Int>,
    @IntRange(from = 1) step: Int,
    onValueChange: (Int) -> Unit
) {
    Column(

    ) {
        Text(
            text = label,
            style = typography.labelLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = with(valueRange) { start.toFloat()..endInclusive.toFloat() },
                steps = with(valueRange) { endInclusive - start } / step
            )

            Text(
                text = "$value ${stringResource(R.string.MetersUnit)}",
                style = typography.labelLarge,
                modifier = Modifier.width(200.dp),
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun MapSettingsScreenPreview() {
    MSUI(preview = true)
}