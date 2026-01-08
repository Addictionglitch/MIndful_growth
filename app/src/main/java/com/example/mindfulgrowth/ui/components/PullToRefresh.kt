package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import kotlin.math.pow

@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var dragOffset by remember { mutableStateOf(0f) }
    var isReleased by remember { mutableStateOf(false) }

    val threshold = 150f
    val maxDrag = 300f

    // Animate drag offset back to 0
    val animatedOffset by animateFloatAsState(
        targetValue = if (isRefreshing || isReleased) 0f else dragOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pullOffset"
    )

    // Rotation animation while refreshing
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "refreshRotation"
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragOffset >= threshold && !isRefreshing) {
                            isReleased = true
                            onRefresh()
                        }
                        dragOffset = 0f
                        isReleased = false
                    },
                    onDragCancel = {
                        dragOffset = 0f
                        isReleased = false
                    },
                    onVerticalDrag = { _, dragAmount ->
                        if (!isRefreshing) {
                            // Apply resistance as user drags further
                            val newOffset = (dragOffset + dragAmount).coerceIn(0f, maxDrag)
                            val resistance = 1f - (newOffset / maxDrag).pow(1.5f)
                            dragOffset = newOffset * resistance
                        }
                    }
                )
            }
    ) {
        // Refresh indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((animatedOffset / 2).dp)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = (animatedOffset / threshold).coerceIn(0f, 1f)
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .rotate(if (isRefreshing) rotation else animatedOffset)
            ) {
                PulsingGlow(
                    color = Color(SystemConfigColors.ACCENT_RED_PRIMARY),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Content with offset
        Box(
            modifier = Modifier.graphicsLayer {
                translationY = animatedOffset
            }
        ) {
            content()
        }
    }
}
