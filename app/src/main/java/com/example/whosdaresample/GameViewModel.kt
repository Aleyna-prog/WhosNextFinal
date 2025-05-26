package com.example.whosdaresample

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.example.whosdaresample.data.GameStat
import com.example.whosdaresample.data.GameStatDao
import kotlinx.coroutines.launch

class GameViewModel(
    private val statDao: GameStatDao
) : ViewModel(), LifecycleObserver {

    val playerNames = mutableStateListOf<String>()
    val currentPlayer = mutableStateOf("")
    val selectedOption = mutableStateOf<String?>(null)
    val currentTask = mutableStateOf("")
    val jokerUsage = mutableStateMapOf<String, Boolean>()
    val isShuffleMode = mutableStateOf(false)

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
        val actualType = if (isShuffleMode.value) listOf("Truth", "Dare").random() else type
        val base = if (actualType == "Truth") truthTasks else dareTasks
        val custom = customTasks.filter { it.type == actualType }.map { it.text }
        val source = base + custom

        val used = if (actualType == "Truth") usedTruthTasks else usedDareTasks
        val available = source.filterNot { it in used }

        if (available.isEmpty()) used.clear()

        val newTask = (source - used).random()
        used.add(newTask)
        return newTask
    }

    fun recordChoice(type: String) {
        val player = currentPlayer.value
        gameStats.add("$player: $type")

        viewModelScope.launch {
            statDao.insert(GameStat(playerName = player, choice = type))
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        // Optional: save game state if needed
    }

    fun getStatsGroupedByPlayer(
        onResult: (Map<String, Pair<Int, Int>>) -> Unit
    ) {
        viewModelScope.launch {
            val stats = statDao.getAllStats()
            val grouped = stats.groupBy { it.playerName }
                .mapValues { entry ->
                    val truths = entry.value.count { it.choice == "Truth" }
                    val dares = entry.value.count { it.choice == "Dare" }
                    truths to dares
                }
            onResult(grouped)
        }
    }

    // ---- Custom Tasks ----
    val customTasks = mutableStateListOf<CustomTask>()

    fun addCustomTask(type: String, text: String) {
        if (text.isNotBlank()) {
            customTasks.add(CustomTask(type, text))
        }
    }

    fun removeCustomTask(task: CustomTask) {
        customTasks.remove(task)
    }
}
