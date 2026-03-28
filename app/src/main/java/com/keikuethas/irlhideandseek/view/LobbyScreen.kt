package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonElevation
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
import com.keikuethas.irlhideandseek.GeneralScreen
import com.keikuethas.irlhideandseek.Player
import com.keikuethas.irlhideandseek.PlayerRole

@Composable
fun LobbyScreen(
    innerPadding: PaddingValues,
    navController: NavController = rememberNavController()
) {

    // TODO: получение данных о комнате (запрос к серверу)
    // NOTE: список игроков должен обновляться динамически
    val playerList = mutableListOf<Player>()

    //TEMP: убрать после реализации запроса к серверу
    playerList.add(Player("Player 1", PlayerRole.Hider))
    playerList.add(Player("Player 2", PlayerRole.Hider))
    playerList.add(Player("Player 3", PlayerRole.Seeker))
    // ---

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            //REFACTOR: Пока что сделал абстрактно, в частности там будет "ищут:" и "прячутся:"
            Text("Роль 1: *кол-во*")
            Spacer(Modifier.width(40.dp))
            Text("Роль 2: *кол-во*")
        }

        LazyColumn(
            Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                playerList, key = { index: Int, item: Player -> item.id },
                itemContent = { index, item ->
                    Text("${item.name} - ${item.role.toString()}")
                }
            )
        }

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
            onClick = {navController.navigate(Home)},
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