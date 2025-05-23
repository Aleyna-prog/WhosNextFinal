// Datei: MainActivity.kt
package com.example.whosdaresample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whosdaresample.navigation.AppNavigation
import com.example.whosdaresample.ui.screens.*
import com.example.whosdaresample.ui.theme.WhosdaresampleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)

        setContent {
            WhosdaresampleTheme {
                val navController = rememberNavController()

                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}

