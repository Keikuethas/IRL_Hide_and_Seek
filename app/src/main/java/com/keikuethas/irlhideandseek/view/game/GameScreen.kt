package com.keikuethas.irlhideandseek.view.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.model.AbilityType
import com.keikuethas.irlhideandseek.model.PersonalBomb
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.model.Shield
import com.keikuethas.irlhideandseek.mvi.game.GameEffect
import com.keikuethas.irlhideandseek.mvi.game.GameIntent
import com.keikuethas.irlhideandseek.mvi.game.GameState
import com.keikuethas.irlhideandseek.mvi.game.GameViewModel
import com.keikuethas.irlhideandseek.mvi.game.PlayerState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.color
import com.keikuethas.irlhideandseek.utils.description
import com.keikuethas.irlhideandseek.utils.name
import com.keikuethas.irlhideandseek.utils.surfaceColor
import com.keikuethas.irlhideandseek.view.EndScreen
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.map.MapObjectState
import com.keikuethas.irlhideandseek.view.map.MapObjectType
import com.keikuethas.irlhideandseek.view.map.YandexMapView
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView

@Composable
private fun Header( //todo разные подписи к таймеру (+выделение цветом)
    state: GameState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.secondsRemain.let { secs ->
            Text(
                state.run {
                    when {
                        itsTimeToHide -> "Время спрятаться:"
                        else -> "До конца раунда: "
                    }
                } + "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"
            )
        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController = rememberNavController(),
    viewModel: GameViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    GSUI(
        state = state.value,
    ) { viewModel.onIntent(it) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is GameEffect.EndGame -> navController.navigate(
                    EndScreen(
                        effect.victory, effect.reason, effect.hunterName
                    )
                )

                GameEffect.GetDamage -> {}
                GameEffect.Quit -> navController.navigate(Home) {
                    popUpTo(Home)
                }
            }
        }
    }

}

val orange = Color(255, 152, 0, 255)
val yellow = Color(238, 205, 0, 255)
val grey = Color(77, 77, 77, 255)

fun zone(color: Color, location: Point) = MapObjectState(
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GSUI(
    preview: Boolean = false,
    state: GameState,
    onIntent: (GameIntent) -> Unit = {},
) {

    val mapView = remember { mutableStateOf<MapView?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Header(state) })
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!state.itsTimeToHide) {
                    if (state.usingAbilityOnMap == null)
                        FloatingActionButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = { onIntent(GameIntent.AbilityListOpen) },
                            containerColor = MaterialTheme.colorScheme.primary,

                            ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    modifier = Modifier.padding(horizontal = 5.dp),
                                    text = "Способности",
                                    textAlign = TextAlign.Center,
                                    style = typography.labelLarge,
                                    autoSize = TextAutoSize.StepBased(maxFontSize = 18.sp),
                                    maxLines = 1
                                )
                            }
                        }
                    else
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 96.dp)
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            FloatingActionButton(
                                modifier = Modifier.aspectRatio(1F),
                                onClick = { onIntent(GameIntent.CancelUseAbility) },
                                shape = RoundedCornerShape(24.dp),
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1F)
                                        .padding(5.dp),
                                    imageVector = Icons.Default.Close,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = null
                                )
                            }

                            FloatingActionButton(
                                modifier = Modifier.aspectRatio(1F),
                                onClick = { onIntent(GameIntent.UseAbility) },
                                shape = RoundedCornerShape(24.dp),
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1F)
                                        .padding(5.dp),
                                    imageVector = Icons.Default.Check,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    contentDescription = null
                                )
                            }
                        }
                }
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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (!state.itsTimeToHide || state.roleType == RoleType.HIDER) {
                    if (!preview)
                        YandexMapView(
                            modifier = Modifier.fillMaxSize(),
                            state = state.mapState,
                            onMapCreated = { map ->
                                mapView.value = map
                            },
                            onCameraMoveFinished = { onIntent(GameIntent.ReportCameraMoveFinished) },
                            onCameraPositionChanged = {
                                onIntent(
                                    GameIntent.ReportCameraPositionChanged(
                                        it
                                    )
                                )
                            }
                        )

                    if (state.usingAbilityOnMap != null)
                        Icon(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp),
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = state.usingAbilityOnMap.color
                        )
                } else
                    Text(
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 10.dp),
                        text = "Ждём, пока все спрячутся...",
                        style = typography.headlineMedium,
                        color = RoleType.SEEKER.color
                    )


                if (state.abilityListOpen || state.playerListOpen)
                    ModalBottomSheet(
                        onDismissRequest = { onIntent(GameIntent.AbilityListClose) },
                        sheetState = rememberModalBottomSheetState(
                            skipPartiallyExpanded = true
                        ),
                        dragHandle = {}
                    ) {
                        if (state.abilityListOpen)
                            AbilityList(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 30.dp),
                                showCatch = state.roleType == RoleType.SEEKER,
                                abilities = state.abilities,
                                onAbilitySelected = { onIntent(GameIntent.SelectAbility(it)) },
                                onCatchSelected = { onIntent(GameIntent.SelectCatch) }
                            )
                        else
                            HidersList(
                                hiders = state.players.filter { it.roleType == RoleType.HIDER && it.isAlive }
                            ) { onIntent(GameIntent.CatchPlayer(it)) }
                    }
            }
        }
    }
}

