package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.keikuethas.irlhideandseek.utils.LocationTracker
import com.keikuethas.irlhideandseek.view.lobby.SettingsButtons

@Composable
fun NewGameScreen() {

//    SettingsButtons(navController)

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

    //геолокация refactor отсюда в viewModel
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
}