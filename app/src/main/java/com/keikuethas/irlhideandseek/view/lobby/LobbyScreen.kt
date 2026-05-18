package com.keikuethas.irlhideandseek.view.lobby

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyEffect
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyIntent
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyState
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyViewModel
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.RoleChangeDialog
import androidx.compose.material3.CircularProgressIndicator
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.view.ErrorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    navController: NavController = rememberNavController(),
    viewModel: LobbyViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    // Отображение ошибки, если есть
    if (!state.value.error.isNullOrBlank()) {
        ErrorDialog(
            title = "Ошибка",
            description = state.value.error ?: "",
            onDismiss = { /* тут можно сбросить ошибку, если нужно */ }
        )
    }

    // Индикатор загрузки, пока isLoading true
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
        // Диалог подтверждения выхода
        if (state.showQuitDialog) {
            AskingDialog(
                title = "Покинуть лобби",
                description = "Вы действительно хотите покинуть лобби? Вы можете подключиться снова до начала игры.",
                confirmButtonText = "Выйти",
                dismissButtonText = "Отмена",
                onDismiss = { onIntent(LobbyIntent.QuitDialogRespond(false)) },
                onConfirm = { onIntent(LobbyIntent.QuitDialogRespond(true)) }
            )
        }

        //TODO: загрузка ролей полноценно

        // Преобразуем List<String> в List<PlayerRole> для отображения (заглушка)
        val playerRoles = state.roles.map { roleName ->
            RoleState(PlayerRole(roleName, emptyList(), RoleType.Hider), 100)
        }

        // Диалог смены роли
        if (state.showRoleChangeDialog) {

            RoleChangeDialog(
                roles = playerRoles,
                playerRole = state.playerRole,
                onDismiss = { onIntent(LobbyIntent.DeclineRoleChange) },
                onRoleSelect = { roleName -> onIntent(LobbyIntent.ChangeRole(roleName)) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Отображение списка игроков
            DisplayPlayers(
                playerList = state.players,
                roleList = playerRoles,
                playerName = state.playerName
            )

            Button(
                onClick = { onIntent(LobbyIntent.RequestRoleChangeDialog) },
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

@Preview
@Composable
fun LobbyContentPreview() {
    val previewState = LobbyState(
        playerName = "Игрок",
        playerRole = "role_id",
        players = listOf("Игрок1" to "Роль1", "Игрок2" to "Роль2"),
        roles = listOf("Роль1", "Роль2"),
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