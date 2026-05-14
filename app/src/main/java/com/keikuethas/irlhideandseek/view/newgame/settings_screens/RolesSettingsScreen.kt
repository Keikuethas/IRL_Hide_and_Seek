package com.keikuethas.irlhideandseek.view.newgame.settings_screens

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.twotone.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.SafeHouse
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.mvi.newGame.main.NewGameViewModel
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSEffect
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSIntent
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSResult
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RSState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RolesSettingsViewModel
import com.keikuethas.irlhideandseek.ui.theme.BarelyGrey
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.dashedBorder
import com.keikuethas.irlhideandseek.utils.description
import com.keikuethas.irlhideandseek.utils.name
import com.keikuethas.irlhideandseek.utils.paramName
import com.keikuethas.irlhideandseek.utils.unitName
import com.keikuethas.irlhideandseek.view.AskingDialog
import com.keikuethas.irlhideandseek.view.RoleTypeLabel
import com.keikuethas.irlhideandseek.view.ValueInputDialog
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RolesSettingsScreen(
    navController: NavController = rememberNavController(),
    sharedGameVM: NewGameViewModel = viewModel(),
    rolesSettingsViewModel: RolesSettingsViewModel = viewModel()
) {
    val state = rolesSettingsViewModel.state.collectAsStateWithLifecycle()
    RSSUI(state.value, { rolesSettingsViewModel.onIntent(it) })
    BackHandler { rolesSettingsViewModel.onIntent(RSIntent.QuitRequest)  }
    LaunchedEffect(key1 = Unit) {
        rolesSettingsViewModel.effect.collect { effect ->
            when (effect) {
                RSEffect.Quit ->
                    navController.navigate(navController.previousBackStackEntry!!.destination)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RSSUI(
    state: RSState = RSState(
        roles = listOf(
            RoleState(PlayerRole("new role", type = RoleType.Hider), 100)
        )
    ),
    onIntent: (RSIntent) -> Unit = {}
) {
    when {
        state.showQuitDialog ->
            AskingDialog(
                description = "Сделанные вами изменения не сохранятся.",
                confirmButtonText = "Выйти",
                onDismiss = { onIntent(RSIntent.QuitAnswer(false)) },
                onConfirm = { onIntent(RSIntent.QuitAnswer(true)) }
            )

        state.showRoleRemoveDialog ->
            AskingDialog(
                title = "Удалить роль",
                description = "Удалить роль ${state.roles[state.currentRole].roleName}? " +
                        "Это действие не может быть отменено.",
                confirmButtonText = "Удалить",
                onDismiss = { onIntent(RSIntent.RoleDeleteAnswer(false)) },
                onConfirm = { onIntent(RSIntent.RoleDeleteAnswer(true)) }
            )

        state.showValueInputDialog != null ->
            ValueInputDialog(
                inputType = state.showValueInputDialog.inputType,
                initialValue = state.showValueInputDialog.initialValue,
                description = "Введите новое значение для [${paramName(state.showValueInputDialog.paramName)}]",
                onDismiss = { onIntent(RSIntent.ValueChangeDismiss) },
                onConfirm = { onIntent(RSIntent.ValueChanged(newValue = it)) }
            )

        state.showRoleTypeDialog ->
            RoleTypeChangeDialog(
                currentType = state.roles[state.currentRole].type,
                onDismiss = { onIntent(RSIntent.RoleTypeChangeAnswer(false)) },
                onChange = { onIntent(RSIntent.RoleTypeChangeAnswer(true)) }
            )

        state.showAbilityAddDialog ->
            AbilityAddDialog(
                onDismiss = { onIntent(RSIntent.AddAbilityDismissed) },
                onSelect = { onIntent(RSIntent.AddAbility(it)) },
                abilities = state.roles[state.currentRole].remainingAbilities
            )
    }

    Scaffold(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            "Настройка ролей",
                            style = typography.headlineLarge,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    onClick = { onIntent(RSIntent.ArrowClick(false)) }
                ) {
                    Icon(
                        Icons.Default.ChevronLeft, null,
                        tint = Color.Blue,
                    )
                }

                if (state.currentRole < state.roles.size)
                    RoleElement(
                        state.roles[state.currentRole],
                        onRoleNameClick = { onIntent(RSIntent.RoleNameClick) },
                        onRoleTypeClick = { onIntent(RSIntent.RoleTypeClick) },
                        onParamValueClick = { type, name ->
                            onIntent(RSIntent.ParamClick(type, name))
                        },
                        onDelete = { onIntent(RSIntent.RoleDeleteRequest) },
                        onAddAbility = { onIntent(RSIntent.AddAbilityRequest) },
                        onHealthClick = { onIntent(RSIntent.RoleHealthClick) }
                    ) else EmptyRoleElement { onIntent(RSIntent.RoleCreate) }

                Surface(
                    onClick = { onIntent(RSIntent.ArrowClick(true)) }
                ) {
                    Icon(
                        Icons.Default.ChevronRight, null,
                        tint = Color.Blue
                    )
                }
            }

            Button(
                onClick = { onIntent(RSIntent.Save) }
            ) {
                Text("Сохранить")
            }

            OutlinedButton(
                onClick = { onIntent(RSIntent.QuitRequest) }
            ) {
                Text("Отменить")
            }
        }
    }
}

@Composable
fun AbilityAddDialog(
    onDismiss: () -> Unit,
    onSelect: (KClass<out Ability>) -> Unit,
    abilities: List<KClass<out Ability>>
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        AADUI(
            onDismiss, onSelect, abilities
        )
    }
}

