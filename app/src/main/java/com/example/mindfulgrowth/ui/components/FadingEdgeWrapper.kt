package com.example.mindfulgrowth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors

@Composable
fun FadingEdgeWrapper(
    modifier: Modifier = Modifier,
    fadeHeight: Dp = 32.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        // Top Fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fadeHeight)
                .align(Alignment.TopCenter)
                .background(Brush.verticalGradient(colors = listOf(Color(SystemConfigColors.PRIMARY_BG), Color.Transparent)))
        )

        // Bottom Fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fadeHeight)
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color(SystemConfigColors.PRIMARY_BG))))
        )
    }
}