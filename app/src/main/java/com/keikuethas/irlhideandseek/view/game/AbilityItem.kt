package com.keikuethas.irlhideandseek.view.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.keikuethas.irlhideandseek.Ability
import com.keikuethas.irlhideandseek.SafeHouse
import com.keikuethas.irlhideandseek.utils.adjustLightness
import com.keikuethas.irlhideandseek.utils.makeGradientAbilityBrush

@Composable
fun AbilityItem(ability: Ability, progress: Float = 0.5f) {

    val gradientBrush = makeGradientAbilityBrush(ability, progress)

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center

    ) {
        Button(
            onClick = {},
            colors = buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .background(
                    brush = gradientBrush,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(2.dp, Brush.linearGradient(listOf(Color.Black, Color.Black)), shape = RoundedCornerShape(12.dp))
        ) {
            Column {
                Row(
                    Modifier
                        .wrapContentSize()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
//                        Icon(
//                            ability.icon,
//                            contentDescription = null
//                        )
                        Text(ability.name)
                    }
                    Row {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Text("${(progress * 100).toInt()}%")
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(ability.description)
            }
        }
    }

}

@Preview
@Composable
fun AbilityItemPreview(count: Int = 5, ability: Ability = SafeHouse()) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {
            items(
                count = count,
                itemContent = { AbilityItem(ability, it / (count - 1).toFloat()) }
            )
        }
    }
}