@Preview
@Composable
fun AADUI(
    onDismiss: () -> Unit = {},
    onSelect: (KClass<out Ability>) -> Unit = {},
    abilities: List<KClass<out Ability>> = listOf(Shield::class)
) {
    OutlinedCard(
        modifier = Modifier.width(300.dp)
    ) {
        Text(
            "Выберите способность",
            style = typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Surface(
            color = BarelyGrey,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.height(400.dp)
        ) {
            LazyColumn(
                Modifier.padding(5.dp)
            ) {
                items(
                    items = abilities
                ) {
                    AbilityInfoCard(type = it, onClick = { onSelect(it) })
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.Revert))
        }
    }
}

@Composable
private fun AbilityInfoCard(
    modifier: Modifier = Modifier,
    type: KClass<out Ability> = Shield::class,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        onClick = onClick
    ) {
        Text(
            type.name(),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            type.description(),
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp, bottom = 5.dp)
        )
    }
}

@Composable
fun EmptyRoleElement(
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .dashedBorder(4.dp, Color.Black, 5.dp, 5.dp, 16.dp)
            .padding(8.dp)
            .height(480.dp)
            .width(280.dp)
            .padding(bottom = 10.dp, end = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
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
                    stringResource(R.string.CreateRole),
                    style = typography.bodyLarge
                )
                Icon(
                    Icons.Default.Add, null
                )
            }
        }
    }
}

@Composable
fun RoleElement(
    state: RoleState = RoleState(
        role = PlayerRole(
            "Role name", listOf(
                Shield(), SafeHouse(), Intel(), PersonalBomb()
            ), RoleType.Hider
        ),
        health = 100
    ),
    onRoleNameClick: () -> Unit = {},
    onRoleTypeClick: () -> Unit = {},
    onParamValueClick: (type: KClass<out Ability>, name: String) -> Unit = { _, _ -> },
    onDelete: () -> Unit = {},
    onAddAbility: () -> Unit = {},
    onHealthClick: () -> Unit = {}
) {
    Box(
        Modifier
            .width(320.dp)
            .height(520.dp)
    )
    {
        ElevatedCard(
            Modifier
                .height(500.dp)
                .width(300.dp)
                .padding(top = 10.dp)
                .padding(end = 10.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                )
                {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            state.roleName,
                            modifier = Modifier.clickable(
                                onClick = onRoleNameClick,
                                indication = null,
                                interactionSource = null,
                            ),
                            textAlign = TextAlign.Center,
                            style = typography.headlineLarge,
                            textDecoration = TextDecoration.Underline
                        )
                        RoleTypeLabel(state.type, onClick = onRoleTypeClick)


                    }

                }

                Surface(
                    onClick = onHealthClick,
                    color = Color.Transparent
                ) {
                    Text(
                        "Здоровье: ${state.health}",
                        style = typography.headlineMedium
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedCard(
                    modifier = Modifier.width(350.dp)
                ) {
                    Text(
                        text = "Способности",
                        style = typography.headlineSmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.padding(5.dp),
                        color = BarelyGrey,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        LazyColumn(

                        ) {
                            items(
                                items = state.abilities
                            ) { state ->
                                AbilityCard(
                                    state = state,
                                    modifier = Modifier
                                        .padding(10.dp, vertical = 5.dp)
                                        .wrapContentHeight()
                                        .heightIn(max = 1000.dp),
                                    onParamValueClick = onParamValueClick
                                )
                            }

                            if (state.displayAbilityAdd)
                                item { EmptyAbilityCard(modifier = Modifier
                                    .padding(10.dp, vertical = 5.dp)
                                    .wrapContentHeight(), onClick = onAddAbility) }
                        }
                    }
                }
            }
        }
        Surface(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(40.dp),
            color = Color.Transparent
        )
        {
            Icon(
                Icons.Default.DeleteForever,
                null,
                tint = Color.Red,
                )
        }
    }
}

