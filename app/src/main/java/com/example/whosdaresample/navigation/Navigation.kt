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
import com.example.whosdaresample.ui.theme.CustomTaskScreen


@Composable
fun AppNavigation(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(
                viewModel = viewModel,
                onStartGame = {
                    viewModel.clearGameStats()
                    navController.navigate("spin")
                },
                onOpenCustomTasks = { navController.navigate("custom") }
            )
        }
        composable("spin") {
            SpinBottleScreen(
                viewModel = viewModel,
                onPlayerChosen = {
                    viewModel.resetRound()
                    viewModel.roundStarted.value = true
                    navController.navigate("game")
                }
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
        composable("custom") {
            CustomTaskScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

    }
}
