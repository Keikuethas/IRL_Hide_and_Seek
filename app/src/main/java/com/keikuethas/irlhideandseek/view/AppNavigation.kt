package com.keikuethas.irlhideandseek.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.keikuethas.irlhideandseek.view.game.GameScreen
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen
import com.keikuethas.irlhideandseek.view.settings_screens.AbilitiesSettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.LobbySettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.MapSettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.RolesSettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.RulesSettingsScreen

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

        //---
        composable<RolesSettings> {
            RolesSettingsScreen()
        }

        composable<RulesSettings>  {
            RulesSettingsScreen()
        }

        composable<MapSettings> {
            MapSettingsScreen()
        }

        composable<AbilitiesSettings> {
            AbilitiesSettingsScreen()
        }

        composable<LobbySettings> {
            LobbySettingsScreen()
        }
        //---

        composable<Game> { backStackEntry ->
            val role = backStackEntry.toRoute<Game>().role
            GameScreen(innerPadding, navController)
        }
    }
}