@Composable
private fun EmptyAbilityCard(
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
                    "Добавить способность",
                    style = typography.bodyLarge
                )
                Icon(
                    Icons.Default.Add, null
                )
            }
        }
    }
}

@Composable
private fun AbilityCard(
    modifier: Modifier = Modifier,
    state: AbilityState = AbilityState(Shield()),
    onParamValueClick: (type: KClass<out Ability>, name: String) -> Unit = { _, _ -> }
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black)
    ) {
        Text(
            state.type.name(),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            state.type.description(),
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp)
        )

        Spacer(Modifier.height(5.dp))
        HorizontalDivider()
        Spacer(Modifier.height(5.dp))

        LazyColumn(

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

@Composable
fun ParamInfo(
    name: String,
    value: Number,
    style: TextStyle = typography.labelLarge,
    onValueClick: () -> Unit = {}
) {

    // перевод на человеческий
    val _name = paramName(name)
    val _unit = unitName(name)

    Row(
        Modifier
            .padding(vertical = 0.dp, horizontal = 10.dp)
            .fillMaxWidth()
            .height(30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            _name,
            style = style
        )
        Surface(
            modifier = Modifier.wrapContentHeight(),
            onClick = { onValueClick() }
        ) {
            Text(
                "$value $_unit",
                style = style,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
fun RoleTypeChangeDialog(
    currentType: RoleType = RoleType.Seeker,
    onDismiss: () -> Unit = {},
    onChange: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {

        RTCDUI(
            currentType = currentType, onDismiss = onDismiss, onChange = onChange
        )
    }
}

@Composable
fun RTCDUI(
    currentType: RoleType = RoleType.Hider,
    onDismiss: () -> Unit = {},
    onChange: () -> Unit = {}
) {
    OutlinedCard(
        Modifier.width(500.dp)
    ) {
        Text(
            "Смена условия победы",
            style = typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            textAlign = TextAlign.Center
        )
        if (currentType == RoleType.Seeker) {
            RTCDVariant(RoleType.Seeker, selected = true, onClick = onDismiss)
            Spacer(Modifier.height(10.dp))
            RTCDVariant(type = RoleType.Hider, selected = false, onClick = onChange)
        } else {
            RTCDVariant(RoleType.Hider, selected = true, onClick = onDismiss)
            Spacer(Modifier.height(10.dp))
            RTCDVariant(type = RoleType.Seeker, selected = false, onClick = onChange)
        }

        Surface(
            modifier = Modifier.padding(10.dp), color = Color(139, 195, 74, 255), //resource
            border = BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.TwoTone.Check, null, modifier = Modifier.size(100.dp)
                )
                Text(
                    "При смене условия победы настройки способностей не будут сброшены.",
                    style = typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

//@Preview
@Composable
fun RTCDVariant(
    type: RoleType = RoleType.Seeker, selected: Boolean = true, onClick: () -> Unit = {}
) {
    Surface(
        Modifier
            .padding(horizontal = 10.dp)
            .run {
                if (!selected) this.dashedBorder(
                    width = 2.dp,
                    color = type.color,
                    dashLength = 5.dp,
                    gapLength = 5.dp,
                    cornerRadius = 10.dp
                ) else this
            }
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = if (selected) BorderStroke(2.dp, type.color) else null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    type.toString(),
                    color = type.color,
                    style = typography.titleLarge,
                    modifier = Modifier.padding(start = 15.dp, bottom = 5.dp)
                )

                if (selected) OutlinedButton(
                    onClick = onClick, border = BorderStroke(2.dp, type.color)
                ) {
                    Text(
                        "Оставить", // resource
                        style = typography.labelMedium, color = type.color
                    )
                }
                else Button(
                    onClick = onClick, colors = ButtonDefaults.buttonColors(
                        containerColor = type.color
                    )
                ) {
                    Text(
                        "Изменить", // resource
                        style = typography.labelMedium
                    )
                }

            }

            Text(
                type.description,
                style = typography.bodyMedium,
                modifier = Modifier.padding(bottom = 5.dp),
                textAlign = TextAlign.Justify
            )
        }
    }
}