package com.keikuethas.irlhideandseek.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.Intel
import com.keikuethas.irlhideandseek.PersonalBomb
import com.keikuethas.irlhideandseek.PlayerRole
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.SafeMansion
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.ui.theme.color
import com.keikuethas.irlhideandseek.utils.adjustLightness
import kotlinx.coroutines.launch

@Composable
        /**
         * Окно для отображения информации об ошибке. Содержит только кнопку "закрыть".
         * @param onDismiss Функция, которая вызывается при закрытии окна
         */
fun RoleChangeDialog(
    roles: List<PlayerRole>,
    playerRole: String,
    onDismiss: () -> Unit = {},
    onRoleSelect: (roleName: String) -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        OutlinedCard(
            Modifier
                .fillMaxWidth()
                .height(900.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Выбор роли",
                    style = typography.headlineSmall
                )

                //content
                Surface(
                    color = Color(220, 220, 220, 255), //resource
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(700.dp)
                ) {
                    LazyColumn(
                        Modifier.padding(5.dp)
                    ) {
                        items(
                            items = roles
                        ) {
                            RoleCard(
                                it,
                                onRoleSelect = onRoleSelect,
                                padding = PaddingValues(vertical = 5.dp),
                                enabled = it.name != playerRole)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    Modifier.wrapContentSize()
                ) {
                    Text("Закрыть")
                }
            }
        }
    }
}

// refactor в отдельный файл
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleCard(
    role: PlayerRole,
    onRoleSelect: (roleName: String) -> Unit = {},
    padding: PaddingValues = PaddingValues(0.dp),
    enabled: Boolean = true
) {
    val expanded = remember { mutableStateOf(false) }

    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .padding(padding)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    role.name,
                    style = typography.headlineMedium
                )

                val toolTipState = rememberTooltipState(isPersistent = true)
                val coroutineScope = rememberCoroutineScope()
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip(
                            title = {
                                Row {
                                    Text(
                                        text = "Роль относится к типу ", //resource
                                        style = typography.titleMedium
                                    )
                                    Text(
                                        text = role.type.toString(), //resource
                                        style = typography.titleMedium,
                                        color = role.type.color
                                    )
                                }
                            }
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = role.type.description,
                                    style = typography.bodyMedium
                                )
                            }
                        }
                    },
                    state = toolTipState,
                    modifier = Modifier
                ) {
                    Surface(
                        onClick = { coroutineScope.launch { toolTipState.show() } },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(2.dp, role.type.color),
                        color = Color.White,
                        modifier = Modifier.padding(top = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(
                                Modifier
                                    .width(10.dp)
                                    .height(20.dp)
                            )
                            Text(
                                role.type.toString(),
                                style = typography.labelLarge,
                                color = role.type.color
                            )
                            Spacer(Modifier.width(10.dp))
                        }
                    }
                }
            }

            IconButton(
                onClick = { onRoleSelect(role.name) },
                colors = IconButtonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = Color.Unspecified
                ),
                enabled = enabled
            ) {
                Icon(if (enabled) Icons.Default.Start else Icons.Default.Check, null)
            }
        }
        Box(
            Modifier.fillMaxWidth()
        ) {
            ElevatedButton(
                onClick = { expanded.value = !expanded.value },
                modifier = Modifier.align(Alignment.Center),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Text(
                    "Способности",
                    style = typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Icon(
                    if (expanded.value) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    null
                )
            }
        }
        AnimatedVisibility(
            expanded.value
        ) {
            LazyColumn(
                Modifier.padding(horizontal = 10.dp)
            ) {
                items(role.abilities) { ability ->
                    AbilityCard(ability, PaddingValues(vertical = 5.dp))
                }
            }
        }
    }
}

@Composable
fun AbilityCard(ability: Ability, padding: PaddingValues = PaddingValues(0.dp)) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(padding),
        border = BorderStroke(2.dp, Color.Blue.adjustLightness(-0.2f))
    ) {
        Text(
            ability.name,
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            ability.description,
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(Modifier.height(10.dp))
        // resource
        ParamInfo("Использований", ability.number_uses)
        ParamInfo("Время перезарядки", ability.recharge_time, units = "с")
        ParamInfo("Время действия", ability.duration_seconds, units = "с")
        ability.ComposableForEachParam { name, value ->
            ParamInfo(name, value)
        }
    }
}

@Composable
fun ParamInfo(name: String, value: Number, style: TextStyle = typography.labelLarge, units: String = "") {

    // перевод на человеческий resource
    val _name: String = when(name) {
        "radius" ->  "радиус"
        "trap_duration_seconds" -> "время действия ловушки"
        "damage" -> "урон"
        else -> name
    }

    Row(
        Modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            _name,
            style = style
        )
        Text(
            "$value $units",
            style = style
        )
    }
}

@Preview
@Composable
private fun AbilityCardPreview() {
    AbilityCard(PersonalBomb())
}

@Preview
@Composable
private fun RoleCardPreview() {
    RoleCard(
        PlayerRole(
            "Охотник",
            listOf(Shield(), Intel(), PersonalBomb()), RoleType.Seeker
        )
    )
}

@Preview
@Composable
private fun RoleDialogPreview() {

    val roleList = listOf(
        PlayerRole("Житель", listOf(Shield(), Intel()), RoleType.Hider),
        PlayerRole("Бомбер", listOf(PersonalBomb()), RoleType.Seeker),
        PlayerRole("Мажор", listOf(Shield(),Intel(), SafeMansion()), RoleType.Hider)
    )

    RoleChangeDialog(
        roleList,
        playerRole = roleList.random().name
    )
}