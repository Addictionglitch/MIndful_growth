
package com.example.mindfulgrowth.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.* // Added for remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.GlassCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.ui.theme.MindfulPalette

// --- TYPOGRAPHY ---
private val PixelFont = FontFamily.Monospace

// --- LOGIC SECTION ---
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mindful_settings")

object PreferencesKeys {
    val AOD_ENABLED = booleanPreferencesKey("aod_enabled")
    val CLOCK_ENABLED = booleanPreferencesKey("clock_enabled")
    val BW_MODE = booleanPreferencesKey("bw_mode")
    val BRIGHTNESS = floatPreferencesKey("brightness")
    val WAKE_GESTURE = intPreferencesKey("wake_gesture")
    val RAISE_TO_WAKE = booleanPreferencesKey("raise_to_wake")
}

data class SettingsUiState(
    val aodEnabled: Boolean = true,
    val clockEnabled: Boolean = true,
    val bwMode: Boolean = false,
    val brightness: Float = 0.5f,
    val wakeGesture: Int = 0,
    val raiseToWakeEnabled: Boolean = false,
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
                    wakeGesture = prefs[PreferencesKeys.WAKE_GESTURE] ?: 0,
                    raiseToWakeEnabled = prefs[PreferencesKeys.RAISE_TO_WAKE] ?: false
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkPermissions() {
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else { true }
        _uiState.value = _uiState.value.copy(hasOverlayPermission = hasOverlay)
    }

    fun toggleAod(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(aodEnabled = enabled)
        saveBoolean(PreferencesKeys.AOD_ENABLED, enabled)
    }
    fun toggleClock(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(clockEnabled = enabled)
        saveBoolean(PreferencesKeys.CLOCK_ENABLED, enabled)
    }
    fun toggleBwMode(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(bwMode = enabled)
        saveBoolean(PreferencesKeys.BW_MODE, enabled)
    }
    fun updateBrightness(brightness: Float) {
        _uiState.value = _uiState.value.copy(brightness = brightness)
        viewModelScope.launch { context.dataStore.edit { it[PreferencesKeys.BRIGHTNESS] = brightness } }
    }

    fun setWakeGesture(gesture: Int) {
        _uiState.value = _uiState.value.copy(wakeGesture = gesture)
        viewModelScope.launch { context.dataStore.edit { it[PreferencesKeys.WAKE_GESTURE] = gesture } }
    }

    fun toggleRaiseToWake(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(raiseToWakeEnabled = enabled)
        saveBoolean(PreferencesKeys.RAISE_TO_WAKE, enabled)
    }

    private fun saveBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch { context.dataStore.edit { it[key] = value } }
    }

    fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

// --- UI SECTION ---

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit // Added onClose callback
) {
    val context = LocalContext.current
    val viewModel = remember { SettingsViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) { // Changed from Box to Column
        // Drag Handle and Close Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
            // Spacer to push Close button to the right
            Spacer(modifier = Modifier.weight(1f))
            // Close Button
            TextButton(onClick = onClose) {
                Text("Close", color = MindfulPalette.TextMedium, fontFamily = PixelFont)
            }
        }

        // Spacer between header row and content
        Spacer(modifier = Modifier.height(16.dp)) // Add some space after the drag handle row

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Occupy remaining vertical space
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp), // Adjusted padding
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // --- HEADER ---
            item {
                Text(
                    text = "SYSTEM_CONFIG",
                    style = MaterialTheme.typography.displaySmall,
                    color = MindfulPalette.TextHigh,
                    fontFamily = PixelFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // --- PERMISSION ALERT ---
            if (!uiState.hasOverlayPermission) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Warning, null, tint = MindfulPalette.CrimsonCore)
                                Spacer(Modifier.width(12.dp))
                                Text("PERMISSION_REQUIRED", color = MindfulPalette.TextHigh, fontWeight = FontWeight.Bold, fontFamily = PixelFont)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Overlay access required for visual injection.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MindfulPalette.TextMedium,
                                fontFamily = PixelFont
                            )
                            Spacer(Modifier.height(20.dp))
                            GlassButton(
                                text = "GRANT ACCESS",
                                onClick = { viewModel.requestOverlayPermission() }
                            )
                        }
                    }
                }
            }

            // --- DISPLAY SECTION ---
            item {
                SettingsSectionTitle("VISUAL_ARRAY")
                GlassCard(shape = RoundedCornerShape(24.dp)) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // AOD
                        InnerGlassPanel {
                            GlassToggleRow(
                                title = "AOD PROTOCOL",
                                icon = Icons.Rounded.Smartphone,
                                isChecked = uiState.aodEnabled,
                                onCheckedChange = { viewModel.toggleAod(it) }
                            )
                        }

                        // Brightness
                        InnerGlassPanel {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Brightness6,
                                        null,
                                        tint = MindfulPalette.TextMedium,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text("LUMINANCE", color = MindfulPalette.TextHigh, fontSize = 14.sp, fontFamily = PixelFont)
                                }
                                Spacer(Modifier.height(16.dp))
                                GlassSlider(
                                    value = uiState.brightness,
                                    onValueChange = { viewModel.updateBrightness(it) }
                                )
                            }
                        }
                    }
                }
            }

            // --- BEHAVIOR SECTION ---
            item {
                SettingsSectionTitle("BEHAVIOR_MATRIX")
                GlassCard(shape = RoundedCornerShape(24.dp)) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Monochrome Mode
                        InnerGlassPanel {
                            GlassToggleRow(
                                title = "MONOCHROME",
                                subtitle = "Greyscale Overlay",
                                icon = Icons.Rounded.FilterBAndW,
                                isChecked = uiState.bwMode,
                                onCheckedChange = { viewModel.toggleBwMode(it) }
                            )
                        }

                        // Wake Gesture
                        InnerGlassPanel {
                            Column(Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.TouchApp,
                                        null,
                                        tint = MindfulPalette.TextMedium,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text("WAKE TRIGGER", color = MindfulPalette.TextHigh, fontSize = 14.sp, fontFamily = PixelFont)
                                }
                                Spacer(Modifier.height(16.dp))
                                GlassSegmentedPicker(
                                    options = listOf("NULL", "TAP", "DBL"),
                                    selectedIndex = uiState.wakeGesture,
                                    onOptionSelected = { viewModel.setWakeGesture(it) }
                                )
                            }
                        }

                        // Raise to Wake
                        InnerGlassPanel {
                            GlassToggleRow(
                                title = "LIFT DETECT",
                                subtitle = "Accelerometer",
                                icon = Icons.Rounded.ScreenRotation,
                                isChecked = uiState.raiseToWakeEnabled,
                                onCheckedChange = { viewModel.toggleRaiseToWake(it) }
                            )
                        }
                    }
                }
            }
        }

        // --- SCROLL FADE GRADIENT ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MindfulPalette.Void)
                    )
                )
        )
    }
}

