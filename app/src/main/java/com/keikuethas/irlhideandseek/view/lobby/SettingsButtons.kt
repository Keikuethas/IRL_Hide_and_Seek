package com.keikuethas.irlhideandseek.view.lobby

import android.adservices.appsetid.AppSetId
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsButtons() {
    // TODO: хост должен иметь возможность запретить
    //  просмотр правил для обычных игроков
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SettingsButton(onClick = {}, Icons.Default.Person, "Роли")
            SettingsButton(onClick = {}, Icons.Default.Build, "Правила")
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SettingsButton(onClick = { }, Icons.Default.Place, "Карта")
            SettingsButton(onClick = {}, Icons.Default.Add, "Бонусы")

        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SettingsButton(onClick = { }, Icons.Default.Settings, "Настройки")
        }
    }
}

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    imageVector: ImageVector = Icons.Default.Settings,
    text: String = ""
) {
    OutlinedButton(
        onClick = onClick,
        Modifier.wrapContentSize()
    ) {
        Row(Modifier.wrapContentSize()) {
            Icon(imageVector, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(text, Modifier.wrapContentSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LobbyPreviewLocal() = LobbyPreview()