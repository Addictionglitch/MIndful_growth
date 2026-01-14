
package com.example.mindfulgrowth.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// @HiltViewModel if you are using Hilt
// class StatsViewModel @Inject constructor(...) : ViewModel() {
class StatsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadStats(StatsRange.WEEK) // Load initial data
    }

    fun onRetry() {
        // You might want to store the last selected range to retry correctly
        loadStats(StatsRange.WEEK)
    }

    // Function to be called from the UI to change the date range
    fun onRangeSelected(range: StatsRange) {
        loadStats(range)
    }

    private fun loadStats(range: StatsRange) {
        viewModelScope.launch {
            _uiState.value = StatsUiState.Loading
            try {
                // Simulate a network/database delay
                delay(1000)

                // MOCK DATA LOGIC - This is where you would fetch real data based on the 'range'
                val mockData = when (range) {
                    StatsRange.WEEK -> StatsData(
                        weeklyFocusData = listOf(0.1f, 0.2f, 0.9f, 0.5f, 0.6f, 0.2f, 0.3f),
                        weeklyFocusLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
                        dailyGoalProgress = 0.6f,
                        dailyGoalTarget = "4h"
                    )
                    StatsRange.MONTH -> StatsData(
                        weeklyFocusData = listOf(0.8f, 0.7f, 0.6f, 0.5f),
                        weeklyFocusLabels = listOf("W1", "W2", "W3", "W4"),
                        dailyGoalProgress = 0.85f,
                        dailyGoalTarget = "90h"
                    )
                    StatsRange.YEAR -> StatsData(
                        weeklyFocusData = listOf(0.4f, 0.5f, 0.3f, 0.6f, 0.8f, 0.7f),
                        weeklyFocusLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun"),
                        dailyGoalProgress = 0.75f,
                        dailyGoalTarget = "1000h"
                    )
                }

                _uiState.value = StatsUiState.Success(mockData)

            } catch (e: Exception) {
                _uiState.value = StatsUiState.Error("Could not load stats. Please try again.")
            }
        }
    }
}