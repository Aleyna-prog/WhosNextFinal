package com.example.whosdaresample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whosdaresample.data.GameStatDao

class GameViewModelFactory(
    private val statDao: GameStatDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(statDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
