package com.example.mindfulgrowth.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FocusDao {
    @Insert
    suspend fun insertSession(session: FocusSession)

    // 1. Get raw sessions for the graph (filtered by date range)
    @Query("SELECT * FROM focus_sessions WHERE startTime BETWEEN :start AND :end ORDER BY startTime ASC")
    suspend fun getSessionsBetween(start: Long, end: Long): List<FocusSession>

    // 2. Efficiently sum total duration for a range (returns null if no sessions)
    @Query("SELECT SUM(durationSeconds) FROM focus_sessions WHERE startTime BETWEEN :start AND :end")
    suspend fun getTotalFocusSeconds(start: Long, end: Long): Long?

    // 3. LIFETIME STATS: Total Trees (1 Session = 1 Tree)
    // We count ALL rows in the table
    @Query("SELECT COUNT(*) FROM focus_sessions")
    suspend fun getTotalSessionCount(): Int

    // 4. LIFETIME STATS: Total Focus Time (All time)
    @Query("SELECT SUM(durationSeconds) FROM focus_sessions")
    suspend fun getLifetimeFocusSeconds(): Long?
}