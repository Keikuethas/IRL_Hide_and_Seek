package com.keikuethas.irlhideandseek.view.lobby

import androidx.activity.OnBackPressedDispatcher
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyEffect
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyIntent
import com.keikuethas.irlhideandseek.mvi.lobby.LobbyViewModel
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.Home
import com.keikuethas.irlhideandseek.view.RoleChangeDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    navController: NavController = rememberNavController(),
    playerName: String = "",
    roomName: String = "",
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    val state = lobbyViewModel.lobbyState.collectAsStateWithLifecycle()

    BackHandler { lobbyViewModel.onIntent(LobbyIntent.QuitRequest) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = {Box {Text("Hide and Seek")}}) }
    ) { innerPadding ->
        if (state.value.showQuitDialog)
            AskingDialog(
                title = "Покинуть лобби",
                description = "Вы действительно хотите покинуть лобби? Вы можете подключиться снова до начала игры.",
                confirmButtonText = "Выйти",
                dismissButtonText = "Отмена",
                onDismiss = { lobbyViewModel.onIntent(LobbyIntent.QuitDialogRespond(false)) },
                onConfirm = { lobbyViewModel.onIntent(LobbyIntent.QuitDialogRespond(true)) }
            )

        if (state.value.showRoleChangeDialog)
            RoleChangeDialog(
                state.value.roles,
                playerRole = state.value.playerRole,
                onDismiss = { lobbyViewModel.onIntent(LobbyIntent.DeclineRoleChange) },
                onRoleSelect = { roleName -> lobbyViewModel.onIntent(LobbyIntent.ChangeRole(roleName)) }
            )

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // todo отображение количества ролей в отдельном окне

            DisplayPlayers(
                state.value.players,
                state.value.roles,
                state.value.playerName
            )

            // TODO: смена роли должна происходить через диалоговое окно

            Button(
                onClick = { lobbyViewModel.onIntent(LobbyIntent.RequestRoleChangeDialog) },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Сменить роль")
            }
            ElevatedButton(
                onClick = { lobbyViewModel.onIntent(LobbyIntent.QuitRequest) },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Покинуть игру")
            }

        }

        LaunchedEffect(key1 = Unit) {
            lobbyViewModel.effect.collect { effect ->
                when (effect) {
                    LobbyEffect.Quit -> navController.navigate(Home)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LobbyPreview() {
    LobbyScreen()
}