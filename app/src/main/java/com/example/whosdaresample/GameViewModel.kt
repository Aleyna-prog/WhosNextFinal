package com.example.whosdaresample

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.whosdaresample.data.GameStat
import com.example.whosdaresample.data.GameStatDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader

class GameViewModel(
    private val statDao: GameStatDao
) : ViewModel(), LifecycleObserver {

    val playerNames = mutableStateListOf<String>()
    val currentPlayer = mutableStateOf("")
    val selectedOption = mutableStateOf<String?>(null)
    val currentTask = mutableStateOf("")
    val jokerUsage = mutableStateMapOf<String, Boolean>()
    val isShuffleMode = mutableStateOf(false)
    val isLightTheme = mutableStateOf(false)
    val playerEmojis = mutableStateMapOf<String, String>()

    private val defaultAvatars = listOf("😎", "🐱", "👽", "🤖", "🦊", "🐸", "🧙", "👸", "🦁", "🐼")

    private val usedTruthTasks = mutableListOf<String>()
    private val usedDareTasks = mutableListOf<String>()

    val gameStats = mutableStateListOf<String>()
    val customTasks = mutableStateListOf<CustomTask>()
    val truthTasks = mutableStateListOf(
        "What is your biggest fear?",
        "Have you ever cheated?",
        "What's a secret you never told anyone?"
    )
    val dareTasks = mutableStateListOf(
        "Do a silly dance!",
        "Speak like a robot for 1 minute!",
        "Sing the chorus of your favorite song!"
    )

    fun hasUsedJoker(name: String): Boolean = jokerUsage[name] == true

    fun useJoker(name: String) {
        jokerUsage[name] = true
    }

    fun addPlayer(name: String, emoji: String? = null) {
        if (name.isNotBlank() && !playerNames.contains(name)) {
            playerNames.add(name)
            val avatar = emoji?.takeIf { it.isNotBlank() } ?: defaultAvatars.shuffled().firstOrNull() ?: "🙂"
            playerEmojis[name] = avatar
        }
    }

    fun removePlayer(name: String) {
        playerNames.remove(name)
        playerEmojis.remove(name)
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

    fun getStatsGroupedByPlayer(onResult: (Map<String, Pair<Int, Int>>) -> Unit) {
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

    fun addCustomTask(type: String, text: String) {
        if (text.isNotBlank()) {
            customTasks.add(CustomTask(type, text))
        }
    }

    fun removeCustomTask(task: CustomTask) {
        customTasks.remove(task)
    }

    fun loadTasksFromJson(application: Application) {
        viewModelScope.launch {
            val jsonString = withContext(Dispatchers.IO) {
                application.assets.open("truth-n-dare.json").bufferedReader().use(BufferedReader::readText)
            }

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val type = item.getString("type")
                val summary = item.getString("summary")
                if (type == "Truth") {
                    truthTasks.add(summary)
                } else if (type == "Dare") {
                    dareTasks.add(summary)
                }
            }
        }
    }

    fun getEmojiForPlayer(name: String): String? {
        return playerEmojis[name]
    }
}