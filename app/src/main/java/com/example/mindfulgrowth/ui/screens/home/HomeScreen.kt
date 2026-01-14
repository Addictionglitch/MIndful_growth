package com.example.mindfulgrowth.ui.screens.home

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.theme.MindfulPalette
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin

// --- MAIN SCREEN ---
@Composable
fun HomeScreen(viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val timerState by viewModel.timerState.collectAsState()
    var isTimeSelectorVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // --- 1. DASHBOARD CONTENT (Goal, Streak, Time) ---
        AnimatedVisibility(
            visible = !isTimeSelectorVisible,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // DAILY GOAL & STREAK ROW
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    // Streak
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFFF5722), // Orange Fire
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${timerState.streakDays}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Daily Goal Mini-Bar
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val progress = (timerState.dailyGoalCurrent.toFloat() / timerState.dailyGoalTarget).coerceIn(0f, 1f)
                        Text(
                            text = "Daily Goal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.width(80.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = MindfulPalette.NeonGreen,
                            trackColor = Color.White.copy(alpha = 0.1f),
                        )
                    }
                }

                // CLICKABLE TIME DISPLAY
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clickable(
                            enabled = timerState.mode == TimerMode.IDLE,
                            onClick = { isTimeSelectorVisible = true }
                        )
                        .padding(16.dp) // Touch target padding
                ) {
                    Text(
                        text = formatTime(timerState.timeRemaining),
                        fontSize = 80.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Light,
                        color = if (timerState.mode == TimerMode.OVERGROWTH) MindfulPalette.NeonGreen else Color.White
                    )

                    // Hint text below time
                    if (timerState.mode == TimerMode.IDLE) {
                        Text(
                            text = "tap to edit",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.align(Alignment.BottomCenter).offset(y = 24.dp)
                        )
                    }
                }
            }
        }

        // --- 2. BOTTOM ACTION BUTTON ---
        // Keeps the layout stable
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            if (!isTimeSelectorVisible) {
                IconButton(
                    onClick = { viewModel.toggleTimer() },
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MindfulPalette.NeonGreen.copy(alpha = 0.15f))
                        .border(2.dp, MindfulPalette.NeonGreen, CircleShape)
                ) {
                    Icon(
                        imageVector = if (timerState.isActive) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        contentDescription = if (timerState.isActive) "Stop" else "Start",
                        tint = MindfulPalette.NeonGreen,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }

        // --- 3. TIME SELECTOR OVERLAY ---
        AnimatedVisibility(
            visible = isTimeSelectorVisible,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f))
        ) {
            Box(Modifier.fillMaxSize()) {
                // The Wheel
                Row(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(0.5f).fillMaxHeight()) {
                        VerticalTimeWheel(
                            initialMinutes = (timerState.selectedDuration / 60).toInt(),
                            onTimeChanged = { mins ->
                                viewModel.setDuration(mins)
                            }
                        )
                    }
                    // Info/Instructions on the right side of wheel
                    Column(
                        Modifier.weight(0.5f).fillMaxHeight().padding(end = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Set Timer",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MindfulPalette.NeonGreen
                        )
                        Text(
                            text = formatTime(timerState.selectedDuration),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Close / Done Button
                IconButton(
                    onClick = { isTimeSelectorVisible = false },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 64.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// --- REUSED WHEEL COMPONENT ---
@Composable
fun VerticalTimeWheel(
    initialMinutes: Int,
    onTimeChanged: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val scrollOffset = remember { Animatable(initialMinutes.toFloat()) }
    val scope = rememberCoroutineScope()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        val current = scrollOffset.value
                        val target = (round(current / 5) * 5).coerceIn(5f, 720f)
                        scope.launch {
                            scrollOffset.animateTo(target, spring(stiffness = Spring.StiffnessLow))
                            onTimeChanged(target.toInt())
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    // Dragging UP increases time
                    val newVal = (scrollOffset.value - (dragAmount / 15f)).coerceIn(5f, 720f)

                    // Haptic tick when crossing 5-min threshold
                    if (newVal.toInt() / 5 != scrollOffset.value.toInt() / 5) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }

                    scope.launch { scrollOffset.snapTo(newVal) }
                    onTimeChanged(newVal.toInt())
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val radius = height * 0.65f
        val circleCenterX = radius + (width * 0.4f)

        val currentMinute = scrollOffset.value
        val range = 40

        for (i in (currentMinute - range).toInt()..(currentMinute + range).toInt()) {
            if (i % 5 != 0) continue

            val diff = i - currentMinute
            val angleDeg = 180f + (diff * 2.5f)
            val angleRad = Math.toRadians(angleDeg.toDouble())

            val tickX = circleCenterX + (radius * cos(angleRad)).toFloat()
            val tickY = centerY + (radius * sin(angleRad)).toFloat()

            val alpha = (1f - (abs(diff) / 25f)).coerceIn(0f, 1f)

            if (alpha > 0) {
                val isSelected = abs(diff) < 2.5f
                val color = if (isSelected) MindfulPalette.NeonGreen else Color.White.copy(alpha = 0.3f)

                drawLine(
                    color = color,
                    start = Offset(tickX, tickY),
                    end = Offset(tickX - (if (isSelected) 40f else 20f), tickY),
                    strokeWidth = if (isSelected) 4f else 2f,
                    alpha = alpha
                )

                if (isSelected || i % 15 == 0) {
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            "${i}m",
                            tickX - 60f,
                            tickY + 12f,
                            Paint().apply {
                                setColor(color.toArgb())
                                textSize = if (isSelected) 48f else 32f
                                textAlign = Paint.Align.RIGHT
                                this.alpha = (alpha * 255).toInt()
                                typeface = Typeface.MONOSPACE
                            }
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}