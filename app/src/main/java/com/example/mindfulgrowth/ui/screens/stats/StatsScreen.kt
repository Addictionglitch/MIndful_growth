
package com.example.mindfulgrowth.ui.screens.stats

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.screens.stats.components.FocusHeroCard
import com.example.mindfulgrowth.ui.screens.stats.components.MetricCard
import com.example.mindfulgrowth.ui.screens.stats.components.StatsLineGraph

// Refactored to remove NavHost-specific parameters
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is StatsUiState.Loading -> CircularProgressIndicator()
            is StatsUiState.Success -> StatsScreenContent(statsData = state.data)
            is StatsUiState.Error -> {
                // Stylized error component can be placed here
            }
        }
    }
}

@Composable
fun StatsScreenContent(statsData: StatsData) {
    var scrubbingValue by remember { mutableStateOf<Float?>(null) }
    val isScrubbing = scrubbingValue != null

    val animatedTotalFocus by animateFloatAsState(
        targetValue = statsData.weeklyFocusData.sum(),
        animationSpec = tween(1000),
        label = "TotalFocusAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
                    slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(durationMillis = 500))
        ) {
            FocusHeroCard(
                title = "Total Focus (7d)",
                value = scrubbingValue ?: animatedTotalFocus,
                isScrubbing = isScrubbing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                StatsLineGraph(
                    data = statsData.weeklyFocusData,
                    onScrub = { scrubbingValue = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(delayMillis = 300, durationMillis = 500)) +
                    slideInVertically(initialOffsetY = { it }, animationSpec = tween(delayMillis = 300, durationMillis = 500))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    title = "Trees Grown",
                    value = "12",
                    icon = Icons.Default.Yard,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
