package com.keikuethas.irlhideandseek.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.keikuethas.irlhideandseek.view.game.GameScreen
import com.keikuethas.irlhideandseek.view.home.HomeScreen
import com.keikuethas.irlhideandseek.view.lobby.LobbyScreen
import com.keikuethas.irlhideandseek.view.newgame.NewGameScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.EventsSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.MapSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.RolesSettingsScreen
import com.keikuethas.irlhideandseek.view.newgame.settings_screens.TimeScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> { HomeScreen(navController) }

        composable<Lobby> { LobbyScreen(navController) }

        composable<Game> { GameScreen(navController) }

        composable<NewGame> { backStackEntry ->
            val newGame = backStackEntry.toRoute<NewGame>()
            NewGameScreen(navController, newGame.playerName)
        }

        composable<RolesSettings> { RolesSettingsScreen(navController) }

        composable<MapSettings> { MapSettingsScreen(navController) }

        composable<EventSettings> { EventsSettingsScreen(navController) }

        composable<TimeSettings> { TimeScreen(navController) }

        composable<EndScreen> { EndScreen(navController) }

    }
}

