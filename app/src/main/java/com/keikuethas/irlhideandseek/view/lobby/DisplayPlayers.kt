package com.keikuethas.irlhideandseek.view.lobby

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo

// upgrade
@Composable
fun DisplayPlayers(
    playerList: List<PlayerInfo>,
    playerName: String
) {
    Column(
        Modifier
            .padding(top = 20.dp, bottom = 10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        val rowModifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp)

        Row(
            rowModifier.padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Имя")
            Text("Роль")
            Text("Готов")
        }

        LazyColumn(
            Modifier.scrollable(
                orientation = Orientation.Vertical,
                state = ScrollableState { 0.01F * it }
            )
        ) {
            items(
                items = playerList
            ) { item ->
                Row(
                    rowModifier,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(item.name)
                        if (item.name == playerName)
                            Icon(Icons.Default.Star, contentDescription = null)
                    }
                    Text(item.role_ref?.name ?: "?")
                    Text(if (item.is_player_ready) "Готов" else "Не готов")
                }
            }
        }
    }
}