package com.example.mindfulgrowth.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulgrowth.data.AppDatabase
import com.example.mindfulgrowth.data.FocusSession
import com.example.mindfulgrowth.ui.screens.stats.StatsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class StatsRange(val label: String) {
    DAY("Day"), WEEK("Week"), MONTH("Month")
}

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Initialize Database
    private val db = androidx.room.Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "mindful-db"
    ).build()
    private val dao = db.focusDao()

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private var currentRange = StatsRange.WEEK

    init {
        loadStats(StatsRange.WEEK)
    }

    fun setRange(range: StatsRange) {
        currentRange = range
        loadStats(range)
    }

    fun retry() {
        loadStats(currentRange)
    }

    private fun loadStats(range: StatsRange) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = StatsUiState.Loading

            try {
                // A. Calculate Date Range (Start/End Timestamps)
                val now = LocalDateTime.now()
                val (start, end) = getRangeTimestamps(range, now)

                // B. Fetch Data from Room
                val sessionsInRange = dao.getSessionsBetween(start, end)
                val totalSecondsInRange = dao.getTotalFocusSeconds(start, end) ?: 0L
                
                // C. Fetch Lifetime Stats (Trees/Forests logic)
                val lifetimeCount = dao.getTotalSessionCount()
                
                // D. Process Data
                val formattedTime = formatDuration(totalSecondsInRange)
                
                // Forest Logic: 100 Trees = 1 Forest
                val forestsGrown = lifetimeCount / 100
                // Progress to next forest (e.g., 45 trees -> 0.45 progress)
                val forestProgress = (lifetimeCount % 100) / 100f
                val treesToNextForest = 100 - (lifetimeCount % 100)

                // E. Generate Graph Data
                val graphPoints = generateGraphData(range, sessionsInRange, start, end)

                _uiState.value = StatsUiState.Success(
                    selectedRange = range,
                    totalFocusTime = formattedTime,
                    focusTimeTrend = "N/A", // Requires comparing to previous range (omitted for brevity)
                    isTrendPositive = true,
                    pickupsCount = 0, // Requires UsageStatsManager (separate permission)
                    treesGrown = lifetimeCount,
                    goalProgress = forestProgress, // Reusing goal bar for Forest Progress
                    goalTarget = "$treesToNextForest trees to next Forest",
                    graphData = graphPoints
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = StatsUiState.Error("Database Error: ${e.localizedMessage}")
            }
        }
    }

    // --- HELPER: Date Ranges ---
    private fun getRangeTimestamps(range: StatsRange, now: LocalDateTime): Pair<Long, Long> {
        val zoneId = ZoneId.systemDefault()
        val end = now.atZone(zoneId).toInstant().toEpochMilli()

        val start = when (range) {
            StatsRange.DAY -> now.truncatedTo(ChronoUnit.DAYS).atZone(zoneId).toInstant().toEpochMilli()
            StatsRange.WEEK -> now.minusDays(6).truncatedTo(ChronoUnit.DAYS).atZone(zoneId).toInstant().toEpochMilli()
            StatsRange.MONTH -> now.minusDays(30).truncatedTo(ChronoUnit.DAYS).atZone(zoneId).toInstant().toEpochMilli()
        }
        return Pair(start, end)
    }

    // --- HELPER: Graph Bucketing ---
    private fun generateGraphData(
        range: StatsRange, 
        sessions: List<FocusSession>, 
        start: Long, 
        end: Long
    ): List<Float> {
        if (sessions.isEmpty()) return listOf(0f, 0f, 0f, 0f, 0f)

        // Bucket Logic:
        // If Week: 7 buckets (one per day)
        // If Day: 24 buckets (one per hour)
        val bucketCount = if (range == StatsRange.WEEK) 7 else if (range == StatsRange.DAY) 24 else 30
        val interval = (end - start) / bucketCount
        
        val buckets = FloatArray(bucketCount) { 0f }
        var maxVal = 0f

        sessions.forEach { session ->
            // Find which bucket this session belongs to
            val offset = session.startTime - start
            val index = (offset / interval).toInt().coerceIn(0, bucketCount - 1)
            
            buckets[index] += session.durationSeconds.toFloat()
            if (buckets[index] > maxVal) maxVal = buckets[index]
        }

        // Normalize to 0.0 - 1.0 for the UI Graph
        return if (maxVal == 0f) buckets.toList() else buckets.map { it / maxVal }
    }

    // --- HELPER: Time Formatting ---
    private fun formatDuration(seconds: Long): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        return if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
    }
}
