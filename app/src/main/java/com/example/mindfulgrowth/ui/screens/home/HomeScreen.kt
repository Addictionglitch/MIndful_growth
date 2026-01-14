
package com.example.mindfulgrowth.ui.screens.home

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import com.example.mindfulgrowth.ui.theme.NeonCyan
import com.example.mindfulgrowth.ui.theme.NeonGreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_timerState.value.isActive) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timerState.value = _timerState.value.copy(isActive = true, sessionComplete = false)
            while (_timerState.value.timeRemaining > 0) {
                delay(1000)
                _timerState.value = _timerState.value.copy(
                    timeRemaining = _timerState.value.timeRemaining - 1,
                    progress = 1f - (_timerState.value.timeRemaining.toFloat() / _timerState.value.duration)
                )
            }
            _timerState.value = _timerState.value.copy(sessionComplete = true)
            stopTimer()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isActive = false, progress = 0f, timeRemaining = _timerState.value.duration)
    }
}

data class TimerState(
    val duration: Long = 25 * 60,
    val timeRemaining: Long = 25 * 60,
    val progress: Float = 0f,
    val isActive: Boolean = false,
    val sessionComplete: Boolean = false
)

@Composable
fun HomeScreen(viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val timerState by viewModel.timerState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 64.dp, bottom = 32.dp)
        ) {
            Text("Focus Session", fontSize = 24.sp, fontFamily = FontFamily.Monospace, color = Color.White)

            FocusTimerRing(
                progress = timerState.progress,
                isActive = timerState.isActive,
                time = formatTime(timerState.timeRemaining),
                modifier = Modifier.size(250.dp)
            )

            if (timerState.sessionComplete) {
                Text("Session Complete!", color = NeonGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoCard("Session Goal", "25 min")
                    InfoCard("Streak", "3 days")
                }
            }

            IconButton(
                onClick = { viewModel.toggleTimer() },
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = if (timerState.isActive) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = if (timerState.isActive) "Stop" else "Start",
                    tint = if (timerState.isActive) NeonGreen else NeonCyan,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}

@Composable
fun FocusTimerRing(
    progress: Float,
    isActive: Boolean,
    time: String,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val breathing by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                style = Stroke(width = 15.dp.toPx())
            )

            drawArc(
                color = NeonCyan,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
            )

            if (isActive) {
                drawArc(
                    color = NeonGreen,
                    startAngle = -90f,
                    sweepAngle = 360 * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round),
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Park,
                contentDescription = "Growth Tree",
                tint = if (isActive) NeonGreen else Color.White,
                modifier = Modifier.size(100.dp * if (isActive) breathing else 1f)
            )
            Text(
                text = time,
                color = Color.White,
                fontSize = 50.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    GlassCard(cornerRadius = 16.dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, color = Color.White.copy(alpha = 0.7f))
            Text(text = value, color = Color.White, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

@Preview
@Composable
fun HomeScreenPreview() {
    MindfulGrowthTheme {
        HomeScreen()
    }
}
