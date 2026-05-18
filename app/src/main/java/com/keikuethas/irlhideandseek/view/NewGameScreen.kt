package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameEffect
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameViewModel
import com.keikuethas.irlhideandseek.utils.LocationTracker
import com.keikuethas.irlhideandseek.view.newgame.SettingsButtons

@Composable
fun NewGameScreen(
    navController: NavController = rememberNavController(),
    newGameViewModel: NewGameViewModel = viewModel()
    ) {

    val state = newGameViewModel.state.collectAsStateWithLifecycle()

    SettingsButtons(navController)

    Spacer(Modifier.height(20.dp))

    OutlinedButton (
        onClick = { TODO("сброс настроек") },
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text("Сбросить все настройки")
    }
    ElevatedButton(
        onClick = { TODO("Создание игры на сервере") },
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text("Создать игру")
    }
    ElevatedButton(
        onClick = {
//            navController.navigate(Home)
            TODO("выход на домашний экран")
                  },
        modifier = Modifier.padding(top = 10.dp)
    ) {
        Text("Отмена")
    }

    //геолокация refactor отсюда в viewModel и дальше
    val locationTracker = LocationTracker(LocalContext.current)
    var host_lat: Double = 0.0
    var host_lng: Double = 0.0
    locationTracker.getCurrentLocation(
        onSuccess = { latitude, longitude ->
            host_lat = latitude
            host_lng = longitude
        },
        onError = {  } //todo обработчик
    )

    LaunchedEffect(key1 = Unit) {
        newGameViewModel.effect.collect { effect ->
            when(effect) {
                is NewGameEffect.JoinGame -> TODO()
                NewGameEffect.Quit -> TODO()
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNewGame() {
    NewGameScreen()
}