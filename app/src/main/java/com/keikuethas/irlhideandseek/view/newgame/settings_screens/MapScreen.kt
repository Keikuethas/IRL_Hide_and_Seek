package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapEffect
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapIntent
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapState
import com.keikuethas.irlhideandseek.mvi.newGame.map.MapViewModel
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.RangeSliderWithTooltips
import com.keikuethas.irlhideandseek.view.map.YandexMapView
import com.keikuethas.irlhideandseek.view.topbar.SettingsTopAppBar
import com.yandex.mapkit.mapview.MapView

//FIXME: камера устанавливается не туда

@Composable
fun MapSettingsScreen(
    localNavController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    MSUI(
        state = state.value,
        unitName = stringResource(R.string.MetersUnit)
    ) { viewModel.onIntent(it) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                MapEffect.Quit -> localNavController.popBackStack()
            }
        }
    }
}

@Composable
fun MSUI(
    preview: Boolean = false,
    state: MapState = MapState(),
    unitName: String = "м",
    onIntent: (MapIntent) -> Unit = {}
) {
    val mapView = remember { mutableStateOf<MapView?>(null) }

    if (state.showQuitDialog)
        AskingDialog(
            title = "Вы уверены?",
            description = "Сделанные вами изменения не сохранятся.",
            confirmButtonText = "Выйти",
            onDismiss = { onIntent(MapIntent.DeclineQuit) },
            onConfirm = { onIntent(MapIntent.ConfirmQuit) },
            dismissButtonText = "Отменить"
        )

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                "Карта",
                onBackClick = { onIntent(MapIntent.RequestQuit) },
                onSaveClick = { onIntent(MapIntent.Save) },
                bottomContentSpacing = 5.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Перемещать зону", style = typography.bodyLarge)
                    Spacer(Modifier.width(24.dp))
                    Switch(
                        checked = state.followCamera,
                        onCheckedChange = { onIntent(MapIntent.ChangeFollowStatus) }
                    )
                }
            }
        }

    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                if (!preview) {
                    YandexMapView(
                        modifier = Modifier.fillMaxSize(),
                        state = state.yandexMapState,
                        onMapCreated = { map ->
                            mapView.value = map
                        },
                        onCameraMoveFinished = { onIntent(MapIntent.ReportCameraMoveFinished) },
                        onCameraPositionChanged = {
                            onIntent(
                                MapIntent.ReportCameraPositionChanged(
                                    it
                                )
                            )
                        }
                    )
                }

                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }


            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                with(state) {
                    BottomBar(
                        minRadius = minSafeZoneRadius,
                        maxRadius = safeZoneRadius,
                        unitName = unitName,
                        onValueChange = { min, max ->
                            onIntent(
                                MapIntent.ChangeZoneRange(
                                    min,
                                    max
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    minRadius: Int,
    maxRadius: Int,
    unitName: String,
    onValueChange: (min: Int, max: Int) -> Unit
) {
    Box(Modifier.padding(horizontal = 48.dp)) {
        RangeSliderWithTooltips(
            value = minRadius.toFloat()..maxRadius.toFloat(),
            onValueChange = { range ->
                onValueChange(range.start.toInt(), range.endInclusive.toInt())
            },
            valueRange = 10F..500F,
            startLabel = "Минимальный радиус",
            endLabel = "Начальный радиус",
            valueFormatter = { "${it.toInt()} $unitName" }
        ) {
            Text("Настройка безопасной зоны", style = typography.labelLarge)
        }
    }
}

@Preview
@Composable
fun MapSettingsScreenPreview() {
    MSUI(preview = true)
}