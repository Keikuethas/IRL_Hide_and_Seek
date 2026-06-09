package com.keikuethas.irlhideandseek.view.lobby

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyEffect
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyIntent
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyState
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.network.models.AbilityInfo
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.ui.theme.surfaceColor
import com.keikuethas.irlhideandseek.view.Game
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.components.AskingDialog
import com.keikuethas.irlhideandseek.view.components.ErrorDialog
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo

// Функция для преобразования AbilityInfo в AbilityState
// Refactor no AbilityInfo here
private fun abilityInfoToAbilityState(abilityInfo: AbilityInfo): AbilityState {
    // Собираем параметры: стандартные + дополнительные из data
    val params = mutableListOf<Pair<String, Number>>().apply {
        add("duration_seconds" to (abilityInfo.duration_seconds ?: 0))
        add("number_uses" to abilityInfo.number_uses)
        add("recharge_time" to abilityInfo.recharge_time)
        abilityInfo.data.forEach { (key, value) -> add(key to value) }
    }

    return AbilityState(abilityInfo.ability_type, params)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    navController: NavController = rememberNavController(),
    viewModel: LobbyViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    if (!state.value.error.isNullOrBlank()) {
        ErrorDialog(
            title = "Ошибка",
            description = state.value.error ?: "",
            onDismiss = { viewModel.onIntent(LobbyIntent.DismissError) }
        )
    }

    if (state.value.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LobbyContent(
            state = state.value,
            onIntent = { viewModel.onIntent(it) },
            onBackPressed = { viewModel.onIntent(LobbyIntent.QuitRequest) }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LobbyEffect.Quit -> navController.navigate(Home)

                is LobbyEffect.StartGame ->
                    navController.navigate(Game(effect.timeToHide))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyContent(
    state: LobbyState,
    onIntent: (LobbyIntent) -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(onBack = onBackPressed)

    Scaffold(
        topBar = { TextTopAppBar(state.roomName, "Код: ${state.roomCode}") }
    ) { innerPadding ->
        if (state.showQuitDialog) {
            AskingDialog(
                title = "Покинуть лобби",
                description = "Вы действительно хотите покинуть лобби?",
                confirmButtonText = "Выйти",
                dismissButtonText = "Отмена",
                onDismiss = { onIntent(LobbyIntent.QuitDialogRespond(false)) },
                onConfirm = { onIntent(LobbyIntent.QuitDialogRespond(true)) }
            )
        }

        // Преобразуем RoleFull в RoleState для диалога (включая способности)
        val playerRoles = state.roles.map { roleFull ->
            RoleState(
                roleName = roleFull.name,

                type = roleFull.victory_condition,
                abilities = roleFull.abilities.map { abilityInfoToAbilityState(it) },
                health = roleFull.health
            )
        }

        if (state.showRoleChangeDialog) {
            RoleChangeDialog(
                roles = playerRoles,
                playerRole = state.playerRole,
                onDismiss = { onIntent(LobbyIntent.DeclineRoleChange) },
                onRoleSelect = { roleName ->
                    val selectedRole = state.roles.find { it.name == roleName }
                    Log.d("LobbyVM", "Role select: $selectedRole")
                    if (selectedRole != null) {
                        // Отправляем ID роли
                        Log.d("LobbyVM", "Role_id: ${selectedRole.id}")
                        onIntent(LobbyIntent.ChangeRole(selectedRole.id))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlayerCard(
                modifier = Modifier
                    .fillMaxHeight(0.15f)
                    .fillMaxWidth(0.9F)
                    .padding(top = 12.dp),
                name = state.playerName,
                role = state.playerRole,
                roleType = state.roles.find { it.name == state.playerRole }?.victory_condition ?: RoleType.SEEKER,
                ready = state.isReady,
                onReadyClick = { onIntent(LobbyIntent.ChangeReadyStatus) },
                onRoleClick = { onIntent(LobbyIntent.RequestRoleChangeDialog) }
            )
            playerRoles

            DisplayPlayers(
                playerList = state.players,
                roles = state.roles
            )

            ElevatedButton(
                onClick = { onIntent(LobbyIntent.QuitRequest) },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Покинуть игру")
            }
        }
    }
}

// Upgrade: icon should change according to roleType
@Composable
private fun PlayerCard(
    name: String = "Player",
    role: String = "Pirate",
    roleType: RoleType = RoleType.HIDER,
    modifier: Modifier = Modifier
        .fillMaxWidth(0.9F)
        .fillMaxHeight(0.3F),
    ready: Boolean = !true,
    cornerRadius: Dp = 24.dp,
    onReadyClick: () -> Unit,
    onRoleClick: () -> Unit
) {

    val activeColor = if (ready) roleType.color else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .padding(2.dp),
        //horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.2F),
            onClick = onReadyClick,
            color = if (ready) roleType.surfaceColor else MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(
                topStart = cornerRadius,
                bottomStart = cornerRadius,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    Icons.Default.Done,
                    contentDescription = null,
                    tint = activeColor,
                    modifier = Modifier
                        .weight(0.8F)
                        .aspectRatio(1f)
                )

                Text(
                    text = if (ready) "Готов" else "Не готов",
                    autoSize = TextAutoSize.StepBased(),
                    modifier = Modifier.weight(0.2F),
                    fontWeight = FontWeight.SemiBold,
                    color = if (ready) roleType.color else Color.Unspecified

                )
            }
        }

        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRoleClick,
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = cornerRadius,
                bottomEnd = cornerRadius
            ),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = name,
                    style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    autoSize = TextAutoSize.StepBased(maxFontSize = 36.sp),
                    maxLines = 1
                )

                Text(
                    text = "Роль: $role",
                    style = typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private class RoleTypeProvider : PreviewParameterProvider<RoleType> {
    override val values =
        RoleType.entries.asSequence()
}

@Preview()
@Composable
fun LobbyContentPreview(
    @PreviewParameter(
        RoleTypeProvider::class,
        limit = 2
    ) roleType: RoleType
) {
    val fakeRole = RoleInfo(
        id = "role1",
        name = "Роль1",
        health = 100,
        victory_condition = roleType,
        abilities = emptyList()
    )
    val fakeRole2 = RoleInfo(
        id = "role2",
        name = "Роль2",
        health = 100,
        victory_condition = RoleType.SEEKER,
        abilities = emptyList()
    )
    val previewState = LobbyState(
        playerName = "Реально длинное имя",
        playerRole = "role1",
        players = listOf(
            PlayerInfo(
                id = "_",
                name = "Игрок 1",
                health = 100,
                is_alive = true,
                location_lat = 23.0,
                location_lng = 42.0,
                role_id = "role1",
                is_player_ready = false
            ),
            PlayerInfo(
                id = "__",
                name = "Игрок 2",
                health = 100,
                is_alive = true,
                location_lat = 23.0,
                location_lng = 42.0,
                role_id = "role2",
                is_player_ready = true
            )
        ),
        roles = listOf(fakeRole, fakeRole2),
        showQuitDialog = false,
        showRoleChangeDialog = false,
        roomName = "Cool room name",
        roomCode = "732720"
    )
    LobbyContent(
        state = previewState,
        onIntent = {},
        onBackPressed = {}
    )
}