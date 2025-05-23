package com.example.whosdaresample.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.whosdaresample.GameViewModel
import com.example.whosdaresample.ui.screens.*
import com.example.whosdaresample.ui.theme.EndScreen
import com.example.whosdaresample.ui.theme.GameScreen
import com.example.whosdaresample.ui.theme.SpinBottleScreen

@Composable
fun AppNavigation(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(
                viewModel = viewModel,
                onStartGame = { navController.navigate("spin") }
            )
        }
        composable("spin") {
            SpinBottleScreen(
                viewModel = viewModel,
                onPlayerChosen = { navController.navigate("game") }
            )
        }
        composable("game") {
            GameScreen(
                viewModel = viewModel,
                onNextRound = { navController.navigate("spin") },
                onEndGame = { navController.navigate("end") }
            )
        }
        composable("end") {
            EndScreen(
                viewModel = viewModel,
                onRestart = {
                    viewModel.playerNames.clear()
                    viewModel.gameStats.clear()
                    navController.navigate("start")
                }
            )
        }
    }
}
