package com.keikuethas.irlhideandseek.view.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keikuethas.irlhideandseek.mvi.home.HomeEffect
import com.keikuethas.irlhideandseek.mvi.home.HomeIntent
import com.keikuethas.irlhideandseek.mvi.home.HomeState
import com.keikuethas.irlhideandseek.mvi.home.HomeViewModel
import com.keikuethas.irlhideandseek.view.ErrorDialog
import com.keikuethas.irlhideandseek.view.Lobby

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = homeViewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // ✅ Лаунчер для запроса разрешений
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Все запрошенные разрешения должны быть выданы
        val allGranted = permissions.values.all { it }
        homeViewModel.onIntent(HomeIntent.PermissionResult(allGranted))
    }

    // ✅ Переопределяем обработку интентов: перехватываем RequestLocationPermission
    val handleIntent: (HomeIntent) -> Unit = { intent ->
        when (intent) {
            is HomeIntent.RequestLocationPermission -> {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            else -> homeViewModel.onIntent(intent)
        }
    }

    // ✅ Автопроверка разрешений при возврате в приложение (например, из Настроек)
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = com.keikuethas.irlhideandseek.LocationProvider.hasLocationPermission
                homeViewModel.onIntent(HomeIntent.PermissionResult(granted))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    HomeScreenUI(
        state = state.value,
        onIntent = handleIntent
    )

    LaunchedEffect(Unit) {
        homeViewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.OpenSettings -> {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }.also { context.startActivity(it) }
                }

                HomeEffect.HostLobby -> { /* TODO: навигация */
                }

                is HomeEffect.JoinLobby -> navController.navigate(
                    Lobby(
                        playerName = effect.playerName,
                        roomName = effect.roomName,
                        gameId = effect.gameId,
                        playerId = effect.playerId
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreenUI(
    state: HomeState = HomeState(),
    onIntent: (HomeIntent) -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "LURK",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        state.error?.let { error ->
            ErrorDialog(title = error.title, description = error.description) {
                onIntent(HomeIntent.DismissError)
            }
        }

        if (state.isLocationPermissionGranted) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                NicknameSection(
                    nickname = state.nameText,
                    onNicknameChange = { onIntent(HomeIntent.EditName(it)) },
                    counter = state.nameTextCounter,
                )
                Spacer(Modifier.height(16.dp))
                MainActionsSection(
                    onCreateGame = { onIntent(HomeIntent.CreateGame) },
                    roomCode = state.roomNameText,
                    onRoomCodeChange = { onIntent(HomeIntent.EditRoomName(it)) },
                    enabled = state.buttonsActive
                )
            }
        } else {
            PermissionDeniedSection(
                onRequestPermission = { onIntent(HomeIntent.RequestLocationPermission) },
                onOpenSettings = { onIntent(HomeIntent.GrantPermissions) }
            )
        }
    }
}