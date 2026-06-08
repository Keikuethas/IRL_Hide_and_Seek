package com.keikuethas.irlhideandseek.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keikuethas.irlhideandseek.model.RoleType
import com.keikuethas.irlhideandseek.ui.theme.color
import kotlinx.coroutines.launch

// Upgrade: добавить настройки размера
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RoleTypeTag(
    type: RoleType = RoleType.SEEKER,
    maxFontSize: TextUnit = 36.sp
) {
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

                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle()) {
                                append("Роль относится к типу ")
                            }
                            withStyle(SpanStyle(color = type.color, fontWeight = FontWeight.Bold)) {
                                append(type.toString())
                            }
                        },
                        style = typography.titleMedium
                    )
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
        RoleTypeLabel(
            type = type,
            maxFontSize = maxFontSize
        ) { coroutineScope.launch { toolTipState.show() } }
    }
}

@Composable
fun RoleTypeLabel(
    type: RoleType,
    maxFontSize: TextUnit = 36.sp,
    onClick: () -> Unit = {},

    ) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, type.color),
        color = Color.White,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            text = type.toString(),
            style = typography.labelLarge,
            color = type.color,
            autoSize = TextAutoSize.StepBased(maxFontSize = maxFontSize),
            maxLines = 1
        )
    }
}