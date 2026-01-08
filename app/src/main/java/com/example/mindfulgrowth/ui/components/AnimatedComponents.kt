package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import kotlin.random.Random

/**
 * Floating particles background effect
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 30,
    color: Color = Color(SystemConfigColors.ACCENT_RED_PRIMARY).copy(alpha = 0.1f)
) {
    val particles = remember {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 3f + 1f,
                speedX = (Random.nextFloat() - 0.5f) * 0.0005f,
                speedY = Random.nextFloat() * 0.0003f + 0.0001f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = ((particle.x + particle.speedX * time * 10000) % 1f) * size.width
            val y = ((particle.y + particle.speedY * time * 10000) % 1f) * size.height

            drawCircle(
                color = color,
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speedX: Float,
    val speedY: Float
)

/**
 * Shimmer loading effect for cards
 */
@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val offset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val shimmerX = size.width * offset
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(SystemConfigColors.GLASS_PRIMARY).copy(alpha = 0f),
                        Color(SystemConfigColors.GLASS_PRIMARY),
                        Color(SystemConfigColors.GLASS_PRIMARY).copy(alpha = 0f)
                    ),
                    startX = shimmerX - 200f,
                    endX = shimmerX + 200f
                )
            )
        }
    }
}

/**
 * Pulsing glow effect for active states
 */
@Composable
fun PulsingGlow(
    color: Color = Color(SystemConfigColors.ACCENT_RED_PRIMARY),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = color.copy(alpha = alpha),
            radius = size.minDimension / 2
        )
    }
}
