package com.keikuethas.irlhideandseek.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Scaffold
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
    state: EndState = EndState()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}