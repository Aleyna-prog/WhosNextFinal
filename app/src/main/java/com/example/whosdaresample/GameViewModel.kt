package com.example.whosdaresample


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*

class GameViewModel : ViewModel(), LifecycleObserver {
    val playerNames = mutableStateListOf<String>()
    val currentPlayer = mutableStateOf("")
    val selectedOption = mutableStateOf<String?>(null)
    val currentTask = mutableStateOf("")
    val jokerUsage = mutableStateMapOf<String, Boolean>()

    fun hasUsedJoker(name: String): Boolean = jokerUsage[name] == true

    fun useJoker(name: String) {
        jokerUsage[name] = true
    }


    val gameStats = mutableStateListOf<String>()

    private val usedTruthTasks = mutableListOf<String>()
    private val usedDareTasks = mutableListOf<String>()

    val truthTasks = mutableListOf(
        "What is your biggest fear?",
        "Have you ever cheated?",
        "What's a secret you never told anyone?"
    )

    val dareTasks = mutableListOf(
        "Do a silly dance!",
        "Speak like a robot for 1 minute!",
        "Sing the chorus of your favorite song!"
    )

    fun addPlayer(name: String) {
        if (name.isNotBlank() && !playerNames.contains(name)) {
            playerNames.add(name)
        }
    }

    fun removePlayer(name: String) {
        playerNames.remove(name)
    }

    fun pickRandomPlayer() {
        currentPlayer.value = playerNames.random()
    }

    fun resetRound() {
        selectedOption.value = null
        currentTask.value = ""
    }

    fun getTask(type: String): String {
        val source = if (type == "Truth") truthTasks else dareTasks
        val used = if (type == "Truth") usedTruthTasks else usedDareTasks

        val available = source.filterNot { it in used }
        if (available.isEmpty()) used.clear()

        val newTask = (source - used).random()
        used.add(newTask)
        return newTask
    }

    fun recordChoice(type: String) {
        gameStats.add("${currentPlayer.value}: $type")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // Optional: save game state if needed
    }
}