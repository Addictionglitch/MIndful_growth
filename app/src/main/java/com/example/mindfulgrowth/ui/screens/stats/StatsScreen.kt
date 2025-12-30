package com.example.mindfulgrowth.ui.screens.stats

import android.graphics.BlurMaskFilter
import android.graphics.Paint as NativePaint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

// --- DESIGN SYSTEM ---
private val CrimsonCore = Color(0xFFFF0007)
private val DeepCrimson = Color(0xFF2D0001)
private val VoidBlack = Color(0xFF0A0F14)
private val TextPrimary = Color(0xFFE8E8E8)
private val TextSecondary = Color(0xFF9CA3AF)
private val PixelFont = FontFamily.Monospace

// --- STATE MANAGEMENT ---
enum class StatsRange {
    WEEK, MONTH, YEAR
}

data class StatsUiState(
    val selectedRange: StatsRange = StatsRange.WEEK,
    val totalTimeSaved: String = "4.2h",
    val graphData: List<Float> = listOf(2f, 3.5f, 1.0f, 4.2f, 3.8f, 5.5f, 4.0f),
    val treesGrown: Int = 148 // Example data
) {
    // 1 Forest = 24 Trees
    val forestsGrown: Int get() = treesGrown / 24
}

class StatsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    fun setRange(range: StatsRange) {
        // Mock data logic
        val newData = when (range) {
            StatsRange.WEEK -> listOf(2f, 3.5f, 1.0f, 4.2f, 3.8f, 5.5f, 4.0f)
            StatsRange.MONTH -> listOf(3f, 5f, 2f, 4f, 6f, 5f, 7f, 6f, 4f, 5f, 3f, 4f)
            StatsRange.YEAR -> listOf(20f, 45f, 30f, 50f, 60f, 55f, 70f, 65f, 50f, 60f, 40f, 55f)
        }
        val newTotal = when (range) {
            StatsRange.WEEK -> "4.2h"
            StatsRange.MONTH -> "18.5h"
            StatsRange.YEAR -> "210h"
        }
        val newTrees = when (range) {
            StatsRange.WEEK -> 12
            StatsRange.MONTH -> 58
            StatsRange.YEAR -> 740
        }

        _uiState.value = _uiState.value.copy(
            selectedRange = range,
            graphData = newData,
            totalTimeSaved = newTotal,
            treesGrown = newTrees
        )
    }
}

// --- MAIN SCREEN ---
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ambient Cyberpunk Background
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(DeepCrimson, VoidBlack),
                    center = Offset(500f, -200f),
                    radius = 1800f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. HEADER
            StatsTimeSelector(
                currentRange = state.selectedRange,
                onRangeSelected = { viewModel.setRange(it) }
            )

            // 2. TIME SAVED GRAPH
            TimeSavedGraphCard(
                totalTime = state.totalTimeSaved,
                dataPoints = state.graphData
            )

            // 3. SPLIT GLASS FRAGMENTS (Trees & Forests)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Fragment 1: Trees
                StatFragmentCard(
                    title = "TREES_GROWN",
                    value = "${state.treesGrown}",
                    icon = Icons.Rounded.Park,
                    modifier = Modifier.weight(1f)
                )

                // Fragment 2: Forests (Value / 24)
                StatFragmentCard(
                    title = "FORESTS_GROWN",
                    value = "${state.forestsGrown}",
                    icon = Icons.Rounded.Forest,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun StatFragmentCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .neonGlow(CrimsonCore, radius = 30f, alpha = 0.1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent,
                        CrimsonCore.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CrimsonCore,
                    modifier = Modifier.size(20.dp)
                )
                // Tiny decorative dot
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                )
            }

            // Value & Title
            Column {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontFamily = PixelFont,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun TimeSavedGraphCard(
    totalTime: String,
    dataPoints: List<Float>
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(280.dp)
            .neonGlow(CrimsonCore, radius = 60f, alpha = 0.1f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent,
                        CrimsonCore.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "// TIME_RECLAIMED",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = PixelFont,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = totalTime,
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontFamily = PixelFont,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(CrimsonCore, RoundedCornerShape(2.dp))
                        .neonGlow(CrimsonCore, 10f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                SmoothLineGraph(dataPoints = dataPoints)
            }
        }
    }
}

@Composable
fun SmoothLineGraph(
    dataPoints: List<Float>
) {
    var touchX by remember { mutableStateOf<Float?>(null) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { touchX = null },
                    onDragCancel = { touchX = null }
                ) { change, _ ->
                    touchX = change.position.x
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val maxVal = dataPoints.maxOrNull() ?: 1f
        val points = dataPoints.mapIndexed { index, value ->
            val x = index * (width / (dataPoints.size - 1))
            val y = height - (value / maxVal * height)
            Offset(x, y)
        }

        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points[0].x, points[0].y)
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                val cp1 = Offset((p1.x + p2.x) / 2, p1.y)
                val cp2 = Offset((p1.x + p2.x) / 2, p2.y)
                path.cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, p2.x, p2.y)
            }
        }

        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    CrimsonCore.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(width = 2.dp.toPx())
        )
        drawPath(
            path = path,
            color = CrimsonCore,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = CrimsonCore.copy(alpha = 0.4f),
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )

        touchX?.let { tx ->
            val step = width / (dataPoints.size - 1)
            val index = (tx / step).roundToInt().coerceIn(0, dataPoints.size - 1)
            val activePoint = points[index]

            drawLine(
                color = TextSecondary.copy(alpha = 0.5f),
                start = Offset(activePoint.x, 0f),
                end = Offset(activePoint.x, height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            drawCircle(color = CrimsonCore, radius = 6.dp.toPx(), center = activePoint)
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = activePoint)
        }
    }
}

@Composable
fun StatsTimeSelector(
    currentRange: StatsRange,
    onRangeSelected: (StatsRange) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(56.dp)
            .neonGlow(CrimsonCore, radius = 40f, alpha = 0.15f)
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StatsRange.values().forEach { range ->
                val isSelected = currentRange == range
                GlassTabButton(
                    text = range.name,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { onRangeSelected(range) }
                )
            }
        }
    }
}

@Composable
fun GlassTabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonCore.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(300),
        label = "bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonCore.copy(alpha = 0.8f) else Color.Transparent,
        animationSpec = tween(300),
        label = "border"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) CrimsonCore else TextSecondary,
        animationSpec = tween(300),
        label = "text"
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.5f else 0f,
        animationSpec = tween(300),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .neonGlow(CrimsonCore, radius = 25f, alpha = glowAlpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = PixelFont,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}

// --- UTILS ---

fun Modifier.neonGlow(
    color: Color,
    radius: Float = 20f,
    alpha: Float = 1f
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = androidx.compose.ui.graphics.Paint()
        paint.color = color.copy(alpha = alpha)
        paint.asFrameworkPaint().maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        val center = Offset(size.width / 2, size.height / 2)
        canvas.drawCircle(center, size.minDimension / 1.8f, paint)
    }
}