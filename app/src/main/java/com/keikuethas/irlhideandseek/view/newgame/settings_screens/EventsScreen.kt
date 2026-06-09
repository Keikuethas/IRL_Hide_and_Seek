package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.model.ActivationFrequency
import com.keikuethas.irlhideandseek.model.EventType
import com.keikuethas.irlhideandseek.mvi.newGame.events.ESEffect
import com.keikuethas.irlhideandseek.mvi.newGame.events.ESIntent
import com.keikuethas.irlhideandseek.mvi.newGame.events.ESState
import com.keikuethas.irlhideandseek.mvi.newGame.events.EventState
import com.keikuethas.irlhideandseek.mvi.newGame.events.EventsViewModel
import com.keikuethas.irlhideandseek.ui.theme.BarelyGrey
import com.keikuethas.irlhideandseek.utils.Description
import com.keikuethas.irlhideandseek.utils.Name
import com.keikuethas.irlhideandseek.utils.dashedBorder
import com.keikuethas.irlhideandseek.utils.paramName
import com.keikuethas.irlhideandseek.view.components.AskingDialog
import com.keikuethas.irlhideandseek.view.components.ValueInputDialog
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar

// NOTE:
//  Вообще я здесь не очень хорошо сделал, перекопировав код из RolesScreen.kt
//  По-хорошему логика должна быть переиспользована, т.е. нужно сделать функции-шаблоны, а потом
//  использовать их в отдельных экранах
@Composable
fun EventsSettingsScreen(
    navController: NavController,
    ESVM: EventsViewModel = hiltViewModel()
) {
    val state = ESVM.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        ESVM.effect.collect {
            when (it) {
                ESEffect.Quit -> navController.navigateUp()
                ESEffect.Save -> {
                    navController.navigateUp()
                }
            }
        }
    }

    BackHandler { ESVM.onIntent(ESIntent.RequestQuit) }

    ESUI(
        state = state.value,
        onIntent = { ESVM.onIntent(it) }
    )
}

@Preview
@Composable
fun ESUI(
    state: ESState = ESState(),
    onIntent: (ESIntent) -> Unit = {}
) {
    when {
        state.showQuitDialog ->
            AskingDialog(
                title = "Вы уверены?",
                description = "Сделанные вами изменения не сохранятся.",
                confirmButtonText = "Выйти",
                onDismiss = { onIntent(ESIntent.QuitAnswer(false)) },
                onConfirm = { onIntent(ESIntent.QuitAnswer(true)) },
                dismissButtonText = "Отменить"
            )

        state.showValueInputDialog != null ->
            ValueInputDialog(
                inputType = state.showValueInputDialog.inputType,
                initialValue = state.showValueInputDialog.initialValue,
                description = "Введите новое значение для [${paramName(state.showValueInputDialog.paramName)}]",
                onDismiss = { onIntent(ESIntent.ValueChangeDismiss) },
                onConfirm = { onIntent(ESIntent.ValueChanged(newValue = it)) }
            )

        state.showEventAddDialog ->
            EventAddDialog(
                onDismiss = { onIntent(ESIntent.AddEventDismissed) },
                onSelect = { onIntent(ESIntent.AddAbility(it)) },
                events = state.unsetEvents
            )
    }

    Scaffold(
        topBar = { TextTopAppBar("События") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Surface(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(0.75f),
                color = BarelyGrey,
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn {
                    items(
                        items = state.events
                    ) { eventState ->
                        EventCard(
                            state = eventState,
                            modifier = Modifier
                                .padding(10.dp, vertical = 5.dp)
                                .wrapContentHeight()
                                .heightIn(max = 1000.dp),
                            onParamValueClick = { type, name ->
                                onIntent(
                                    ESIntent.RequestValueChange(
                                        type,
                                        name
                                    )
                                )
                            },
                            onDeleteEvent = { onIntent(ESIntent.DeleteEvent(it)) },
                            onFrequencyChanged = {
                                onIntent(
                                    ESIntent.ChangeFrequency(
                                        eventState.type,
                                        it
                                    )
                                )
                            }
                        )
                    }

                    if (state.displayAddEvent)
                        item {
                            EmptyEventCard(
                                modifier = Modifier
                                    .padding(10.dp, vertical = 5.dp)
                                    .wrapContentHeight(),
                                onClick = { onIntent(ESIntent.RequestAddEvent) }
                            )
                        }
                }
            }

            Column(
                modifier = Modifier.weight(0.25F),
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { onIntent(ESIntent.SaveSettings) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Сохранить",
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Второстепенные кнопки
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { onIntent(ESIntent.ResetSettings) },
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
                        onClick = { onIntent(ESIntent.RequestQuit) },
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
        }
    }
}

@Composable
fun EventAddDialog(
    onDismiss: () -> Unit,
    onSelect: (EventType) -> Unit,
    events: List<EventType>
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        EADUI(
            onDismiss, onSelect, events
        )
    }
}

@Preview
@Composable
fun EADUI(
    onDismiss: () -> Unit = {},
    onSelect: (EventType) -> Unit = {},
    events: List<EventType> = EventType.entries
) {
    OutlinedCard(
        modifier = Modifier.width(300.dp)
    ) {
        Text(
            "Выберите событие",
            style = typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(400.dp)
        ) {
            LazyColumn(
                Modifier.padding(5.dp)
            ) {
                items(
                    items = events
                ) {
                    EventInfoCard(type = it, onClick = { onSelect(it) })
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.CloseLabel))
        }
    }
}

@Composable
private fun EmptyEventCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .dashedBorder(2.dp, Color.Black, 4.dp, 4.dp, 16.dp)
            .padding(4.dp)
            .fillMaxWidth()
            .height(200.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    "Добавить событие",
                    style = typography.bodyLarge
                )
                Icon(
                    Icons.Default.Add, null
                )
            }
        }
    }
}

@Preview
@Composable
private fun EventInfoCard(
    modifier: Modifier = Modifier,
    type: EventType = EventType.BOMBARDMENT,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        onClick = onClick
    ) {
        Text(
            type.Name,
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            type.Description,
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCard(
    modifier: Modifier = Modifier,
    state: EventState,
    onParamValueClick: (type: EventType, name: String) -> Unit = { _, _ -> },
    onDeleteEvent: (type: EventType) -> Unit = {},
    onFrequencyChanged: (newValue: ActivationFrequency) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Заголовок и кнопка удаления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    state.type.Name,
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = { onDeleteEvent(state.type) }) {
                    Icon(
                        Icons.Default.DeleteForever,
                        null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                state.type.Description,
                style = typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.frequency.Name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Частота срабатывания") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    ActivationFrequency.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.Name) },
                            onClick = {
                                isExpanded = false
                                onFrequencyChanged(type)
                            },
                            leadingIcon = {
                                if (type == state.frequency) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Параметры события
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.params) { item ->
                    ParamInfo(
                        item.first,
                        item.second,
                        onValueClick = { onParamValueClick(state.type, item.first) })
                }
            }
        }
    }
}

@Composable
fun ParamInfo(
    name: String,
    value: Number,
    onValueClick: () -> Unit = {}
) {
    val _name = paramName(name)
    val _unit = com.keikuethas.irlhideandseek.utils.unitName(name)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(2/3F),
            text = _name,
            style = typography.bodyMedium
        )
        TextButton(
            onClick = onValueClick
        ) {
            Text(
                "${value.toInt()} $_unit",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(maxFontSize = 14.sp)
            )
        }
    }
}