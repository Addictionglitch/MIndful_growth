package com.example.mindfulgrowth.ui.theme

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Extended Color Palette
@Immutable
data class MindfulColors(
    val goldPrimary: Color = Color(0xFFC69C6D),
    val goldLight: Color = Color(0xFFD4B896),
    val goldDark: Color = Color(0xFFB88A5A),
    val greenAccent: Color = Color(0xFF5B8C5A),
    val greenLight: Color = Color(0xFF7DAA7C),
    val greenDark: Color = Color(0xFF3A5A39),
    
    // Glass morphism colors
    val glassBackground: Color = Color(0x26FFFFFF),
    val glassBorder: Color = Color(0x33FFFFFF),
    val glassHighlight: Color = Color(0x0DFFFFFF),
    
    // Gradient colors
    val gradientStart: Color = Color(0xFF2C211B),
    val gradientMid: Color = Color(0xFF221E1C),
    val gradientEnd: Color = Color(0xFF1A1F1F),
    
    // Surface colors
    val surfaceCard: Color = Color(0xFF1E1E1E),
    val surfaceElevated: Color = Color(0xFF252525),
    val surfaceOverlay: Color = Color(0x40000000),
    
    // Text colors
    val textPrimary: Color = Color(0xFFEADDCD),
    val textSecondary: Color = Color(0xFF9D8D7E),
    val textTertiary: Color = Color(0xFF6B6B6B),
    val textGold: Color = Color(0xFFC69C6D),
    
    // Status colors
    val success: Color = Color(0xFF4CAF50),
    val warning: Color = Color(0xFFFFA726),
    val error: Color = Color(0xFFE57373),
    
    // Shimmer/Bloom
    val bloomGlow: Color = Color(0x33C69C6D),
    val shimmer: Color = Color(0x1AFFFFFF)
)

// Spacing System
@Immutable
data class MindfulSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp
)

// Elevation System
@Immutable
data class MindfulElevation(
    val none: Dp = 0.dp,
    val xs: Dp = 2.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp
)

// Corner Radius System
@Immutable
data class MindfulShapes(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val full: Dp = 999.dp
)

// Animation Durations
@Immutable
data class MindfulAnimations(
    val fast: Int = 150,
    val normal: Int = 300,
    val slow: Int = 500,
    val verySlow: Int = 800,
    
    val defaultEasing: androidx.compose.animation.core.Easing = FastOutSlowInEasing
)

// Composition Locals
val LocalMindfulColors = staticCompositionLocalOf { MindfulColors() }
val LocalMindfulSpacing = staticCompositionLocalOf { MindfulSpacing() }
val LocalMindfulElevation = staticCompositionLocalOf { MindfulElevation() }
val LocalMindfulShapes = staticCompositionLocalOf { MindfulShapes() }
val LocalMindfulAnimations = staticCompositionLocalOf { MindfulAnimations() }

object MindfulTheme {
    val colors: MindfulColors
        @Composable get() = LocalMindfulColors.current
    
    val spacing: MindfulSpacing
        @Composable get() = LocalMindfulSpacing.current
    
    val elevation: MindfulElevation
        @Composable get() = LocalMindfulElevation.current
    
    val shapes: MindfulShapes
        @Composable get() = LocalMindfulShapes.current
    
    val animations: MindfulAnimations
        @Composable get() = LocalMindfulAnimations.current
}

@Composable
fun MindfulGrowthTheme(
    darkTheme: Boolean = true, // App is dark-only
    dynamicColor: Boolean = false, // Disabled for consistent branding
    content: @Composable () -> Unit
) {
    val mindfulColors = MindfulColors()
    
    val colorScheme = darkColorScheme(
        primary = mindfulColors.goldPrimary,
        onPrimary = Color(0xFF121212),
        primaryContainer = mindfulColors.goldDark,
        onPrimaryContainer = mindfulColors.textPrimary,
        
        secondary = mindfulColors.greenAccent,
        onSecondary = Color.White,
        secondaryContainer = mindfulColors.greenDark,
        onSecondaryContainer = mindfulColors.textPrimary,
        
        background = mindfulColors.gradientEnd,
        onBackground = mindfulColors.textPrimary,
        
        surface = mindfulColors.surfaceCard,
        onSurface = mindfulColors.textPrimary,
        surfaceVariant = mindfulColors.surfaceElevated,
        onSurfaceVariant = mindfulColors.textSecondary,
        
        error = mindfulColors.error,
        onError = Color.White
    )

    val typography = Typography(
        displayLarge = Typography().displayLarge.copy(color = mindfulColors.textPrimary),
        displayMedium = Typography().displayMedium.copy(color = mindfulColors.textPrimary),
        displaySmall = Typography().displaySmall.copy(color = mindfulColors.textPrimary),
        
        headlineLarge = Typography().headlineLarge.copy(color = mindfulColors.textPrimary),
        headlineMedium = Typography().headlineMedium.copy(color = mindfulColors.textPrimary),
        headlineSmall = Typography().headlineSmall.copy(color = mindfulColors.textPrimary),
        
        titleLarge = Typography().titleLarge.copy(color = mindfulColors.textPrimary),
        titleMedium = Typography().titleMedium.copy(color = mindfulColors.textPrimary),
        titleSmall = Typography().titleSmall.copy(color = mindfulColors.textPrimary),
        
        bodyLarge = Typography().bodyLarge.copy(color = mindfulColors.textPrimary),
        bodyMedium = Typography().bodyMedium.copy(color = mindfulColors.textSecondary),
        bodySmall = Typography().bodySmall.copy(color = mindfulColors.textSecondary),
        
        labelLarge = Typography().labelLarge.copy(color = mindfulColors.textSecondary),
        labelMedium = Typography().labelMedium.copy(color = mindfulColors.textSecondary),
        labelSmall = Typography().labelSmall.copy(color = mindfulColors.textTertiary)
    )

    CompositionLocalProvider(
        LocalMindfulColors provides mindfulColors,
        LocalMindfulSpacing provides MindfulSpacing(),
        LocalMindfulElevation provides MindfulElevation(),
        LocalMindfulShapes provides MindfulShapes(),
        LocalMindfulAnimations provides MindfulAnimations()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}