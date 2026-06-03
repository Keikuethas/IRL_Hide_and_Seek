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
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.Websocket_V2.RoleFull
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyEffect
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyIntent
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyState
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.network.models.AbilityInfo
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.adjustLightness
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.components.AskingDialog
import com.keikuethas.irlhideandseek.view.components.ErrorDialog
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar

// Функция для преобразования AbilityInfo в AbilityState
private fun abilityInfoToAbilityState(abilityInfo: AbilityInfo): AbilityState {
    // Собираем параметры: стандартные + дополнительные из data
    val params = mutableListOf<Pair<String, Number>>().apply {
        add("duration_seconds" to (abilityInfo.duration_seconds ?: 0))
        add("number_uses" to abilityInfo.number_uses)
        add("recharge_time" to abilityInfo.recharge_time)
        abilityInfo.data.forEach { (key, value) -> add(key to value) }
    }
    // Получаем класс способности по типу (нужно реализовать функцию getAbilityClassByType)
    val abilityClass = getAbilityClassByType(abilityInfo.ability_type)
    return AbilityState(abilityClass, params)
}

// Временно заглушка для маппинга типа способности в KClass (можно добавить в отдельный объект)
private fun getAbilityClassByType(type: String): kotlin.reflect.KClass<out Ability> {
    return when (type) {
        "SHIELD" -> com.keikuethas.irlhideandseek.Shield::class
        "INTEL" -> com.keikuethas.irlhideandseek.Intel::class
        "SCAN" -> com.keikuethas.irlhideandseek.Scan::class
        "PERSONAL_BOMB" -> com.keikuethas.irlhideandseek.PersonalBomb::class
        "TRAP" -> com.keikuethas.irlhideandseek.Trap::class
        "SNARE" -> com.keikuethas.irlhideandseek.Snare::class
        "SAFE_HOUSE" -> com.keikuethas.irlhideandseek.SafeHouse::class
        "SAFE_MANSION" -> com.keikuethas.irlhideandseek.SafeMansion::class
        else -> com.keikuethas.irlhideandseek.Shield::class
    }
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

        // Находим имя текущей роли по ID
        val currentRoleName = state.roles.find { it.id == state.playerRole }?.name ?: "Не выбрана"

        // Преобразуем RoleFull в RoleState для диалога (включая способности)
        val playerRoles = state.roles.map { roleFull ->
            RoleState(
                roleName = roleFull.name,
                type = if (roleFull.victory_condition == "SEEKER") RoleType.SEEKER else RoleType.HIDER,
                abilities = roleFull.abilities.map { abilityInfoToAbilityState(it) },
                health = roleFull.health
            )
        }

        if (state.showRoleChangeDialog) {
            RoleChangeDialog(
                roles = playerRoles,
                playerRole = currentRoleName,
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
                roleType = if (state.roles.find { it.id == state.playerRole }!!.victory_condition == "SEEKER") RoleType.SEEKER else RoleType.HIDER, //refactor make normal class or methods
            )

            DisplayPlayers(
                playerList = state.players,
                roleList = playerRoles,
                playerName = state.playerName
            )

            Button(
                onClick = {
                    Log.d("LobbyScreen", "RequestRoleChangeDialog clicked")
                    onIntent(LobbyIntent.RequestRoleChangeDialog)
                },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Сменить роль")
            }

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
    cornerRadius: Dp = 24.dp
) {

    val activeColor = if (ready) roleType.color else Color.Gray

    Row(
        modifier = modifier
            .padding(2.dp),
        //horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier,
            onClick = { TODO() },
            color = if (ready) roleType.color.adjustLightness(-0.2F) else MaterialTheme.colorScheme.surfaceVariant,
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
                    if (ready) Icons.Default.Done else Icons.Default.DoneOutline,
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
            modifier = Modifier,
            onClick = { TODO() },
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
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 1
                )

                Text(
                    text = "Роль: $role",
                    style = typography.bodyLarge
                )
            }
        }
    }
}

private class RoleTypeProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> =
        sequenceOf("SEEKER", "HIDER")
}

@Preview
@Composable
fun LobbyContentPreview(
    @PreviewParameter(
        RoleTypeProvider::class,
        limit = 2
    ) roleType: String
) {
    val fakeRole = RoleFull(
        id = "role1",
        name = "Роль1",
        health = 100,
        victory_condition = roleType,
        abilities = emptyList(),
        events = emptyList()
    )
    val fakeRole2 = RoleFull(
        id = "role2",
        name = "Роль2",
        health = 100,
        victory_condition = "SEEKER",
        abilities = emptyList(),
        events = emptyList()
    )
    val previewState = LobbyState(
        playerName = "Реально длинное имя",
        playerRole = "role1",
        players = listOf("Игрок1" to "Роль1", "Игрок2" to "Роль2"),
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