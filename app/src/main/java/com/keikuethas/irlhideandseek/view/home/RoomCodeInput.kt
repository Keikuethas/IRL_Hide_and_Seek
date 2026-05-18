package com.keikuethas.irlhideandseek.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ContentAlpha

@Composable
fun RoomCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = 6,
    enabled: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(enabled = enabled, onClick = { focusRequester.requestFocus() })
    ) {
        // Визуальные ячейки
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(if (enabled) 1f else ContentAlpha.disabled),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(maxLength) { index ->
                val char = if (index < code.length) code[index] else '_'
                val isCurrentInput = index == code.length && enabled

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .border(
                            width = if (isCurrentInput) 2.dp else 1.dp,
                            color = if (isCurrentInput) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                            shape = MaterialTheme.shapes.medium
                        )
                        .background(
                            color = if (isCurrentInput) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char.toString(),
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        color = if (char == '_') MaterialTheme.colorScheme.outline
                        else LocalContentColor.current
                    )
                }
            }
        }

        // Невидимое поле для захвата ввода
        BasicTextField(
            value = code,
            onValueChange = { newValue ->
                if (enabled) {
                    val filtered = newValue
                        .filter { it.isLetterOrDigit() }
                        .uppercase()
                        .take(maxLength)
                    onCodeChange(filtered)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii,
                capitalization = KeyboardCapitalization.Characters,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester)
                .alpha(0.0f),
            enabled = enabled,
            cursorBrush = SolidColor(Color.Transparent),
            decorationBox = { }
        )
    }
}