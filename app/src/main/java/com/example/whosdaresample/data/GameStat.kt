package com.example.whosdaresample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_stats")
data class GameStat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val choice: String, // "Truth" or "Dare"
    val timestamp: Long = System.currentTimeMillis()
)
