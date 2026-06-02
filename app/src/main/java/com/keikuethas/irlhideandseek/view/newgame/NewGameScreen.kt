package com.keikuethas.irlhideandseek.view.newgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameEffect
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameIntent
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameState
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameViewModel
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.EventSettings
import com.keikuethas.irlhideandseek.view.Lobby
import com.keikuethas.irlhideandseek.view.MapSettings
import com.keikuethas.irlhideandseek.view.RolesSettings
import com.keikuethas.irlhideandseek.view.TimeSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    navController: NavController = rememberNavController(),
    newGameViewModel: NewGameViewModel = hiltViewModel()
) {
    val state = newGameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        newGameViewModel.effect.collect { effect ->
            when (effect) {
                is NewGameEffect.JoinGame -> navController.navigate(
                    Lobby(
                        playerName = "", //todo,
                        roomName = state.value.roomName,
                        gameId = effect.gameID,
                        playerId = "host_id"
                    )
                )

                NewGameEffect.Quit -> navController.navigateUp()
                NewGameEffect.GoToEvents -> navController.navigate(EventSettings)
                NewGameEffect.GoToTime -> navController.navigate(TimeSettings)
                NewGameEffect.GoToMap -> navController.navigate(MapSettings)
                NewGameEffect.GoToRoles -> navController.navigate(RolesSettings)
            }
        }
    }

    NGSUI(
        state = state.value,
        onIntent = { newGameViewModel.onIntent(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NGSUI(
    state: NewGameState = NewGameState(),
    onIntent: (NewGameIntent) -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Новая игра",
                        style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Название игры
            OutlinedTextField(
                value = state.roomName,
                onValueChange = { onIntent(NewGameIntent.ChangeRoomName(it)) },
                label = { Text("Название игры") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Выбор пресета
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.selectedPreset,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Пресет настроек") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    state.presetList.forEach { preset ->
                        DropdownMenuItem(
                            text = { Text(preset) },
                            onClick = {
                                onIntent(NewGameIntent.SelectPreset(preset))
                                expanded = false
                            }
                        )
                    }
                }
            }


            ActionCard(
                title = "Настройка времени",
                description = "Задайте время пряток и поиска", // upgrade придумать слово получше
                icon = Icons.Default.Timelapse,
                onClick = { onIntent(NewGameIntent.GoToTime) },
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                iconBackgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                hasDrawer = false
            )

            // Секция ролей: Карточка + Ящик валидации
            Column {
                ActionCard(
                    title = "Настройка ролей",
                    description = "Создайте и настройте игровые роли",
                    icon = Icons.Default.Person,
                    onClick = { onIntent(NewGameIntent.GoToRoles) },
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconBackgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    hasDrawer = state.missingRoles.isNotEmpty()
                )
                if (state.missingRoles.isNotEmpty())
                    ValidationDrawer(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                        RoleValidationList(
                            missingRoles = state.missingRoles,
                            onCreateRole = { roleType ->
                                onIntent(
                                    NewGameIntent.CreateEmptyRole(
                                        roleType
                                    )
                                )
                            }
                        )
                    }
            }

            // Секция событий: Карточка + Ящик валидации
            Column {
                ActionCard(
                    title = "Настройка событий",
                    description = "Настройте игровые события",
                    icon = Icons.Default.Info,
                    onClick = { onIntent(NewGameIntent.GoToEvents) },
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconBackgroundColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    hasDrawer = !state.eventsConfigured
                )
                if (!state.eventsConfigured)
                    ValidationDrawer(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                null,
                                Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "События не включены",
                                style = typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
            }

            // Секция карты: Карточка + Ящик валидации
            Column {
                ActionCard(
                    title = "Настройка карты",
                    description = "Выберите и настройте игровую зону",
                    icon = Icons.Default.LocationOn,
                    onClick = { onIntent(NewGameIntent.GoToMap) },
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    iconBackgroundColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    hasDrawer = !state.mapConfigured
                )
                if (!state.mapConfigured){
                    ValidationDrawer(backgroundColor = MaterialTheme.colorScheme.surfaceVariant) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "Карта не настроена",
                                style = typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // Кнопка создания игры (блокируется, если есть ошибки валидации)
            Button(
                onClick = { onIntent(NewGameIntent.CreateGame) },
                enabled = state.canCreateGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.canCreateGame) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (state.canCreateGame) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Создать игру",
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (state.canCreateGame) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    )
                )
            }

            // Второстепенные кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onIntent(NewGameIntent.ResetSettings) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Сбросить", style = typography.labelLarge)
                }

                OutlinedButton(
                    onClick = { onIntent(NewGameIntent.QuitRequest) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Назад", style = typography.labelLarge)
                }
            }
        }

        // Диалоги
        if (state.quitDialogOpen) {
            AskingDialog(
                title = "Выход в меню",
                description = "Все текущие настройки будут потеряны. Продолжить?",
                confirmButtonText = "Выйти",
                dismissButtonText = "Отмена",
                onDismiss = { onIntent(NewGameIntent.QuitDialogRespond(false)) },
                onConfirm = { onIntent(NewGameIntent.QuitDialogRespond(true)) }
            )
        }
        if (state.resetDialogOpen) {
            AskingDialog(
                title = "Сброс настроек",
                description = "Вернуть все параметры к значениям по умолчанию?",
                confirmButtonText = "Сбросить",
                dismissButtonText = "Отмена",
                onDismiss = { onIntent(NewGameIntent.ResetDialogRespond(false)) },
                onConfirm = { onIntent(NewGameIntent.ResetDialogRespond(true)) }
            )
        }
    }
}

/**
 * Универсальная карточка-кнопка с динамической формой нижних углов
 */
@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    backgroundColor: Color,
    iconBackgroundColor: Color,
    contentColor: Color,
    hasDrawer: Boolean = false
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp),
        shape = RoundedCornerShape(
            topStart = 24.dp, topEnd = 24.dp,
            bottomStart = if (hasDrawer) 0.dp else 24.dp,
            bottomEnd = if (hasDrawer) 0.dp else 24.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconBackgroundColor,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(16.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = contentColor
                )
                Text(
                    text = description,
                    style = typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Эффект «выдвинутого ящика» для сообщений валидации
 */
@Composable
private fun ValidationDrawer(
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    // Создаем более темный оттенок фонового цвета для эффекта "утопленного" ящика
    // Умножаем каждый канал на 0.96 (затемнение на ~4%)
    val drawerColor = Color(
        red = backgroundColor.red * 0.96f,
        green = backgroundColor.green * 0.96f,
        blue = backgroundColor.blue * 0.96f,
        alpha = backgroundColor.alpha
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        color = drawerColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

/**
 * Компактный список ошибок в стиле IDE
 */
@Composable
private fun RoleValidationList(
    missingRoles: List<RoleType>,
    onCreateRole: (RoleType) -> Unit
) {
    if (missingRoles.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        missingRoles.forEach { roleType ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    null,
                    Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    "Отсутствует роль типа $roleType",
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { onCreateRole(roleType) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        "+ Создать",
                        style = typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}