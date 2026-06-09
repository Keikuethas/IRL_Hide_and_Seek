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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keikuethas.irlhideandseek.websocket.incoming.PlayerInfo
import com.keikuethas.irlhideandseek.websocket.incoming.RoleInfo

// upgrade
@Composable
fun DisplayPlayers(
    playerList: List<PlayerInfo>,
    roles: List<RoleInfo>
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(10.dp)
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

            HorizontalDivider()

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
                        }
                        Text(roles.find { it.id == item.role_id }?.name ?: "NULL")
                        Text(if (item.is_player_ready) "Готов" else "Не готов")
                    }
                }
            }
        }
    }
}

private fun getRoleById(roles: List<RoleInfo>, roleId: String) =
    roles.find { it.id == roleId }

@Preview
@Composable
private fun PlayerItem(
    playerName: String = "test name",
    role: String = "role",

) {

}