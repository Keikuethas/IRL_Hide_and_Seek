package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeEffect
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeIntent
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeState
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeType
import com.keikuethas.irlhideandseek.mvi.newGame.time.TimeViewModel
import com.keikuethas.irlhideandseek.view.components.AskingDialog
import com.keikuethas.irlhideandseek.view.components.CustomTimeInputDialog
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun TimeScreen(
    navController: NavController,
    viewModel: TimeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BackHandler(enabled = !state.showQuitDialog) {
        viewModel.onIntent(TimeIntent.RequestQuit)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                TimeEffect.Save, TimeEffect.Quit -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TextTopAppBar("Время") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimeConfigCard(
                title = "Время чтобы спрятаться",
                currentTimeSec = state.hideTime,
                onEditClick = { viewModel.onIntent(TimeIntent.RequestTimeChange(TimeType.Hide)) }
            )

            TimeConfigCard(
                title = "Время чтобы искать",
                currentTimeSec = state.seekTime,
                onEditClick = { viewModel.onIntent(TimeIntent.RequestTimeChange(TimeType.Seek)) }
            )

            TimeConfigCard(
                title = "Время между сужением зоны",
                currentTimeSec = state.shrinkTime,
                onEditClick = { viewModel.onIntent(TimeIntent.RequestTimeChange(TimeType.Shrink)) }
            )



            if (!state.isSeekTimeValid) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Минимальное время раунда: ${TimeState.minRoundTime / 60}м ${TimeState.minRoundTime % 60}с",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.onIntent(TimeIntent.RequestQuit) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Отменить")
                }

                Button(
                    onClick = { viewModel.onIntent(TimeIntent.Save) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    enabled = state.isSeekTimeValid
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Сохранить")
                }
            }
        }
    }

    if (state.editingType != null) {
        CustomTimeInputDialog(
            initTime = when (state.editingType) {
                TimeType.Hide -> state.hideTime
                TimeType.Seek -> state.seekTime
                TimeType.Shrink -> state.shrinkTime
                else -> TODO()
            },
            text = when(state.editingType) {
                TimeType.Hide -> "Таймаут перед раундом"
                TimeType.Seek -> "Время раунда"
                TimeType.Shrink -> "Период сужения зоны"
                else -> TODO()
            },
            onPick = { newValue ->
                viewModel.onIntent(TimeIntent.ChangeTime(newValue))
            },
            onDismiss = { viewModel.onIntent(TimeIntent.DeclineTimeChange) }
        )
    }

    if (state.showQuitDialog) {
        AskingDialog(
            title = "Подтверждение выхода",
            description = "Несохранённые изменения будут потеряны. Вы уверены?",
            confirmButtonText = "Выйти",
            dismissButtonText = "Отмена",
            onDismiss = { viewModel.onIntent(TimeIntent.DenyQuit) },
            onConfirm = { viewModel.onIntent(TimeIntent.ConfirmQuit) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeConfigCard(
    title: String,
    currentTimeSec: Int,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть: Заголовок, Иконка и Время
            Column(modifier = Modifier.weight(1f)) {
                // Строка с заголовком и иконкой
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Время
                Text(
                    text = formatTime(currentTimeSec),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Правая часть: Кнопка "Изменить"
            FilledTonalButton(
                onClick = onEditClick,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Изменить")
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
}