@Preview
@Composable
private fun HidersList(
    modifier: Modifier = Modifier.fillMaxWidth(),
    hiders: List<PlayerState> = listOf(
        PlayerState(
            id = "p",
            name = "poor fella",
            isAlive = true,
            location = Point(0.0, 0.0),
        )
    ),
    onSelected: (playerId: String) -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Поймать игрока",
            style = typography.headlineMedium,
            autoSize = TextAutoSize.StepBased(maxFontSize = 36.sp),
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = hiders
                ) { item ->
                    HiderItem(
                        hider = item
                    ) { onSelected(item.id) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HiderItem(
    hider: PlayerState = PlayerState(
        id = "p",
        name = "poor fella",
        isAlive = true,
        location = Point(0.0, 0.0),
    ),
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(2.dp, RoleType.HIDER.color),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.Person,
                tint = RoleType.HIDER.color,
                contentDescription = null
            )

            Text(
                text = hider.name,
                style = typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp),
                autoSize = TextAutoSize.StepBased(maxFontSize = 24.sp),
                maxLines = 1
            )
        }

    }
}


@Preview
@Composable
private fun AbilityList(
    modifier: Modifier = Modifier.fillMaxWidth(),
    showCatch: Boolean = true,
    abilities: List<AbilityState> = listOf(
        AbilityState(Shield()),
        AbilityState(PersonalBomb())
    ),
    onCatchSelected: () -> Unit = {},
    onAbilitySelected: (AbilityType) -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Применение способности",
            style = typography.headlineMedium,
            autoSize = TextAutoSize.StepBased(maxFontSize = 36.sp),
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            LazyColumn(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    CatchItem(
                        onClick = onCatchSelected
                    )
                }

                items(
                    items = abilities
                ) { item ->
                    AbilityItem(
                        ability = item,
                        onClick = { onAbilitySelected(item.type) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CatchItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(2.dp, RoleType.SEEKER.color),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = "Поймать",
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle()) {
                    append("Мгновенно выводит ")
                }
                withStyle(SpanStyle(color = RoleType.HIDER.color)) {
                    append("Прячущегося")
                }
                withStyle(SpanStyle()) {
                    append(" из игры.")
                }
            },
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
        )
    }
}

// TODO: индикация перезарядки
@Preview
@Composable
private fun AbilityItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    ability: AbilityState = AbilityState(Shield()),
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        onClick = { if (ability.cooldownProgress == 1F) onClick() },
        colors = CardDefaults.cardColors(
            containerColor =
                if (ability.cooldownProgress == 1F)
                    ability.type.surfaceColor
                else MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            ability.type.name(),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            ability.type.description(),
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
        )
    }
}


@Preview
@Composable
private fun GamePreview() {
    GSUI(
        preview = true,
        state = GameState(
            abilityListOpen = true
        )
    )
}