
package com.example.mindfulgrowth.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Centralized Brush Definition
@Immutable
data class MindfulBrushes(
    val primaryBrush: Brush = Brush.radialGradient(
        colors = listOf(Color.DarkGray.copy(alpha = 0.3f), VoidBlack)
    )
)

val LocalMindfulBrushes = staticCompositionLocalOf { MindfulBrushes() }


private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = NeonCyan,
    tertiary = NeonGreen,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1E)
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    secondary = NeonCyan,
    tertiary = NeonGreen
)

@Composable
fun MindfulGrowthTheme(
    darkTheme: Boolean = true, // Force dark theme
    dynamicColor: Boolean = false, // Disable dynamic color for consistency
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicDarkColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb() // Make status bar transparent
            window.navigationBarColor = Color.Transparent.toArgb() // Make nav bar transparent
            WindowCompat.setDecorFitsSystemWindows(window, false) // Go Edge-to-Edge
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Easy access extension
val MaterialTheme.brushes: MindfulBrushes
    @Composable
    get() = LocalMindfulBrushes.current
