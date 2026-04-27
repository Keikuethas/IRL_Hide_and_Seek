package com.keikuethas.irlhideandseek.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.GeneralScreen
import com.keikuethas.irlhideandseek.mvi.home.HomeEffect
import com.keikuethas.irlhideandseek.mvi.home.HomeIntent
import com.keikuethas.irlhideandseek.mvi.home.HomeViewModel

// refactor вынести строки в ресурсы
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues = PaddingValues(),
    navController: NavController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    // состояние экрана
    val state = homeViewModel.homeState.collectAsStateWithLifecycle()

    // refactor наверняка это должно быть не здесь
    // есть ли доступ к геолокации
    val permissionsGranted =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    state.value.error?.let {error ->
        ErrorDialog(
            title = error.title,
            description = error.description
        ) { homeViewModel.onIntent(HomeIntent.DismissError)}
    }

    if (permissionsGranted) {
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(vertical = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                state.value.nameText,
                onValueChange = { value -> homeViewModel.onIntent(HomeIntent.EditName(value)) },
                label = {
                    Row {
                        Text("Отображаемое имя")
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                },
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                supportingText = { Text(state.value.nameTextCounter) }
            )

            OutlinedTextField(
                state.value.roomNameText,
                onValueChange = { value -> homeViewModel.onIntent(HomeIntent.EditRoomName(value)) },
                label = {
                    Row {
                        Text("Идентификатор комнаты")
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    }
                },
                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                supportingText = { Text(state.value.roomNameTextCounter) }
            )

            ElevatedButton(
                onClick = { homeViewModel.onIntent(HomeIntent.JoinGame) },
                enabled = state.value.buttonsActive
            ) { Text("Присоединиться") }

            ElevatedButton(
                onClick = { homeViewModel.onIntent(HomeIntent.CreateGame) },
                enabled = state.value.buttonsActive
            ) { Text("Создать") }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 50.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Игровой процесс построен на работе с Вашим местоположением в реальной жизни. " +
                        "Пожалуйста, предоставьте разрешение на работу с геолокацией."
            )
            ElevatedButton(
                onClick = { homeViewModel.onIntent(HomeIntent.GrantPermissions) },
            ) { Text("Предоставить разрешение") }
        }
    }

    LaunchedEffect(key1 = Unit) {
        homeViewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.OpenSettings -> {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }.let { context.startActivity(it) }
                }

                HomeEffect.HostLobby -> TODO("Навигация на новый экран")
                is HomeEffect.JoinLobby -> TODO("Навигация на старый переделанный экран")
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomePreview() {
    GeneralScreen { innerPadding -> HomeScreen(innerPadding) }
}