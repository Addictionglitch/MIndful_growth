package com.example.mindfulgrowth.ui.screens.stats

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.*
import com.example.mindfulgrowth.ui.theme.MindfulTheme
import com.example.mindfulgrowth.ui.components.WaveGraphView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel
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

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Check permission on composition
    LaunchedEffect(Unit) {
        val hasPermission = checkUsageStatsPermission(context)
        viewModel.updatePermissionStatus(hasPermission)
        if (hasPermission) {
            viewModel.refreshData()
        }
    }
    
    StatsScreenContent(
        uiState = uiState,
        onRefresh = viewModel::refreshData,
        onRequestPermission = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        },
        modifier = modifier
    )
}

@Composable
private fun StatsScreenContent(
    uiState: StatsUiState,
    onRefresh: () -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val spacing = MindfulTheme.spacing
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.gradientStart,
                                colors.gradientMid,
                                colors.gradientEnd
                            )
                        )
                    )
                }
        )
        
        // Floating particles
        FloatingParticles()
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(spacing.lg)
        ) {
            // Header
            WelcomeHeader(
                modifier = Modifier.padding(bottom = spacing.xl)
            )
            
            // Permission prompt or content
            if (!uiState.hasPermission) {
                PermissionPrompt(
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.padding(bottom = spacing.lg)
                )
            } else {
                // Usage graph section
                UsageGraphSection(
                    usageHours = uiState.dailyUsageHours,
                    dayLabels = uiState.dayLabels,
                    onRefresh = onRefresh,
                    isLoading = uiState.isLoading,
                    modifier = Modifier.padding(bottom = spacing.md)
                )
                
                Spacer(Modifier.height(spacing.md))
                
                // Metrics row
                MetricsRow(
                    treesGrown = uiState.treesGrown,
                    forestProgress = uiState.forestProgress,
                    modifier = Modifier.padding(bottom = spacing.md)
                )
                
                Spacer(Modifier.height(spacing.md))
                
                // Achievements
                AchievementsSection(
                    achievements = uiState.achievements,
                    modifier = Modifier.padding(bottom = spacing.xl)
                )
            }
            
            // Bottom padding
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun WelcomeHeader(
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    
    Column(modifier = modifier) {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = colors.textGold
        )
        
        Text(
            text = "Your digital wellbeing journey",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun PermissionPrompt(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        bloom = true,
        bloomIntensity = 0.5f
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(MindfulTheme.spacing.lg)
        ) {
            Text(
                text = "Usage Statistics Required",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MindfulTheme.colors.textPrimary
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.sm))
            
            Text(
                text = "To track your screen time and show your growth, we need access to usage statistics.",
                style = MaterialTheme.typography.bodyMedium,
                color = MindfulTheme.colors.textSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.lg))
            
            GlassButton(
                text = "Grant Permission",
                onClick = onRequestPermission,
                style = GlassButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun UsageGraphSection(
    usageHours: List<Float>,
    dayLabels: List<String>,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        blur = true,
        bloom = true
    ) {
        Column {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY USE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MindfulTheme.colors.textSecondary
                    )
                    
                    Text(
                        text = "Last 7 days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindfulTheme.colors.textTertiary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh data",
                        tint = MindfulTheme.colors.goldPrimary,
                        modifier = Modifier.then(
                            if (isLoading) {
                                Modifier.graphicsLayer {
                                    rotationZ = 360f
                                }
                            } else Modifier
                        )
                    )
                }
            }
            
            Spacer(Modifier.height(MindfulTheme.spacing.md))
            
            // Graph
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ShimmerEffect()
                }
            } else {
                AndroidView(
                    factory = { context ->
                        WaveGraphView(context).apply {
                            setData(usageHours, dayLabels)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
    }
}

@Composable
private fun MetricsRow(
    treesGrown: Int,
    forestProgress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md)
    ) {
        // Trees grown card
        MetricCard(
            title = "Trees Grown",
            value = treesGrown.toString(),
            icon = "🌳",
            modifier = Modifier.weight(1f)
        )
        
        // Forest progress card
        ProgressCard(
            title = "Forest Progress",
            progress = forestProgress,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.height(160.dp),
        bloom = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.sm))
            
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MindfulTheme.colors.textPrimary
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MindfulTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun ProgressCard(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )
    
    GlassCard(
        modifier = modifier.height(160.dp),
        bloom = true
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌲",
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.md))
            
            // Circular progress indicator
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    color = MindfulTheme.colors.greenAccent,
                    strokeWidth = 6.dp,
                    trackColor = MindfulTheme.colors.glassBackground
                )
                
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MindfulTheme.colors.textPrimary
                )
            }
            
            Spacer(Modifier.height(MindfulTheme.spacing.sm))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MindfulTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun AchievementsSection(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        blur = true
    ) {
        Column {
            Text(
                text = "ACHIEVEMENTS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MindfulTheme.colors.textSecondary,
                modifier = Modifier.padding(bottom = MindfulTheme.spacing.lg)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                achievements.take(3).forEach { achievement ->
                    AchievementItem(
                        title = achievement.title,
                        isUnlocked = achievement.isUnlocked,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementItem(
    title: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isUnlocked) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "achievementScale"
    )
    
    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                PulsingGlow(
                    color = MindfulTheme.colors.goldPrimary,
                    modifier = Modifier.size(72.dp)
                )
            }
            
            Text(
                text = if (isUnlocked) "🏆" else "🔒",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.graphicsLayer {
                    alpha = if (isUnlocked) 1f else 0.3f
                }
            )
        }
        
        Spacer(Modifier.height(MindfulTheme.spacing.xs))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = if (isUnlocked) {
                MindfulTheme.colors.textPrimary
            } else {
                MindfulTheme.colors.textTertiary
            },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2
        )
    }
}

// Helper function
private fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}