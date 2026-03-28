package com.keikuethas.irlhideandseek.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(innerPadding: PaddingValues) {
    val navController = rememberAnimatedNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(innerPadding)
        }

        composable<Lobby>{
            LobbyScreen(innerPadding)
        }

        composable<Game> {
            GameScreen(innerPadding)
        }
    }
}

