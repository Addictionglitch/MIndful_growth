package com.example.mindfulgrowth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.TranslucentWhite

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape,
    borderWidth: Dp = 1.dp,
    borderColor: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.1f),
        )
    ),
    backgroundColor: Color = TranslucentWhite,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = borderWidth,
                brush = borderColor,
                shape = shape
            ),
    ) {
        content()
    }
}