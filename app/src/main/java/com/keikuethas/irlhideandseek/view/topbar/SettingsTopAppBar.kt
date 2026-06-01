package com.keikuethas.irlhideandseek.view.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    text: String = "Test",
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    bottomContentSpacing: Dp = 5.dp,
    bottomContent: @Composable () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(bottomContentSpacing)
            ) {
                MainContent(text, onBackClick, onSaveClick)

                bottomContent()
            }
        }, colors = colors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    text: String, onBackClick: () -> Unit, onSaveClick: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            MainContent(text, onBackClick, onSaveClick)
        }, colors = colors
    )
}

private val modifier = Modifier.padding(top = 5.dp)
private val colors
    @Composable get() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    )

@Composable
private fun MainContent(
    text: String, onBackClick: () -> Unit, onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

            ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text,
            style = typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            ),
        )

        IconButton(
            onClick = onSaveClick, colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface
            ), modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                Icons.Default.Save,
                null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}