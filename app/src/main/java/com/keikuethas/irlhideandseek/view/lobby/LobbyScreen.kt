package com.keikuethas.irlhideandseek.view.lobby

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.Websocket_V2.RoleFull
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyEffect
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyIntent
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyState
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.ErrorDialog
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.RoleChangeDialog

// Функция для преобразования AbilityInfo в AbilityState
private fun abilityInfoToAbilityState(abilityInfo: com.keikuethas.irlhideandseek.network.models.AbilityInfo): AbilityState {
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
private fun getAbilityClassByType(type: String): kotlin.reflect.KClass<out com.keikuethas.irlhideandseek.Ability> {
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
    viewModel: LobbyViewModel = hiltViewModel()
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
            onBackPressed = { viewModel.onIntent(LobbyIntent.QuitRequest) },
            onNavigateBack = { navController.popBackStack() }
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
    onBackPressed: () -> Unit,
    onNavigateBack: () -> Unit
) {
    BackHandler(onBack = onBackPressed)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Box { Text("Hide and Seek") } }) }
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
            Text(text = "Игрок: ${state.playerName}", modifier = Modifier.padding(top = 8.dp))
            Text(text = "Роль: $currentRoleName", modifier = Modifier.padding(bottom = 16.dp))

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
                onClick = { onIntent(LobbyIntent.QuitRequest)},
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Покинуть игру")
            }
        }
    }
}

@Preview
@Composable
fun LobbyContentPreview() {
    val fakeRole = RoleFull(
        id = "role1",
        name = "Роль1",
        health = 100,
        victory_condition = "HIDER",
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
        playerName = "Игрок",
        playerRole = "role1",
        players = listOf("Игрок1" to "Роль1", "Игрок2" to "Роль2"),
        roles = listOf(fakeRole, fakeRole2),
        showQuitDialog = false,
        showRoleChangeDialog = false
    )
    LobbyContent(
        state = previewState,
        onIntent = {},
        onBackPressed = {},
        onNavigateBack = {}
    )
}