package com.example.mindfulgrowth.ui.screens.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import com.example.mindfulgrowth.viewmodel.StatsRange
import com.example.mindfulgrowth.viewmodel.StatsViewModel

private val TextSecondary @Composable get() = Color(SystemConfigColors.TEXT_SECONDARY)
private val PixelFont = FontFamily.Monospace

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 60.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- HEADER (Always Visible) ---
        item {
            StatsHeader(
                selectedRange = (state as? StatsUiState.Success)?.selectedRange ?: StatsRange.WEEK,
                onRangeSelected = viewModel::setRange
            )
        }

        // --- CONTENT AREA (Animated Switch) ---
        item {
            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "stats_content"
            ) { targetState ->
                when (targetState) {
                    is StatsUiState.Loading -> {
                        StatsLoadingSkeleton()
                    }
                    is StatsUiState.Error -> {
                        StatsErrorState(
                            message = targetState.message,
                            onRetry = viewModel::retry
                        )
                    }
                    is StatsUiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            // 1. Hero
                            FocusHeroCard(
                                timeSaved = targetState.totalFocusTime,
                                trend = targetState.focusTimeTrend,
                                isTrendPositive = targetState.isTrendPositive,
                                graphData = targetState.graphData
                            )

                            // 2. Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MetricCard(
                                    title = "UNLOCKS",
                                    value = "${targetState.pickupsCount}",
                                    icon = Icons.Rounded.Smartphone,
                                    contentDesc = "${targetState.pickupsCount} phone unlocks today",
                                    modifier = Modifier.weight(1f).height(140.dp)
                                )
                                MetricCard(
                                    title = "TREES",
                                    value = "${targetState.treesGrown}",
                                    icon = Icons.Rounded.Forest,
                                    contentDesc = "${targetState.treesGrown} trees grown in your forest",
                                    modifier = Modifier.weight(1f).height(140.dp)
                                )
                            }

                            // 3. Goal
                            GoalProgressCard(
                                progress = targetState.goalProgress,
                                target = targetState.goalTarget
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsHeader(
    selectedRange: StatsRange,
    onRangeSelected: (StatsRange) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "PERFORMANCE",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontFamily = PixelFont,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(16.dp))
        
        GlassSegmentedControl(
            options = StatsRange.values().toList(),
            selectedOption = selectedRange,
            onOptionSelected = onRangeSelected
        )
    }
}
