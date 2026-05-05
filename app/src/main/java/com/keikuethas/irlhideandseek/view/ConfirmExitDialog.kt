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
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
    description: String = "Это длинное описание для демонстрации окна ошибки. Оно такое большое, что занимает несколько строк.",
    confirmButtonText: String = "",
    dismissButtonText: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {onDismiss()}
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        )
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
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
}