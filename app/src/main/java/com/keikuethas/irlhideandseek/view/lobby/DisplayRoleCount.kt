package com.keikuethas.irlhideandseek.view.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keikuethas.irlhideandseek.Player
import com.keikuethas.irlhideandseek.PlayerRole

// Describe
@Composable
fun DisplayRoleCount(playerList: Collection<Player>) {
    LazyRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        itemsIndexed(
            items = PlayerRole.entries,
            key = { _: Int, item: PlayerRole -> item.name },
            itemContent = { _, item ->
                Text("${item.name}: ${playerList.count { it.role == item }} чел.")
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LobbyPreviewLocal() = LobbyPreview()
