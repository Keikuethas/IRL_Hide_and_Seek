package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Scaffold
import com.keikuethas.irlhideandseek.mvi.endscreen.EndIntent
import com.keikuethas.irlhideandseek.mvi.endscreen.EndState
import com.keikuethas.irlhideandseek.mvi.endscreen.EndViewModel

//TODO экран конца игры (победа/поражение)

@Composable
fun EndScreen(
    navController: NavController = rememberNavController(),
    viewMode: EndViewModel = hiltViewModel()
) {

}

@Preview
@Composable
fun EndContent(
    state: EndState = EndState(),
    onIntent: (EndIntent) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth(3 / 4F)
                    .fillMaxHeight(1 / 3F)
                    .align(Alignment.Center),
                colors = CardDefaults.cardColors(
                    containerColor = with(colorScheme) {
                        if (state.victory) primaryContainer
                        else errorContainer
                    }
                )
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (state.victory) "Победа!" else "Поражение...",
                    color = with(colorScheme) {
                        if (state.victory) primary else error
                    },
                    textAlign = TextAlign.Center,
                    style = typography.headlineLarge,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 72.sp),
                    maxLines = 1
                )
            }
        }

    }
}