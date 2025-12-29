package com.example.mindfulgrowth.ui.screens.stats

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.Paint as AndroidPaint
import android.graphics.Path
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.mindfulgrowth.ui.components.GlassButton
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.components.GlassButtonStyle
import com.example.mindfulgrowth.ui.theme.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val hasPermission = checkUsageStatsPermission(context)
        viewModel.updatePermissionStatus(hasPermission)
        if (hasPermission) {
            viewModel.refreshData()
        }
    }

    StatsScreenContent(
        uiState = uiState,
        onRequestPermission = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
        modifier = modifier
    )
}

@Composable
private fun StatsScreenContent(
    uiState: StatsUiState,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF021024), // DarkNavy top
                        Color(0xFF052659)  // RichBlue bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (!uiState.hasPermission) {
                PermissionPrompt(onRequestPermission = onRequestPermission)
            } else {
                UsageLineChartCard(uiState.dailyUsageHours, uiState.dayLabels)
                Spacer(Modifier.height(16.dp))
                MetricsDonutCharts(uiState.treesGrown, uiState.forestProgress)
                Spacer(Modifier.height(16.dp))
                TotalFocusBarChart(uiState.dailyUsageHours)
            }
            Spacer(Modifier.height(100.dp)) // Padding for floating nav bar
        }
    }
}

@Composable
private fun UsageLineChartCard(usageHours: List<Float>, dayLabels: List<String>) {
    var selectedIndex by remember { mutableStateOf(usageHours.size - 1) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        blur = true,
        bloom = false,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Text("DAILY USE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp), color = Color(0xFFD4AF37))
            Text("Last 7 days", style = MaterialTheme.typography.bodySmall, color = Color(0xFF777C7C))
            Spacer(Modifier.height(24.dp))
            UsageLineChart(usageHours, dayLabels, selectedIndex) { index -> selectedIndex = index }
        }
    }
}

@Composable
private fun UsageLineChart(data: List<Float>, labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val goldColor = GoldPrimary
    val paint = remember {
        AndroidPaint().apply { color = goldColor.toArgb() }
    }

    // Reuse a text paint (Android native Paint) to avoid reallocations per draw
    val textPaint = remember {
        AndroidPaint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            textAlign = AndroidPaint.Align.CENTER
        }
    }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .pointerInput(Unit) {
            detectTapGestures {
                val xStep = size.width / (data.size - 1)
                val index = (it.x / xStep).roundToInt().coerceIn(0, data.size - 1)
                onSelect(index)
            }
        }
    ) { 
        val path = Path()
        val xStep = size.width / (data.size - 1)
        val maxVal = data.maxOrNull() ?: 1f

        data.forEachIndexed { i, value ->
            val x = i * xStep
            val y = size.height * (1 - (value / maxVal))
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPath(path, paint)
        }

        val selectedX = selectedIndex * xStep
        val selectedY = size.height * (1 - (data[selectedIndex] / maxVal))
        drawCircle(color = goldColor, radius = 8.dp.toPx(), center = Offset(selectedX, selectedY))
        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = Offset(selectedX, selectedY))

        // Draw labels under the chart (use labels parameter)
        // Update text size each draw (dp -> px depends on density)
        textPaint.textSize = 12.dp.toPx()
        textPaint.alpha = (0.85f * 255).toInt()

        labels.forEachIndexed { i, lbl ->
            val x = i * xStep
            val y = size.height + 18.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText(lbl, x, y, textPaint)
        }
    }
}

@Composable
private fun MetricsDonutCharts(treesGrown: Int, goalProgress: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GlassCard(modifier = Modifier.weight(1f).aspectRatio(1f), cornerRadius = 12.dp, blur = true, bloom = false, contentPadding = PaddingValues(12.dp)) {
            DonutChart(title = "Trees Grown", value = treesGrown.toString(), progress = 0.75f, color = accentBlue)
        }
        GlassCard(modifier = Modifier.weight(1f).aspectRatio(1f), cornerRadius = 12.dp, blur = true, bloom = false, contentPadding = PaddingValues(12.dp)) {
            DonutChart(title = "Goal Reached", value = "${(goalProgress * 100).toInt()}%", progress = goalProgress, color = accentOrange)
        }
    }
}

@Composable
fun DonutChart(title: String, value: String, progress: Float, color: Color) {
    val animatedProgress by animateFloatAsState(targetValue = progress, animationSpec = tween(300), label = "")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            Canvas(modifier = Modifier.size(100.dp)) {
                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = -225f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -225f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = 20f, cap = StrokeCap.Round)
                )
            }
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFFF5F5F5))
        }
        Spacer(Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF777C7C))
    }
}

@Composable
private fun TotalFocusBarChart(data: List<Float>) {
    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp, blur = true, bloom = false, contentPadding = PaddingValues(16.dp)) {
        Column {
            Text("TOTAL FOCUS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp), color = Color(0xFFD4AF37))
            Spacer(Modifier.height(16.dp))
            BarChart(data)
        }
    }
}

@Composable
private fun BarChart(data: List<Float>) {
    val brush = Brush.verticalGradient(listOf(accentBlue, accentPurple))
    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        val barWidth = size.width / (data.size * 2 - 1)
        val maxVal = data.maxOrNull() ?: 1f
        data.forEachIndexed { index, value ->
            val barHeight = size.height * (value / maxVal)
            drawRoundRect(
                brush = brush,
                topLeft = Offset(x = index * barWidth * 2, y = size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

@Composable
private fun PermissionPrompt(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 12.dp,
        blur = true,
        bloom = false,
        contentPadding = PaddingValues(16.dp)
    ) {
         Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             modifier = Modifier.padding(16.dp)
         ) {
            Text(
                text = "Usage Statistics Required",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFFF5F5F5)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "To track your screen time and show your growth, we need access to usage statistics.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF777C7C),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            GlassButton(
                text = "Grant Permission",
                onClick = onRequestPermission,
                style = GlassButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
         }
     }
}

@Suppress("DEPRECATION")
private fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

class StatsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    fun refreshData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(1000) // Simulate network/data fetch
            // TODO: Load actual usage stats here
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                achievements = listOf(
                    Achievement("early_riser", "Early Riser", 0, true),
                    Achievement("7_day_streak", "7 Day Streak", 0, true),
                    Achievement("master_gardener", "Master Gardener", 0, false)
                )
            )
        }
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.value = _uiState.value.copy(hasPermission = hasPermission)
    }
}

data class StatsUiState(
    val dailyUsageHours: List<Float> = listOf(2.5f, 3.2f, 1.8f, 4.1f, 2.9f, 3.5f, 2.1f),
    val dayLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today"),
    val treesGrown: Int = 14,
    val forestProgress: Float = 0.6f,
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val iconRes: Int,
    val isUnlocked: Boolean
)
