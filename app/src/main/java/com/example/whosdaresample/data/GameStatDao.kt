package com.example.whosdaresample.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameStatDao {

    @Insert
    suspend fun insert(stat: GameStat)

    @Query("SELECT * FROM game_stats")
    suspend fun getAllStats(): List<GameStat>

    @Query("DELETE FROM game_stats")
    suspend fun clearAll()
}
