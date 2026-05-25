package com.keikuethas.irlhideandseek.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keikuethas.irlhideandseek.view.game.GameScreen
import com.keikuethas.irlhideandseek.view.home.HomeScreen
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen
import com.keikuethas.irlhideandseek.view.newgame.NewGameNavigation
import com.keikuethas.irlhideandseek.view.newgame.NewGameScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.EventsSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.MapSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.RolesSettingsScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(navController)
        }

        composable<Lobby>{
            LobbyScreen(navController)
        }

        composable<Game> {
            GameScreen(navController)
        }

        composable<NewGameNavigation> {
            NewGameNavigation(navController)
        }

        composable<NewGame> {
            NewGameScreen(navController)
        }

        composable<RolesSettings> {
            RolesSettingsScreen(navController)
        }

        composable<MapSettings> {
            MapSettingsScreen(navController)
        }

        composable<EventSettings> {
            EventsSettingsScreen(navController)
        }
    }
}

