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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mindfulgrowth.service.ScreenStateService
import com.example.mindfulgrowth.ui.components.*
import com.example.mindfulgrowth.ui.navigation.BottomNavigationBar
import com.example.mindfulgrowth.ui.theme.*
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F1111), // BackgroundColor (kept but content gets gradient)
        topBar = { SettingsTopBar() },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF021024), // DarkNavy
                            Color(0xFF052659)  // RichBlue
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SettingsHeader(modifier = Modifier.padding(bottom = 16.dp)) }

            item { SectionHeader("DISPLAY & BEHAVIOR", Modifier.padding(vertical = 8.dp)) }

            item {
                SettingItem(
                    title = "Always On Display",
                    description = "Show tree when locked",
                    icon = Icons.Default.PhoneAndroid,
                    value = uiState.aodEnabled,
                    onToggle = viewModel::toggleAod
                )
            }
            item {
                SettingItem(
                    title = "Show Clock",
                    description = "Display time on lock screen",
                    icon = Icons.Default.AccessTime,
                    value = uiState.clockEnabled,
                    onToggle = viewModel::toggleClock
                )
            }
            item {
                SettingItem(
                    title = "Black & White Mode",
                    description = "Reduce visual stimulation",
                    icon = Icons.Default.Contrast,
                    value = uiState.bwMode,
                    onToggle = viewModel::toggleBwMode
                )
            }

            item { BrightnessSliderCard(uiState.brightness, viewModel::updateBrightness) }

            item { SectionHeader("WAKE GESTURE", Modifier.padding(vertical = 8.dp)) }

            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GlassSegmentedControl(
                        options = listOf("Single Tap", "Double Tap"),
                        selectedIndex = uiState.wakeGesture,
                        onSelectionChange = viewModel::setWakeGesture,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (!uiState.hasOverlayPermission) {
                item { PermissionCard(viewModel::requestOverlayPermission) }
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    TopAppBar(
        title = { Text("Settings") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F1111),
            titleContentColor = Color(0xFFF5F5F5)
        )
    )
}

@Composable
private fun SettingsHeader(modifier: Modifier = Modifier) {
    // Keep only the small subtitle under the TopAppBar to avoid duplicate screen titles.
    Column(modifier = modifier) {
        Text(
            text = "Customize your focus experience",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFA7A9A9), // TextSecondary
            modifier = Modifier.padding(vertical = 4.dp)
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
        style = MaterialTheme.typography.labelLarge,
        color = Color(0xFFD4AF37), // GoldPrimary
        modifier = modifier
    )
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    icon: ImageVector,
    value: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2121) // SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37), // Gold
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 16.dp)
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF5F5F5) // White
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777C7C) // Dimmed gray
                    )
                }
            }

            Switch(
                checked = value,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFD4AF37), // Gold
                    checkedTrackColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                    uncheckedThumbColor = Color(0xFF777C7C),
                    uncheckedTrackColor = Color(0xFF777C7C).copy(alpha = 0.3f)
                )
            )
        }
    }
}


@Composable
private fun BrightnessSliderCard(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
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
                        color = Color(0xFFF5F5F5) // TextPrimary
                    )
                    Text(
                        text = "Adjusts brightness only for Focus Mode",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA7A9A9) // TextSecondary
                    )
                }

                Text(
                    text = "${(brightness * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentOrange
                )
            }

            Spacer(Modifier.height(spacing.medium))

            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = accentOrange,
                    activeTrackColor = accentOrange,
                    inactiveTrackColor = surfaceCard.copy(alpha = 0.6f)
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
        bloomIntensity = 0.08f
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = accentOrange,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(spacing.medium))

            Text(
                text = "Permission Required",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color(0xFFF5F5F5), // TextPrimary
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(spacing.small))

            Text(
                text = "Focus Mode needs permission to display over other apps",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFA7A9A9), // TextSecondary
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(spacing.large))

            GlassButton(
                text = "Grant Permission",
                onClick = onRequestPermission,
                style = GlassButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
