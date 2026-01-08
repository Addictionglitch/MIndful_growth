package com.example.mindfulgrowth.ui.screens.stats

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import com.example.mindfulgrowth.viewmodel.StatsRange

// --- THEME ALIASES ---
private val TextPrimary @Composable get() = Color(SystemConfigColors.TEXT_PRIMARY)
private val TextSecondary @Composable get() = Color(SystemConfigColors.TEXT_SECONDARY)
private val AccentColor @Composable get() = Color(SystemConfigColors.ACCENT_RED_PRIMARY)
private val NeonAccent @Composable get() = Color(SystemConfigColors.NEON_GREEN_ACCENT)
private val PixelFont = FontFamily.Monospace

// --- HERO CARD ---
@Composable
fun FocusHeroCard(
    timeSaved: String,
    trend: String,
    isTrendPositive: Boolean,
    graphData: List<Float>,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    }
                )
            }
            .semantics(mergeDescendants = true) {
                contentDescription = "Focus time today: $timeSaved. Trend: ${if(isTrendPositive) "Up" else "Down"} $trend compared to last period."
            },
        bloomIntensity = 0.1f
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "FOCUS TIME",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontFamily = PixelFont
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = timeSaved,
                        style = MaterialTheme.typography.displaySmall,
                        color = TextPrimary,
                        fontFamily = PixelFont,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Trend Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isTrendPositive) NeonAccent.copy(alpha = 0.1f)
                            else Color.Red.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = trend,
                        color = if (isTrendPositive) NeonAccent else Color.Red,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = PixelFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Graph
            Box(Modifier.fillMaxSize()) {
                StatsLineGraph(
                    data = graphData,
                    lineColor = AccentColor,
                    fillColor = AccentColor.copy(alpha = 0.1f)
                )
            }
        }
    }
}

// --- METRIC CARD ---
@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    contentDesc: String,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    GlassCard(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    }
                )
            }
            .semantics(mergeDescendants = true) {
                contentDescription = contentDesc
            },
        bloomIntensity = 0.15f
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Handled by parent semantics
                tint = NeonAccent,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontFamily = PixelFont
                )
            }
        }
    }
}

// --- GOAL PROGRESS ---
@Composable
fun GoalProgressCard(
    progress: Float,
    target: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f)
                stateDescription = "Daily goal is ${(progress * 100).toInt()} percent complete. Target is $target."
            }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("DAILY GOAL", style = MaterialTheme.typography.labelMedium, color = TextSecondary, fontFamily = PixelFont)
                Text(
                    text = "${(progress * 100).toInt()}% / $target",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentColor,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(AccentColor, NeonAccent)
                            )
                        )
                )
            }
        }
    }
}

// --- LOADING SKELETON ---
@Composable
fun StatsLoadingSkeleton() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse), label = "alpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        // Hero Skeleton
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f * alpha))
        )
        // Grid Skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.weight(1f).height(140.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.05f * alpha)))
            Box(Modifier.weight(1f).height(140.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.05f * alpha)))
        }
    }
}

// --- ERROR STATE ---
@Composable
fun StatsErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.Warning, null, tint = Color.Red, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            text = "SYNC ERROR",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            fontFamily = PixelFont
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            fontFamily = PixelFont,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
        ) {
            Text("RETRY CONNECTION", color = Color.Black, fontFamily = PixelFont, fontWeight = FontWeight.Bold)
        }
    }
}

// --- MISSING COMPONENTS (Graph & Segmented Control) ---

@Composable
fun GlassSegmentedControl(
    options: List<StatsRange>,
    selectedOption: StatsRange,
    onOptionSelected: (StatsRange) -> Unit
) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(SystemConfigColors.GLASS_SECONDARY))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            val bgColor by animateColorAsState(
                if (isSelected) Color(SystemConfigColors.ACCENT_RED_PRIMARY).copy(alpha = 0.2f)
                else Color.Transparent, label = "bg"
            )
            val textColor by animateColorAsState(
                if (isSelected) Color(SystemConfigColors.TEXT_PRIMARY)
                else Color(SystemConfigColors.TEXT_SECONDARY), label = "text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.label,
                    color = textColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun StatsLineGraph(
    data: List<Float>,
    lineColor: Color,
    fillColor: Color
) {
    if (data.isEmpty()) return

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp) // Leave room for top curve
    ) {
        val width = size.width
        val height = size.height
        val spacing = width / (data.size - 1)

        // Calculate points (Invert Y because canvas origin is top-left)
        val points = data.mapIndexed { index, value ->
            val x = index * spacing
            val y = height - (value * height)
            Offset(x, y)
        }

        // Build path
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                // Control points for smooth bezier
                val cp1 = Offset((p1.x + p2.x) / 2, p1.y)
                val cp2 = Offset((p1.x + p2.x) / 2, p2.y)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y)
            }
        }

        // Draw Fill Gradient
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillColor, Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Draw Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw Points
        points.forEachIndexed { index, point ->
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
}