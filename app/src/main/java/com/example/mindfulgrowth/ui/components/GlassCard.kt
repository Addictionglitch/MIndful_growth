package com.example.mindfulgrowth.ui.components

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.MindfulTheme
import kotlin.math.pow
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode

/**
 * Premium glass morphism card with blur, bloom, and hover effects
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = MindfulTheme.shapes.large,
    blur: Boolean = true,
    blurRadius: Dp = 20.dp,
    bloom: Boolean = true,
    bloomIntensity: Float = 0.3f,
    elevation: Dp = MindfulTheme.elevation.sm,
    contentPadding: PaddingValues = PaddingValues(MindfulTheme.spacing.md),
    content: @Composable BoxScope.() -> Unit
) {
    val colors = MindfulTheme.colors
    val shape = RoundedCornerShape(cornerRadius)
    
    // Hover state animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else bloomIntensity,
        animationSpec = tween(MindfulTheme.animations.fast),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                
                // Apply blur on Android 12+
                if (blur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = BlurEffect(
                        blurRadius.toPx(),
                        blurRadius.toPx(),
                        TileMode.Clamp
                    )
                }
            }
            .then(
                if (bloom) {
                    Modifier.drawBehind {                        // Outer glow (bloom effect)
                        drawIntoCanvas { canvas ->
                            val paint = Paint().asFrameworkPaint().apply {
                                color = colors.bloomGlow.toArgb()
                                maskFilter = BlurMaskFilter(
                                    32.dp.toPx(),
                                    BlurMaskFilter.Blur.NORMAL
                                )
                            }
                            
                            canvas.nativeCanvas.drawRoundRect(
                                -8.dp.toPx(),
                                -8.dp.toPx(),
                                size.width + 8.dp.toPx(),
                                size.height + 8.dp.toPx(),
                                cornerRadius.toPx(),
                                cornerRadius.toPx(),
                                paint
                            )
                        }
                        
                        // Inner glow
                        drawRoundRect(
                            color = colors.bloomGlow.copy(alpha = glowAlpha),
                            size = size,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
                        )
                    }
                } else Modifier
            )
            .clip(shape)
            .background(colors.glassBackground)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.glassBorder.copy(alpha = 0.8f),
                        colors.glassBorder.copy(alpha = 0.3f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else Modifier
            )
    ) {
        // Top highlight stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colors.glassHighlight,
                            Color.Transparent
                        )
                    )
                )
        )
        
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Lightweight glass card variant for list items
 */
@Composable
fun GlassCardCompact(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    GlassCard(
        modifier = modifier.height(72.dp),
        onClick = onClick,
        cornerRadius = MindfulTheme.shapes.medium,
        blur = false,
        bloom = false,
        contentPadding = PaddingValues(
            horizontal = MindfulTheme.spacing.md,
            vertical = MindfulTheme.spacing.sm
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            content = content
        )
    }
}