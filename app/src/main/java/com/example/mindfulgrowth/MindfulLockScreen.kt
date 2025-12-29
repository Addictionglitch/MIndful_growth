package com.example.mindfulgrowth
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// --- MAIN ENTRY POINT ---
@Composable
fun MindfulLockScreen(
    // Logic passes data down here.
    // If logic isn't ready, pass defaults:
    timerProgress: Float = 0.65f, // 0.0 to 1.0
    currentTime: LocalDateTime = LocalDateTime.now()
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

    // Deep Focus Theme Colors
    val deepBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F2027), // Deep Blue/Black
            Color(0xFF203A43),
            Color(0xFF2C5364)  // Slate
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepBg)
    ) {
        // 1. Ambient Background Animation (Breathing Orbs)
        AmbientBackground()

        // 2. Main Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP: Status / Weather Placeholder ---
            FrostedGlassCard(
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "🌱 Focus Mode Active",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // --- CENTER: The Timer & Clock ---
            Box(contentAlignment = Alignment.Center) {
                // The high-performance GPU-only animation
                MindfulTimerRing(progress = timerProgress)

                // The Clock Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatter.format(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 86.sp,
                            fontWeight = FontWeight.Thin,
                            fontFamily = FontFamily.Monospace, // VITAL: Prevents jitter
                            letterSpacing = (-2).sp
                        ),
                        color = Color.White,
                        // This prevents numbers from wiggling when they change width (1 vs 0)
                        modifier = Modifier.graphicsLayer { alpha = 0.95f }
                    )
                    Text(
                        text = dateFormatter.format(currentTime).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                }
            }

            // --- BOTTOM: Metrics Placeholders ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(label = "Streak", value = "12")
                MetricItem(label = "Focus", value = "45m")
                MetricItem(label = "Tree", value = "Lvl 4")
            }
        }
    }
}

// --- COMPONENT: High Performance Timer Ring ---
@Composable
fun MindfulTimerRing(progress: Float) {
    // Continuous slow rotation for the "futuristic" feel
    val infiniteTransition = rememberInfiniteTransition(label = "ring_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "rotation"
    )

    // Pulse effect
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.98f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(320.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()

            // 1. Background Track (dim)
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                style = Stroke(width = strokeWidth)
            )

            // 2. The Progress Arc (Neon Cyan)
            // Using rotate allows the gradient to spin, looking "alive"
            rotate(degrees = rotation) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF00C6FF).copy(alpha = 0.1f), // Fade tail
                            Color(0xFF00C6FF),                    // Bright body
                            Color(0xFFE0FFFF)                     // White-hot tip
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
    }
}

// --- COMPONENT: Real Frosted Glass (Android 12+) ---
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.06f)) // subtle tint
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.06f)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
    ) {
        // Apply a very small native blur only to the background layer to preserve content sharpness
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(modifier = Modifier.matchParentSize().graphicsLayer {
                val blur = RenderEffect.createBlurEffect(4f, 4f, Shader.TileMode.CLAMP)
                renderEffect = blur.asComposeRenderEffect()
            })
        }
        content()
    }
}

// --- COMPONENT: Ambient Background Orbs ---
@Composable
fun AmbientBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient")
    val yShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(5000, easing = SineBounceEasing), RepeatMode.Reverse),
        label = "orb_float"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Top Left Orb (Cyan)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF00C6FF).copy(alpha = 0.2f), Color.Transparent),
                center = Offset(0f, 0f),
                radius = 600f
            ),
            center = Offset(100f, 200f + yShift),
            radius = 400f
        )
        // Bottom Right Orb (Magenta/Purple)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF9D50BB).copy(alpha = 0.15f), Color.Transparent),
                center = Offset(size.width, size.height),
                radius = 700f
            ),
            center = Offset(size.width - 100f, size.height - 100f - yShift),
            radius = 500f
        )
    }
}

// --- COMPONENT: Simple Metric Item ---
@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

// Helper easing for organic movement
val SineBounceEasing = Easing { fraction ->
    Math.sin(fraction * Math.PI).toFloat()
}
