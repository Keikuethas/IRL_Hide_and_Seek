package com.keikuethas.irlhideandseek.view.game

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.mvi.game.GameEffect
import com.keikuethas.irlhideandseek.mvi.game.GameIntent
import com.keikuethas.irlhideandseek.mvi.game.GameState
import com.keikuethas.irlhideandseek.mvi.game.GameViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.adjustLightness
import com.keikuethas.irlhideandseek.view.map.MapObjectState
import com.keikuethas.irlhideandseek.view.map.MapObjectType
import com.keikuethas.irlhideandseek.view.map.YandexMapState
import com.keikuethas.irlhideandseek.view.map.YandexMapView
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import kotlinx.coroutines.flow.SharedFlow

// TODO
//  ф-я для меток игроков (картинки для игроков надо тоже)

@Composable
private fun Header( //todo разные подписи к таймеру (+выделение цветом)
    state: GameState
) {
    state.secondsRemain.let { secs ->
        Text(
            state.run {
                when {
                    itsTimeToHide && roleType == RoleType.Hider -> "Время спрятаться:"
                    else -> ""
                }
            } + "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController = rememberNavController(),
    gameViewModel: GameViewModel = hiltViewModel()
) {

    val state = gameViewModel.state.collectAsStateWithLifecycle()

    GSUI(
        //state = state.value,
        effectStream = gameViewModel.effect,
        onNavigation = { navController.navigate(it) },
    ) { gameViewModel.onIntent(it) }

}

val orange = Color(255, 152, 0, 255)
val yellow = Color(238, 205, 0, 255)
val grey = Color(77, 77, 77, 255)

fun zone(color: Color, location:Point) = MapObjectState(
    type = MapObjectType.Zone(
        strokeColor = color,
        fillColor = color.copy(alpha = 0.1F),
        radius = 50F
    ),
    location = location
)

fun ozone(lat: Double, lng: Double) = zone(orange, Point(lat, lng))
fun yzone(lat: Double, lng: Double) = zone(yellow, Point(lat, lng))
fun gzone(lat: Double, lng: Double) = zone(grey, Point(lat, lng))


//todo effect
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GSUI(
    preview: Boolean = false,

    // temp for test
    state: GameState = GameState(
        mapState = YandexMapState(
            objects = listOf(
                MapObjectState(
                    type = MapObjectType.Zone( //sz
                        strokeColor = Color.Blue.adjustLightness(0.1F),
                        fillColor = Color.Blue.copy(alpha = 0.05F),
                        radius = 300F
                    ),
                    location = Point(55.660346, 37.474629),
                ),
                MapObjectState(
                    //char
                    type = MapObjectType.Marker(
                        strokeColor = Color.Green,
                        fillColor = RoleType.Hider.color
                    ),
                    location = Point(55.660311, 37.472870),
                ),
                gzone(55.660644, 37.474967)
            ),
            cameraPosition = Point(55.660346, 37.474629),
            zoom = 15.25f,
            shouldMoveCamera = true
        ),
        secondsRemain = 520
    ),
    // temp ------
    effectStream: SharedFlow<GameEffect>? = null,
    onNavigation: (Any) -> Unit = {},
    onIntent: (GameIntent) -> Unit = {},
) {

    val mapView = remember { mutableStateOf<MapView?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Header(state) })
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                @Composable
                fun FAB(
                    modifier: Modifier = Modifier,
                    containerColor: Color = FloatingActionButtonDefaults.containerColor,
                    tint: Color = Color.Unspecified,
                    imageVector: ImageVector,
                    text: String,
                    textAlign: TextAlign? = null,
                    style: TextStyle = LocalTextStyle.current,
                    onClick: () -> Unit
                ) {
                    FloatingActionButton(
                        onClick = onClick,
                        containerColor = containerColor,
                        modifier = modifier
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector, null, tint = tint)
                            Text(
                                text,
                                textAlign = textAlign,
                                style = style,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }

                FAB(
                    containerColor = Color.Red.adjustLightness(-0.1F),
                    imageVector = Icons.Default.BackHand,
                    text = "Поймать",
                    style = typography.labelSmall
                ) { }

                Spacer(Modifier.height(20.dp))

                FAB(
                    imageVector = Icons.Default.AutoAwesome,
                    text = "Способности",
                    style = typography.labelSmall
                ) { }
            }
        }
    ) { innerPadding ->
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
                    state = state.mapState,
                    onMapCreated = { map ->
                        mapView.value = map
                    },
                    onCameraMoveFinished = { onIntent(GameIntent.ReportCameraMoved) }
                )
            }


        }
    }
}

@Preview
@Composable
private fun AbilityList(
    modifier: Modifier = Modifier,
    abilities: List<AbilityState> = listOf(
        AbilityState(Shield())
    )
) {
    Column(
        modifier = modifier
    ) {

    }
}

@Preview
@Composable
private fun AbilityItem(
    modifier: Modifier = Modifier,
    ability: AbilityState = AbilityState(Shield())
) {

}

// refactor
private fun addCircle(
    mapView: MapView,
    latitude: Double,
    longitude: Double,
    radius: Float,
    context: Context,
    _strokeColor: Int,
    _fillColor: Int
) {
    val circle = Circle(
        Point(latitude, longitude),
        radius
    )
    mapView.mapWindow.map.mapObjects.addCircle(circle).apply {
        strokeWidth = 2f
        strokeColor = ContextCompat.getColor(context, _strokeColor)
        fillColor = ContextCompat.getColor(context, _fillColor)
    }
}

// refactor use placemark in VM instead
private fun enableUserLocation(mapView: MapView, context: Context) {
    val mapKit: MapKit = MapKitFactory.getInstance()
    val userLocationLayer: UserLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        userLocationLayer.apply {
            isVisible = true
            isHeadingEnabled = true
        }
    }
}

@Preview
@Composable
private fun GamePreview() {
    GSUI(preview = true)
}