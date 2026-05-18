package com.keikuethas.irlhideandseek.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.view.game.GameScreen
import com.keikuethas.irlhideandseek.view.home.HomeScreen
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.MapSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.RolesSettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(navController)
        }

        composable<Lobby>{
            LobbyScreen(navController)
        }

        //---
        composable<RolesSettings> {
            RolesSettingsScreen()
        }


        composable<MapSettings> {
            MapSettingsScreen()
        }

        //---

        composable<Game> { backStackEntry ->
            //val role = backStackEntry.toRoute<Game>().role
            GameScreen(navController)
        }
    }
}

