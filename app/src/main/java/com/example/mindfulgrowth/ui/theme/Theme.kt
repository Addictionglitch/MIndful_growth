
package com.example.mindfulgrowth.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    background = MindfulPalette.Void,
    surface = MindfulPalette.Glass,
    onBackground = MindfulPalette.TextHigh,
    onSurface = MindfulPalette.TextHigh,
    primary = MindfulPalette.TextHigh,
    onPrimary = MindfulPalette.Void,
    secondary = MindfulPalette.TextMedium,
    onSecondary = MindfulPalette.Void,
)

data class GlassStyle(
    val blurRadius: Dp = 30.dp,
    val backgroundAlpha: Float = 0.05f,
    val rimStrokeWidth: Dp = 1.dp
)

val LocalGlassStyle = compositionLocalOf { GlassStyle() }

@Composable
fun MindfulGrowthTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    CompositionLocalProvider(LocalGlassStyle provides GlassStyle()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
