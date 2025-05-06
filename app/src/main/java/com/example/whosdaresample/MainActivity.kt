package com.example.whosdaresample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.whosdaresample.ui.theme.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhosdaresampleTheme {
                val navController = rememberNavController()
                var playerNames by remember { mutableStateOf(listOf<String>()) }
                var currentPlayer by remember { mutableStateOf("") }

                NavHost(navController = navController, startDestination = "start") {
                    composable("start") {
                        StartScreen(
                            playerNames = playerNames,
                            onAddName = { name ->
                                if (name.isNotBlank() && !playerNames.contains(name)) {
                                    playerNames = playerNames + name
                                }
                            },
                            onRemoveName = { name ->
                                playerNames = playerNames - name
                            },
                            onStartGame = {
                                navController.navigate("spin")
                            }
                        )
                    }

                    composable("spin") {
                        SpinBottleScreen(
                            playerNames = playerNames,
                            onPlayerChosen = { chosen ->
                                currentPlayer = chosen
                                navController.navigate("game")
                            }
                        )
                    }

                    composable("game") {
                        GameScreen(
                            currentPlayer = currentPlayer,
                            onNextRound = {
                                navController.navigate("spin")
                            }
                        )
                    }
                }
            }
        }
    }
}
