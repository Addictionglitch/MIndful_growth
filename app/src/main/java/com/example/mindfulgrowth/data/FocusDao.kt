package com.example.mindfulgrowth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FocusDao {
    @Insert
    suspend fun insertSession(session: FocusSession)

    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    suspend fun getAllSessions(): List<FocusSession>
}