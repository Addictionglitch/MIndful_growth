package com.example.mindfulgrowth.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.service.ScreenStateService
import com.example.mindfulgrowth.ui.components.*
import com.example.mindfulgrowth.ui.theme.MindfulTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// DataStore Extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mindful_settings")

object PreferencesKeys {
    val AOD_ENABLED = booleanPreferencesKey("aod_enabled")
    val CLOCK_ENABLED = booleanPreferencesKey("clock_enabled")
    val BW_MODE = booleanPreferencesKey("bw_mode")
    val BRIGHTNESS = floatPreferencesKey("brightness")
    val WAKE_GESTURE = intPreferencesKey("wake_gesture")
}

// ViewModel
data class SettingsUiState(
    val aodEnabled: Boolean = true,
    val clockEnabled: Boolean = true,
    val bwMode: Boolean = false,
    val brightness: Float = 0.5f,
    val wakeGesture: Int = 0,
    val hasOverlayPermission: Boolean = false,
    val isServiceRunning: Boolean = false
)

class SettingsViewModel(private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        checkPermissions()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                _uiState.value = _uiState.value.copy(
                    aodEnabled = prefs[PreferencesKeys.AOD_ENABLED] ?: true,
                    clockEnabled = prefs[PreferencesKeys.CLOCK_ENABLED] ?: true,
                    bwMode = prefs[PreferencesKeys.BW_MODE] ?: false,
                    brightness = prefs[PreferencesKeys.BRIGHTNESS] ?: 0.5f,
                    wakeGesture = prefs[PreferencesKeys.WAKE_GESTURE] ?: 0
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun checkPermissions() {
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
        _uiState.value = _uiState.value.copy(hasOverlayPermission = hasOverlay)
    }
    
    fun toggleAod(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[PreferencesKeys.AOD_ENABLED] = enabled }
            _uiState.value = _uiState.value.copy(aodEnabled = enabled)
        }
    }
    
    fun toggleClock(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[PreferencesKeys.CLOCK_ENABLED] = enabled }
            _uiState.value = _uiState.value.copy(clockEnabled = enabled)
        }
    }
    
    fun toggleBwMode(enabled: Boolean) {
        viewModelScope.launch {
            context.dataStore.edit { it[PreferencesKeys.BW_MODE] = enabled }
            _uiState.value = _uiState.value.copy(bwMode = enabled)
        }
    }
    
    fun updateBrightness(brightness: Float) {
        viewModelScope.launch {
            context.dataStore.edit { it[PreferencesKeys.BRIGHTNESS] = brightness }
            _uiState.value = _uiState.value.copy(brightness = brightness)
        }
    }
    
    fun setWakeGesture(gesture: Int) {
        viewModelScope.launch {
            context.dataStore.edit { it[PreferencesKeys.WAKE_GESTURE] = gesture }
            _uiState.value = _uiState.value.copy(wakeGesture = gesture)
        }
    }
    
    fun startFocusMode() {
        if (_uiState.value.hasOverlayPermission) {
            val intent = Intent(context, ScreenStateService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            _uiState.value = _uiState.value.copy(isServiceRunning = true)
        }
    }
    
    fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

// Main Composable
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    SettingsScreenContent(
        uiState = uiState,
        onAodToggle = viewModel::toggleAod,
        onClockToggle = viewModel::toggleClock,
        onBwModeToggle = viewModel::toggleBwMode,
        onBrightnessChange = viewModel::updateBrightness,
        onWakeGestureChange = viewModel::setWakeGesture,
        onStartFocusMode = viewModel::startFocusMode,
        onRequestPermission = viewModel::requestOverlayPermission,
        modifier = modifier
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState,
    onAodToggle: (Boolean) -> Unit,
    onClockToggle: (Boolean) -> Unit,
    onBwModeToggle: (Boolean) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onWakeGestureChange: (Int) -> Unit,
    onStartFocusMode: () -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val spacing = MindfulTheme.spacing
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
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
        
        FloatingParticles()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(spacing.lg)
        ) {
            SettingsHeader(modifier = Modifier.padding(bottom = spacing.xl))
            
            SectionHeader(
                title = "DISPLAY & BEHAVIOR",
                modifier = Modifier.padding(bottom = spacing.md)
            )
            
            SettingToggleCard(
                title = "Always On Display",
                subtitle = "Show tree when locked",
                icon = Icons.Default.PhoneAndroid,
                checked = uiState.aodEnabled,
                onCheckedChange = onAodToggle,
                modifier = Modifier.padding(bottom = spacing.sm)
            )
            
            SettingToggleCard(
                title = "Show Clock",
                subtitle = "Display time on lock screen",
                icon = Icons.Default.AccessTime,
                checked = uiState.clockEnabled,
                onCheckedChange = onClockToggle,
                modifier = Modifier.padding(bottom = spacing.sm)
            )
            
            SettingToggleCard(
                title = "Black & White Mode",
                subtitle = "Reduce visual stimulation",
                icon = Icons.Default.Contrast,
                checked = uiState.bwMode,
                onCheckedChange = onBwModeToggle,
                modifier = Modifier.padding(bottom = spacing.md)
            )
            
            BrightnessSliderCard(
                brightness = uiState.brightness,
                onBrightnessChange = onBrightnessChange,
                modifier = Modifier.padding(bottom = spacing.xl)
            )
            
            SectionHeader(
                title = "WAKE GESTURE",
                modifier = Modifier.padding(bottom = spacing.md)
            )
            
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.xl)
            ) {
                GlassSegmentedControl(
                    options = listOf("Single Tap", "Double Tap"),
                    selectedIndex = uiState.wakeGesture,
                    onSelectionChange = onWakeGestureChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (!uiState.hasOverlayPermission) {
                PermissionCard(
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.padding(bottom = spacing.xl)
                )
            }
            
            Spacer(Modifier.weight(1f))
            
            GlassButton(
                text = if (uiState.isServiceRunning) "Focus Mode Active" else "Activate Focus Mode",
                onClick = onStartFocusMode,
                style = GlassButtonStyle.PRIMARY,
                enabled = uiState.hasOverlayPermission && !uiState.isServiceRunning,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SettingsHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MindfulTheme.colors.textPrimary
        )
        Text(
            text = "Customize your focus experience",
            style = MaterialTheme.typography.bodyMedium,
            color = MindfulTheme.colors.textSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        ),
        color = MindfulTheme.colors.goldPrimary,
        modifier = modifier
    )
}

