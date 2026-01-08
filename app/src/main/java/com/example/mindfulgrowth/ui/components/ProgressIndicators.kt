package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TreeGrowingLoader(
    modifier: Modifier = Modifier,
    loaderSize: Dp = 64.dp,
    color: Color = Color(SystemConfigColors.NEON_GREEN_ACCENT)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "treeGrowing")

    val growthProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "growth"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(loaderSize)) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        rotate(rotation) {
            // Draw tree branches
            for (i in 0..5) {
                val angle = (i * 60f).toRadians()
                val progress = ((growthProgress * 2 - i * 0.2f).coerceIn(0f, 1f))
                val length = centerY * 0.8f * progress

                drawLine(
                    color = color,
                    start = Offset(centerX, centerY),
                    end = Offset(
                        centerX + (cos(angle.toDouble()) * length).toFloat(),
                        centerY + (sin(angle.toDouble()) * length).toFloat()
                    ),
                    strokeWidth = 4.dp.toPx() * (1f - i * 0.1f),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun CircularGlowLoader(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = Color(SystemConfigColors.ACCENT_RED_PRIMARY)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "circularGlow")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.size(size)) {
        // Background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = size.toPx() / 2
        )

        // Rotating arc
        drawArc(
            color = color.copy(alpha = pulse),
            startAngle = rotation,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun Float.toRadians() = (this * Math.PI / 180f).toFloat()
