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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import com.keikuethas.irlhideandseek.R
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.SafeMansion
import com.keikuethas.irlhideandseek.Shield
import com.keikuethas.irlhideandseek.mvi.newGame.roles.AbilityState
import com.keikuethas.irlhideandseek.mvi.newGame.roles.RoleState
import com.keikuethas.irlhideandseek.ui.theme.BarelyGrey
import com.keikuethas.irlhideandseek.utils.adjustLightness
import com.keikuethas.irlhideandseek.utils.description
import com.keikuethas.irlhideandseek.utils.name
import com.keikuethas.irlhideandseek.utils.paramName
import com.keikuethas.irlhideandseek.utils.unitName

@Composable
        /**
         *
         */
fun RoleChangeDialog(
    roles: List<RoleState>,
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
                    color = BarelyGrey,
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
                                enabled = it.roleName != playerRole
                            )
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
    role: RoleState,
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
                    role.roleName,
                    style = typography.headlineMedium
                )

                RoleTypeTag(role.type)
            }

            IconButton(
                onClick = { onRoleSelect(role.roleName) },
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
fun AbilityCard(ability: AbilityState, padding: PaddingValues = PaddingValues(0.dp)) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .padding(padding),
        border = BorderStroke(2.dp, Color.Blue.adjustLightness(-0.2f))
    ) {
        Text(
            ability.type.name(),
            style = typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            ability.type.description(),
            style = typography.bodyMedium,
            modifier = Modifier.padding(start = 10.dp)
        )
        Spacer(Modifier.height(10.dp))

        LazyColumn() {
            items(items = ability.params) {
                ParamInfo(it.first, it.second)
            }
        }
    }
}

@Composable
fun ParamInfo(
    name: String,
    value: Number,
    style: TextStyle = typography.labelLarge,
    unit: String = ""
) {

    // перевод на человеческий
    val _name = paramName(name)
    val _unit = unitName(name)

    Row(
        Modifier
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            _name,
            style = style
        )
        Text(
            "$value $unit",
            style = style
        )
    }
}

@Preview
@Composable
private fun AbilityCardPreview() {
    AbilityCard(AbilityState(PersonalBomb()))
}

@Preview
@Composable
private fun RoleCardPreview() {
    RoleCard(
        RoleState(PlayerRole(
            "Охотник",
            listOf(Shield(), Intel(), PersonalBomb()), RoleType.Seeker
        ), 100
    ))
}

@Preview
@Composable
private fun RoleDialogPreview() {

    val roleList = listOf(
        PlayerRole("Житель", listOf(Shield(), Intel()), RoleType.Hider),
        PlayerRole("Бомбер", listOf(PersonalBomb()), RoleType.Seeker),
        PlayerRole("Мажор", listOf(Shield(), Intel(), SafeMansion()), RoleType.Hider)
    )

    RoleChangeDialog(
        roleList.map { RoleState(it, 100) },
        playerRole = roleList.random().name
    )
}