// --- REUSABLE COMPONENTS ---

@Composable
fun SettingsSectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 16.dp)
                .background(MindfulPalette.CrimsonCore)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MindfulPalette.TextMedium,
            fontFamily = PixelFont,
            letterSpacing = 2.sp
        )
    }
}

/**
 * Inner "floating" panels for individual settings.
 * Creates depth perception inside the main card.
 */
@Composable
fun InnerGlassPanel(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        content()
    }
}

@Composable
fun GlassSegmentedPicker(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val animatedColor by animateColorAsState(if (isSelected) MindfulPalette.CrimsonCore else Color.Transparent, label = "bg")
            val borderColor = if (isSelected) MindfulPalette.CrimsonCore else Color.White.copy(alpha = 0.1f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(animatedColor.copy(alpha = if (isSelected) 0.2f else 0f))
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    .clickable { onOptionSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontFamily = PixelFont,
                    fontSize = 12.sp,
                    color = if (isSelected) MindfulPalette.TextHigh else MindfulPalette.TextMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GlassToggleRow(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon in a small glass well
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (isChecked) MindfulPalette.CrimsonCore else MindfulPalette.TextMedium, modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(title, color = MindfulPalette.TextHigh, fontSize = 16.sp, fontFamily = PixelFont, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, color = MindfulPalette.TextMedium, style = MaterialTheme.typography.bodySmall, fontFamily = PixelFont)
            }
        }

        GlassSwitch(checked = isChecked)
    }
}

@Composable
fun GlassSwitch(checked: Boolean) {
    val thumbPos by animateFloatAsState(if (checked) 1f else 0f, label = "Switch")
    val trackColor = if (checked) MindfulPalette.CrimsonCore.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
    val thumbColor = if (checked) MindfulPalette.CrimsonCore else MindfulPalette.TextMedium

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(trackColor)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (20 * thumbPos).dp)
                .padding(4.dp)
                .size(20.dp)
                .neonGlow(if(checked) MindfulPalette.CrimsonCore else Color.Transparent, 10f, 0.8f)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

@Composable
fun GlassSlider(value: Float, onValueChange: (Float) -> Unit) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        colors = SliderDefaults.colors(
            thumbColor = MindfulPalette.CrimsonCore,
            activeTrackColor = MindfulPalette.CrimsonCore,
            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier.height(20.dp)
    )
}

@Composable
fun GlassButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MindfulPalette.CrimsonCore.copy(alpha = 0.1f))
            .border(1.dp, MindfulPalette.CrimsonCore.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MindfulPalette.CrimsonCore, fontWeight = FontWeight.Bold, fontFamily = PixelFont)
    }
}

// --- VISUAL EFFECTS ---

fun Modifier.neonGlow(
    color: Color,
    radius: Float = 20f,
    alpha: Float = 1f
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        paint.color = color.copy(alpha = alpha)
        paint.asFrameworkPaint().maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        val center = Offset(size.width / 2, size.height / 2)
        val size = size.minDimension / 1.5f // Draw slightly smaller so it stays "behind"
        canvas.drawCircle(center, size, paint)
    }
}
