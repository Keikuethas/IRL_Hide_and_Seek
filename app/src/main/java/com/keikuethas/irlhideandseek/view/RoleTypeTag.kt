package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keikuethas.irlhideandseek.RoleType
import com.keikuethas.irlhideandseek.ui.theme.color
import kotlinx.coroutines.launch

// Upgrade: добавить настройки размера
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RoleTypeTag(type: RoleType = RoleType.Seeker) {
    val toolTipState = rememberTooltipState(isPersistent = true, initialIsVisible = false)
    val coroutineScope = rememberCoroutineScope()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Below,
            5.dp
        ),
        tooltip = {
            RichTooltip(
                title = {
                    Row {
                        Text(
                            text = "Роль относится к типу ", //resource
                            style = typography.titleMedium
                        )
                        Text(
                            text = type.toString(),
                            style = typography.titleMedium,
                            color = type.color
                        )
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = type.description,
                        style = typography.bodyMedium
                    )
                }
            }
        },
        state = toolTipState,
        modifier = Modifier
    ) {
        RoleTypeLabel(type, onClick = { coroutineScope.launch { toolTipState.show() } })
    }
}

@Composable
fun RoleTypeLabel(type: RoleType, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, type.color),
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
                type.toString(),
                style = typography.labelLarge,
                color = type.color
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}