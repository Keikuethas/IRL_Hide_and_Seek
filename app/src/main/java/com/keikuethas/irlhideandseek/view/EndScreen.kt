package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.mvi.endscreen.EndIntent
import com.keikuethas.irlhideandseek.mvi.endscreen.EndState
import com.keikuethas.irlhideandseek.mvi.endscreen.EndViewModel
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar

//TODO экран конца игры (победа/поражение)

@Composable
fun EndScreen(
    navController: NavController = rememberNavController(),
    viewMode: EndViewModel = hiltViewModel()
) {

}

@Preview(showBackground = true)
@Composable
fun EndContent(
    state: EndState = EndState(victory = false),
    onIntent: (EndIntent) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TextTopAppBar("Конец игры") }
    ) {innerPadding ->



        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(3 / 4F)
                    .fillMaxHeight(1 / 3F)
                    .align(Alignment.Center)
                    ,
                colors = CardDefaults.cardColors(
                    containerColor = with(colorScheme) {
                        if (state.victory) primaryContainer
                        else errorContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (state.victory) "Победа!" else "Поражение...",
                        color = with(colorScheme) {
                            if (state.victory) primary else error
                        },
                        textAlign = TextAlign.Center,
                        style = typography.headlineLarge,
                        autoSize = TextAutoSize.StepBased(maxFontSize = 72.sp),
                        maxLines = 1
                    )

                    Text(
                        modifier = Modifier,
                        text = when(state.reason){
                            DeathReason.HUNTER_FOUND_PLAYER -> TODO()
                            DeathReason.HP_ARE_OVER -> TODO()
                            null -> if (state.victory) "Вы отлично справились."
                            else "Время вышло, но не все были найдены."
                        },
                        style = typography.bodyLarge,
                        color = colorScheme.
                    )
                }
            }
        }

    }
}