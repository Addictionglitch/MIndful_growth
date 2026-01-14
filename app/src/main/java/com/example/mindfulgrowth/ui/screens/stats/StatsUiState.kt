
package com.example.mindfulgrowth.ui.screens.stats

// This is the single source of truth for your UI State.
data class StatsData(
    val weeklyFocusData: List<Float> = emptyList(),
    val weeklyFocusLabels: List<String> = emptyList(),
    val dailyGoalProgress: Float = 0f,
    val dailyGoalTarget: String = "0h"
)

sealed interface StatsUiState {
    object Loading : StatsUiState
    data class Success(val data: StatsData) : StatsUiState
    data class Error(val message: String) : StatsUiState
}