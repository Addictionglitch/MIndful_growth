package com.example.mindfulgrowth.ui.screens.stats

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.MindfulTheme
import kotlin.math.roundToInt

// --- THEME ALIASES ---
// Use MindfulTheme.colors instead of SystemConfigColors directly for consistency
private val TextPrimary @Composable get() = MindfulTheme.colors.textPrimary
private val TextSecondary @Composable get() = MindfulTheme.colors.textSecondary
private val AccentColor @Composable get() = MindfulTheme.colors.goldPrimary
private val NeonAccent @Composable get() = MindfulTheme.colors.greenAccent

// --- HERO CARD (Interactive) ---
@Composable
fun FocusHeroCard(
    timeSaved: String, // Total Weekly Time
    trend: String,
    isTrendPositive: Boolean,
    graphData: List<Float>,
    modifier: Modifier = Modifier
) {
    // MOCK DATA for Dates (In real app, pass this from ViewModel)
    val graphLabels = remember {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    }
    
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val haptics = LocalHapticFeedback.current

    val displayLabel = if (selectedIndex != null) "FOCUS: ${graphLabels.getOrElse(selectedIndex!!) { "" }}" else "WEEKLY FOCUS"
    val displayValue = if (selectedIndex != null) {
        val rawVal = graphData.getOrElse(selectedIndex!!) { 0f }
        "${(rawVal * 120).toInt()}m" 
    } else {
        timeSaved
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { selectedIndex = null })
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
                    AnimatedContent(
                        targetState = displayLabel,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "hero_label"
                    ) { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium, // This is M3
                            color = if (selectedIndex != null) NeonAccent else TextSecondary
                        )
                    }
                    
                    Spacer(Modifier.height(4.dp))
                    
                    AnimatedContent(
                        targetState = displayValue,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "hero_value"
                    ) { value ->
                        Text(
                            text = value,
                            style = MaterialTheme.typography.displaySmall, // This is M3
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (selectedIndex == null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (isTrendPositive) NeonAccent.copy(alpha = 0.1f)
                                else MindfulTheme.colors.warning.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = trend,
                            color = if (isTrendPositive) NeonAccent else MindfulTheme.colors.warning,
                            style = MaterialTheme.typography.labelSmall, // This is M3
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(Modifier.fillMaxSize().padding(bottom = 16.dp)) {
                StatsLineGraph(
                    data = graphData,
                    labels = graphLabels,
                    lineColor = AccentColor,
                    fillColor = AccentColor.copy(alpha = 0.1f),
                    selectedIndex = selectedIndex,
                    onPointSelected = { index ->
                        selectedIndex = index
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                    }
                )
            }
        }
    }
}

// --- INTERACTIVE LINE GRAPH ---
@Composable
fun StatsLineGraph(
    data: List<Float>,
    labels: List<String>,
    lineColor: Color,
    fillColor: Color,
    selectedIndex: Int?,
    onPointSelected: (Int) -> Unit
) {
    if (data.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val labelColor = TextSecondary

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val width = size.width
                        val spacing = width / (data.size - 1)
                        val index = (offset.x / spacing).roundToInt().coerceIn(0, data.size - 1)
                        onPointSelected(index)
                    }
                )
            }
    ) {
        val width = size.width
        val height = size.height - 20.dp.toPx()
        val spacing = width / (data.size - 1)

        val points = data.mapIndexed { index, value ->
            val x = index * spacing
            val y = height - (value * height)
            Offset(x, y)
        }

        // 1. Draw Fill Gradient
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val cp1 = Offset((p1.x + p2.x) / 2, p1.y)
                val cp2 = Offset((p1.x + p2.x) / 2, p2.y)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y)
            }
        }

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

        // 2. Draw Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Draw Points & Selection
        points.forEachIndexed { index, point ->
            val isSelected = index == selectedIndex
            val shouldShowLabel = index == 0 || index == points.lastIndex || isSelected
            
            if (shouldShowLabel) {
                val measuredText = textMeasurer.measure(
                    text = labels.getOrElse(index) { "" },
                    style = TextStyle(
                        color = if (isSelected) Color.White else labelColor,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
                
                drawText(
                    textLayoutResult = measuredText,
                    topLeft = Offset(
                        x = point.x - (measuredText.size.width / 2),
                        y = height + 8.dp.toPx()
                    )
                )
            }

            drawCircle(
                color = if (isSelected) Color.White else lineColor,
                radius = if (isSelected) 6.dp.toPx() else 3.dp.toPx(),
                center = point
            )
            
            if (isSelected) {
                drawLine(
                    color = Color.White.copy(alpha = 0.5f),
                    start = Offset(point.x, point.y),
                    end = Offset(point.x, height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
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
                contentDescription = null,
                tint = NeonAccent,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium, // This is M3
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall, // This is M3
                    color = TextSecondary
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
    val currentProgress = progress.coerceIn(0f, 1f)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                // For accessibility, describe the state of the progress.
                // e.g., TalkBack will announce "60 percent".
                stateDescription = "${(currentProgress * 100).toInt()} percent"
            }
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("DAILY GOAL", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Text(
                    text = "${(currentProgress * 100).toInt()}% / $target",
                    style = MaterialTheme.typography.labelMedium,
                    color = AccentColor,
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
                        .fillMaxWidth(currentProgress)
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
    val alpha = 0.2f
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f * alpha))
        )
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
        Icon(Icons.Rounded.Smartphone, null, tint = MindfulTheme.colors.warning, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("SYNC ERROR", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AccentColor)) {
            Text("RETRY", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
