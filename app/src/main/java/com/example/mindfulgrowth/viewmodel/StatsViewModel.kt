package com.example.mindfulgrowth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulgrowth.ui.screens.stats.StatsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

// Reusing existing enum
enum class StatsRange(val label: String) {
    DAY("Day"), WEEK("Week"), MONTH("Month")
}

class StatsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    // Internal state tracking
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
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            
            // SIMULATION: Replace this delay with actual Repository calls
            // e.g. statsRepository.getUsageStats(range)
            delay(1200) 

            // SIMULATION: Randomly throw error to demonstrate Error State
            if (Random.nextInt(10) == 0) {
                _uiState.value = StatsUiState.Error("Failed to load usage statistics.")
                return@launch
            }

            // Mock Data Generation based on Range
            val (data, time, trend, isPos) = when (range) {
                StatsRange.DAY -> Quad(
                    listOf(0.1f, 0.3f, 0.2f, 0.5f, 0.4f, 0.8f, 0.6f), "45m", "+5%", true
                )
                StatsRange.WEEK -> Quad(
                    listOf(0.2f, 0.4f, 0.35f, 0.7f, 0.5f, 0.9f, 0.75f), "18h 12m", "+12%", true
                )
                StatsRange.MONTH -> Quad(
                    listOf(0.5f, 0.6f, 0.4f, 0.8f, 0.7f, 0.9f, 0.85f), "64h", "-8%", false
                )
            }

            _uiState.value = StatsUiState.Success(
                selectedRange = range,
                totalFocusTime = time,
                focusTimeTrend = trend,
                isTrendPositive = isPos,
                pickupsCount = Random.nextInt(10, 60),
                treesGrown = Random.nextInt(1, 15),
                goalProgress = 0.75f,
                goalTarget = "60m",
                graphData = data
            )
        }
    }
}

// Helper tuple
private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
