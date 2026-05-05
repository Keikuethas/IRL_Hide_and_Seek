package com.keikuethas.irlhideandseek.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.keikuethas.irlhideandseek.view.game.GameScreen
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen
import com.keikuethas.irlhideandseek.view.settings_screens.AbilitiesSettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.LobbySettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.MapSettingsScreen
import com.keikuethas.irlhideandseek.view.settings_screens.RulesSettingsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(navController)
        }

        composable<Lobby>{
            val playerName = it.toRoute<Lobby>().playerName
            val roomName = it.toRoute<Lobby>().roomName
            LobbyScreen(navController, playerName, roomName)
        }

        //---
//        composable<RolesSettings> {
//            RolesSettingsScreen(it.toRoute<RolesSettings>().gameSettings)
//        } //TEMP

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
            //val role = backStackEntry.toRoute<Game>().role
            GameScreen(navController)
        }
    }
}

