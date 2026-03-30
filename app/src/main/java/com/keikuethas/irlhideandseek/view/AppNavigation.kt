package com.keikuethas.irlhideandseek.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(innerPadding: PaddingValues) {
    val navController = rememberAnimatedNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(innerPadding, navController)
        }

        composable<Lobby>{
            LobbyScreen(innerPadding, navController)
        }

        composable<Game> {
            GameScreen(innerPadding)
        }
    }
}

