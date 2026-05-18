package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.keikuethas.irlhideandseek.R

@Preview
@Composable
        /**
         * Окно с двумя кнопками.
         * @param title Заголовок окна
         * @param description Основной текст окна
         * @param onDismiss Функция, которая вызывается при отклонении
         * @param onConfirm Функция, которая вызывается при подтверждении
         */
fun AskingDialog(
    title: String = "Подтверждение выхода",
    description: String = "Вы уверены, что хотите выйти?",
    confirmButtonText: String = "Выйти",
    dismissButtonText: String = "Отменить",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = { onDismiss() }
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        ADUI(title, description, confirmButtonText, dismissButtonText, onDismiss, onConfirm)
    }
}

@Preview
@Composable
private fun ADUI(
    title: String = "Подтверждение выхода",
    description: String = "Вы уверены, что хотите выйти?",
    confirmButtonText: String = "Выйти",
    dismissButtonText: String = "Отменить",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = { onDismiss() }
) {
    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .height(300.dp),
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
                title,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                description,
                Modifier.wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    Modifier.wrapContentSize()
                ) {
                    Text(dismissButtonText)
                }

                Button(
                    onClick = { onConfirm() },
                    Modifier.wrapContentSize()
                ) {
                    Text(confirmButtonText)
                }
            }

        }
    }
}

enum class DialogInputType { INT, FLOAT, STRING }

@Composable
fun ValueInputDialog(
    initialValue: Any = "",
    inputType: DialogInputType = DialogInputType.INT,
    description: String = "Введите новое значение поля",
    onDismiss: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
    ) {
        VIDUI(
            initialValue, inputType, description, onDismiss, onConfirm
        )
    }
}

@Preview
@Composable
private fun VIDUI(
    initialValue: Any = "",
    inputType: DialogInputType = DialogInputType.INT,
    description: String = "Введите новое значение поля",
    onDismiss: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {

    val value = remember { mutableStateOf(initialValue.toString()) }
    val invalidInput: Boolean = value.value.run {
        when (inputType) {
            DialogInputType.INT -> toIntOrNull()
            DialogInputType.FLOAT -> toFloatOrNull()
            DialogInputType.STRING -> this
        } == null
    }

    OutlinedCard(
        Modifier
            .fillMaxWidth()
            .height(300.dp),
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
                "Ввод значения",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                description,
                Modifier.wrapContentSize(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = value.value,
                onValueChange = { value.value = it },
                placeholder = { Text("Значение") },
                isError = invalidInput
            )

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    Modifier.wrapContentSize()
                ) {
                    Text(stringResource(R.string.Revert))
                }

                Button(
                    onClick = { onConfirm(value.value) },
                    Modifier.wrapContentSize(),
                    enabled = !invalidInput
                ) {
                    Text(stringResource(R.string.Save))
                }
            }

        }
    }
}