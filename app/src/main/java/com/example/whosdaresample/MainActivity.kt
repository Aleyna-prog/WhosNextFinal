package com.example.whosdaresample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.whosdaresample.data.AppDatabase
import com.example.whosdaresample.navigation.AppNavigation
import com.example.whosdaresample.ui.theme.WhosdaresampleTheme
import android.app.Application


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Room DB & DAO laden
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.gameStatDao()
        val factory = GameViewModelFactory(dao)

        // ViewModel mit Factory erzeugen
        viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]
        lifecycle.addObserver(viewModel)

        // Tasks aus JSON laden
        viewModel.loadTasksFromJson(application)

        // UI starten
        setContent {
            val navController = rememberNavController()
            WhosdaresampleTheme(darkTheme = !viewModel.isLightTheme.value) {
                AppNavigation(navController = navController, viewModel = viewModel)
            }
        }
    }
}
