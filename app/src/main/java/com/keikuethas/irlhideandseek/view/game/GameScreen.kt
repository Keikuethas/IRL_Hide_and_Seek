package com.keikuethas.irlhideandseek.view.game

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.GeneralScreen
import com.keikuethas.irlhideandseek.R
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    innerPadding: PaddingValues,
    navController: NavController = rememberNavController(),
    preview: Boolean = false
) {
    val context = LocalContext.current
    val mapView = remember { mutableStateOf(MapView(context)) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        //initialValue = SheetValue.PARTIALLY_EXPANDED, // Частично открыта по умолчанию
        skipPartiallyExpanded = false, // Разрешить три состояния
    )

    Box(
        Modifier
            .padding(innerPadding)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Green)
        ) {
            if (!preview) YandexMapView(
                modifier = Modifier.fillMaxSize(),
                onMapCreated = { map ->
                    mapView.value = map
                }
            )
        }


        //NOTE: вайбкод
        if (showBottomSheet.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    // При закрытии свайпом вниз
                    showBottomSheet.value = false
                },
                sheetState = sheetState,
                dragHandle = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally){
                        Spacer(Modifier.height(50.dp))
                        // "Ручка" для перетаскивания
                        BottomSheetDefaults.DragHandle(
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("Умения", Modifier.padding(10.dp))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,

                ) {
                //TODO: Контент панели (умения)

                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    repeat(300)
                    {
                        AbilityItem(Ability())
                    }
                }

                // Отступ снизу для безопасности (iPhone X+ и аналоги)
                Spacer(
                    modifier = Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        } else
            Box(
                Modifier
                    .fillMaxSize(),
                Alignment.BottomCenter
            ) {
                Button(
                    onClick = { showBottomSheet.value = true },
                    Modifier.wrapContentSize(),
                    colors = ButtonColors(
                        containerColor = Color(150, 100, 200), //REFACTOR: убрать цвет в тему
                        contentColor = Color.Unspecified,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
                    Row {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Умения")
                    }
                }
            }
    }

    //enableUserLocation(mapView.value, context)
//
//    //TEMP
//    val map = mapView.value.mapWindow.map
//    map.move(CameraPosition(
//        /* target = */ Point(55.751225, 37.62954),
//        /* zoom = */ 30.0F,
//        /* azimuth = */ 1F,
//        /* tilt = */ 0f
//    ))
}

//Note: вайбкод
private fun enableUserLocation(mapView: MapView, context: Context) {
    val mapKit: MapKit = MapKitFactory.getInstance()

    // ✅ Используем mapWindow вместо map
    val userLocationLayer: UserLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)

    userLocationLayer.isVisible = true
    userLocationLayer.isHeadingEnabled = true

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        userLocationLayer.also {
            it.isVisible = true
            it.isHeadingEnabled = true
        }
    }
}


@Preview
@Composable
fun GamePreview() {
    GeneralScreen { GameScreen(it, preview = true) }
}