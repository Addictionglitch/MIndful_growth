package com.example.mindfulgrowth.ui.screens.goal

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GoalDetailsScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onNavigateBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        with(sharedTransitionScope) {
            GoalDetailHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        state = rememberSharedContentState(key = "goal-card"),
                        animatedVisibilityScope = animatedContentScope
                    )
            )
        }
        // ... rest of screen
    }
}

@Composable
fun GoalDetailHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(200.dp)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("Daily Goal Details", style = MaterialTheme.typography.headlineMedium, color = Color.White)
    }
}
