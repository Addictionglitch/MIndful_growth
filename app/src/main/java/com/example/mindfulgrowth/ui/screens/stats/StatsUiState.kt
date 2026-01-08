package com.example.mindfulgrowth.ui.screens.stats

import com.example.mindfulgrowth.viewmodel.StatsRange

sealed interface StatsUiState {
    data object Loading : StatsUiState
    
    data class Error(
        val message: String,
        val isPermissionError: Boolean = false
    ) : StatsUiState

    data class Success(
        val selectedRange: StatsRange,
        // Formatted strings for display
        val totalFocusTime: String, 
        val focusTimeTrend: String, // e.g., "+12%" or "-5%"
        val isTrendPositive: Boolean,
        val pickupsCount: Int,
        val treesGrown: Int,
        val goalProgress: Float, // 0.0f to 1.0f
        val goalTarget: String, // e.g. "60m"
        val graphData: List<Float>
    ) : StatsUiState
}
