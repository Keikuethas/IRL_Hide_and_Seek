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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.model.DeathReason
import com.keikuethas.irlhideandseek.mvi.endscreen.EndEffect
import com.keikuethas.irlhideandseek.mvi.endscreen.EndIntent
import com.keikuethas.irlhideandseek.mvi.endscreen.EndState
import com.keikuethas.irlhideandseek.mvi.endscreen.EndViewModel
import com.keikuethas.irlhideandseek.view.topbar.TextTopAppBar


@Composable
fun EndScreen(
    navController: NavController = rememberNavController(),
    viewModel: EndViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = viewModel.effect

    EndContent(
        state = state.value,
    ) {viewModel.onIntent(it)}

    LaunchedEffect(Unit) {
        effect.collect { effect -> when(effect){
            EndEffect.Quit -> navController.navigate(Home) {popUpTo(Home) }
        } }
    }
}

@Preview(showBackground = true)
@Composable
fun EndContent(
    state: EndState = EndState(victory = true),
    onIntent: (EndIntent) -> Unit = {}
) {

    val textColor = with(colorScheme) {
        if (state.victory) primary else error
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TextTopAppBar("Конец игры") },
        containerColor = colorScheme.secondary
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = if (state.victory) "Победа!" else "Поражение...",
                        color = textColor,
                        textAlign = TextAlign.Center,
                        style = typography.headlineLarge,
                        autoSize = TextAutoSize.StepBased(maxFontSize = 72.sp),
                        maxLines = 1
                    )

                    Text(
                        modifier = Modifier,
                        text = buildAnnotatedString {
                            when (state.reason) {
                                DeathReason.HUNTER_FOUND_PLAYER -> {
                                    append("Вас нашли. ")

//                                    withStyle(SpanStyle(color = RoleType.SEEKER.color)) {
//                                        append("Охотник ${state.hunterName ?: "NULL"}.")
//                                    }
                                }

                                DeathReason.HP_ARE_OVER -> append("Ваше здоровье на нуле.")
                                null -> if (state.victory) append("Вы отлично справились.")
                                else append("Время вышло, но не все были найдены.")
                            }
                        },
                        style = typography.headlineSmall,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )

                    OutlinedButton(
                        onClick = { onIntent(EndIntent.Quit) }
                    ) {
                        Text(
                            text = "На главную"
                        )
                    }
                }
            }
        }

    }
}