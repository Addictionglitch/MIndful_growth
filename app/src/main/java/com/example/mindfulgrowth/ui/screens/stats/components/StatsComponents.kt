
package com.example.mindfulgrowth.ui.screens.stats.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.NeonCyan
import com.example.mindfulgrowth.ui.theme.NeonGreen

@Composable
fun StatsLineGraph(
    data: List<Float>,
    modifier: Modifier = Modifier,
    onScrub: (Float?) -> Unit
) {
    if (data.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val haptic = LocalHapticFeedback.current

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val newIndex = (offset.x / size.width * (data.size - 1)).toInt().coerceIn(0, data.size - 1)
                        if (selectedIndex != newIndex) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = newIndex
                            onScrub(data[newIndex])
                        }
                    },
                    onDragEnd = {
                        selectedIndex = null
                        onScrub(null)
                    },
                    onDrag = { change, _ ->
                        val newIndex = (change.position.x / size.width * (data.size - 1)).toInt().coerceIn(0, data.size - 1)
                        if (selectedIndex != newIndex) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedIndex = newIndex
                            onScrub(data[newIndex])
                        }
                        change.consume()
                    }
                )
            }
    ) {
        val path = Path()
        if (data.size > 1) {
            val stepX = size.width / (data.size - 1)
            val stepY = size.height / (data.maxOrNull() ?: 1f)

            path.moveTo(0f, size.height - data.first() * stepY)

            for (i in 0 until data.size - 1) {
                val currentX = i * stepX
                val currentY = size.height - data[i] * stepY
                val nextX = (i + 1) * stepX
                val nextY = size.height - data[i + 1] * stepY
                path.cubicTo(
                    currentX + stepX / 2f, currentY,
                    currentX + stepX / 2f, nextY,
                    nextX, nextY
                )
            }

            drawPath(
                path = path,
                brush = Brush.verticalGradient(listOf(NeonCyan, Color.Transparent)),
                style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(16.dp.toPx()))
            )

            selectedIndex?.let { index ->
                val x = index * stepX
                drawLine(
                    color = NeonGreen,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                drawCircle(
                    color = NeonGreen,
                    radius = 8.dp.toPx(),
                    center = Offset(x, size.height - data[index] * stepY)
                )
            }
        }
    }
}

@Composable
fun FocusHeroCard(
    title: String,
    value: Float,
    isScrubbing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassCard(modifier = modifier, shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(targetState = isScrubbing, label = "TitleValueTransition") { scrubbing ->
                Text(
                    text = if (scrubbing) String.format("%.1f", value) + "h" else title,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = NeonCyan)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, fontSize = 20.sp, fontFamily = FontFamily.Monospace, color = Color.White)
        }
    }
}