@Composable
private fun SettingToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCardCompact(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MindfulTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MindfulTheme.colors.glassBackground,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MindfulTheme.colors.goldPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MindfulTheme.colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MindfulTheme.colors.textSecondary
                )
            }
            
            AnimatedSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun AnimatedSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "switchThumb"
    )
    
    Box(
        modifier = modifier
            .width(52.dp)
            .height(32.dp)
            .background(
                color = if (checked) colors.goldPrimary else colors.glassBackground,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            .padding(4.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = thumbOffset)
                .background(
                    color = if (checked) androidx.compose.ui.graphics.Color(0xFF121212) else colors.textSecondary,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
    }
}

@Composable
private fun BrightnessSliderCard(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val spacing = MindfulTheme.spacing
    
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AOD Brightness",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Adjusts brightness only for Focus Mode",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                
                Text(
                    text = "${(brightness * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colors.goldPrimary
                )
            }
            
            Spacer(Modifier.height(spacing.md))
            
            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = colors.goldPrimary,
                    activeTrackColor = colors.goldPrimary,
                    inactiveTrackColor = colors.glassBackground
                )
            )
        }
    }
}

@Composable
private fun PermissionCard(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        bloom = true,
        bloomIntensity = 0.5f
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MindfulTheme.colors.warning,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.md))
            
            Text(
                text = "Permission Required",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MindfulTheme.colors.textPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(Modifier.height(MindfulTheme.spacing.sm))
            
            Text(
                text = "Focus Mode needs permission to display over other apps",
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