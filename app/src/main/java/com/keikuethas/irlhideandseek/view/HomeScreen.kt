package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.GeneralScreen
import com.keikuethas.irlhideandseek.PlayerRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    navController: NavController = rememberNavController()
) {

    val name = remember { mutableStateOf("") }
    val gameID = remember { mutableStateOf("") }

    Column(
        Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .padding(vertical = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            name.value,
            onValueChange = { name.value = it.take(20) },
            label = { Row {
                Text("Отображаемое имя")
                Spacer(Modifier.width(10.dp))
                Icon(Icons.Default.Person, contentDescription = null)
            } },
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            supportingText = { Text("${name.value.length}/20") }
        )

        OutlinedTextField(
            gameID.value,
            onValueChange = { gameID.value = it.take(10) },
            label = {
                Row {
                    Text("Идентификатор комнаты")
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                }
            },
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            supportingText = { Text("${gameID.value.length}/10") }
        )

        Box(Modifier, contentAlignment = Alignment.BottomCenter) {
            ElevatedButton(onClick = {
                //TODO: добавить проверку на существование комнаты (связь с сервером)
                //Если комнаты нет, вывести диалог. окно с предл. создать комнату

                //TEMP: здесь переход на lobby
            navController.navigate(Game(PlayerRole.Seeker))
            }) { Text("Присоединиться") }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomePreview() {
    GeneralScreen { innerPadding -> HomeScreen(innerPadding) }
}