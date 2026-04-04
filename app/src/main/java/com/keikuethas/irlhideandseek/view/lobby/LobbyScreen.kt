package com.keikuethas.irlhideandseek.view.lobby

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.GameSettings
import com.keikuethas.irlhideandseek.GeneralScreen
import com.keikuethas.irlhideandseek.Player
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.view.Home

@Composable
fun LobbyScreen(
    innerPadding: PaddingValues,
    navController: NavController = rememberNavController()
) {

    // TODO: получение данных о комнате (запрос к серверу)
    // NOTE: список игроков должен обновляться динамически


    //TEMP: убрать после реализации запроса к серверу
    val playerList = mutableListOf<Player>()
    val hostId = "test"
    val gameSettings = GameSettings("test", hostId)
    playerList.add(Player("Player 1", PlayerRole.Seeker, id = hostId))
    playerList.add(Player("Player 2", PlayerRole.Hider))
    playerList.add(Player("Player 3", PlayerRole.Seeker))
    playerList.add(Player("Player 4", PlayerRole.Seeker))
    // ---

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SettingsButtons()

        Spacer(Modifier.height(20.dp))

        DisplayRoleCount(playerList)

        DisplayPlayers(playerList, gameSettings)

        // TODO: смена роли должна происходить через диалоговое окно
        // TODO: хост должен иметь возможность ограничить
        //  самостоятельную смену ролей участниками.
        //  В таком случае под кнопкой должен появляться
        //  поясняющий текст, а сама кнопка должна стать неактивной.
        ElevatedButton(
            onClick = {},
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text("Сменить роль")
        }

        ElevatedButton(
            onClick = { navController.navigate(Home) },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text("Покинуть игру")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LobbyPreview() {
    GeneralScreen { innerPadding -> LobbyScreen(innerPadding) }
}