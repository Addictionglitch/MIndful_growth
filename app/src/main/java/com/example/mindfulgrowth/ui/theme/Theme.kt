package com.example.mindfulgrowth.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 1. Define Colors
@Immutable
data class MindfulColors(
    val goldPrimary: Color = Color(0xFFC69C6D),
    val goldLight: Color = Color(0xFFD4B896),
    val goldDark: Color = Color(0xFFB88A5A),
    val greenAccent: Color = Color(0xFF5B8C5A),

    // Glass / Dark UI Colors
    val glassBackground: Color = Color(0x26FFFFFF),
    val glassBorder: Color = Color(0x33FFFFFF),

    // Gradients
    val gradientStart: Color = Color(0xFF2C211B),
    val gradientMid: Color = Color(0xFF221E1C), // Added this
    val gradientEnd: Color = Color(0xFF1A1F1F),

    val surfaceCard: Color = Color(0xFF1E1E1E),
    val textPrimary: Color = Color(0xFFEADDCD),
    val textSecondary: Color = Color(0xFF9D8D7E),

    // Status
    val warning: Color = Color(0xFFFFA726) // Added this
)

// 2. Define Spacing
@Immutable
data class MindfulSpacing(
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp // Added this
)

// 3. Define Shapes
@Immutable
data class MindfulShapes(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val full: Dp = 999.dp
)

// 4. Create CompositionLocals
val LocalMindfulColors = staticCompositionLocalOf { MindfulColors() }
val LocalMindfulSpacing = staticCompositionLocalOf { MindfulSpacing() }
val LocalMindfulShapes = staticCompositionLocalOf { MindfulShapes() }

// 5. The Main Theme Object
object MindfulTheme {
    val colors: MindfulColors
        @Composable get() = LocalMindfulColors.current

    val spacing: MindfulSpacing
        @Composable get() = LocalMindfulSpacing.current

    val shapes: MindfulShapes
        @Composable get() = LocalMindfulShapes.current
}

// 6. The Theme Composable Function
@Composable
fun MindfulGrowthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val mindfulColors = MindfulColors()

    val colorScheme = darkColorScheme(
        primary = mindfulColors.goldPrimary,
        background = mindfulColors.gradientEnd,
        surface = mindfulColors.surfaceCard,
        onPrimary = Color.Black,
        onBackground = mindfulColors.textPrimary,
        onSurface = mindfulColors.textPrimary
    )

    CompositionLocalProvider(
        LocalMindfulColors provides mindfulColors,
        LocalMindfulSpacing provides MindfulSpacing(),
        LocalMindfulShapes provides MindfulShapes()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}