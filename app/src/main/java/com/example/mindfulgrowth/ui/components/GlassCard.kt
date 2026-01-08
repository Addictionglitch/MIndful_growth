package com.example.mindfulgrowth.ui.components

import android.os.Build
import android.graphics.RenderEffect
import android.graphics.Shader
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.min
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import com.example.mindfulgrowth.ui.theme.spacing

/**
 * Premium glass morphism card with blur, bloom, and hover effects
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 16.dp,
    blur: Boolean = true,
    blurRadius: Dp = 2.dp,             // subtle glass blur (max 2.dp)
    bloom: Boolean = true,
    bloomIntensity: Float = 0.06f,     // very subtle bloom by default
    contentPadding: PaddingValues = PaddingValues(spacing.medium),
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    // Hover state animation
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        // faster, less bouncy press animation
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.6f else bloomIntensity,
        animationSpec = tween(160),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .then(
                // Lightweight bloom: avoid native mask filters entirely to prevent large smeared artifacts.
                if (bloom) {
                    Modifier.drawBehind {
                        // Only draw when visible
                        if (glowAlpha <= 0.02f && !isPressed) return@drawBehind

                        // Subtle vertical gradient overlay for warm tint
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(SystemConfigColors.ACCENT_RED_PRIMARY).copy(alpha = glowAlpha * 0.06f),
                                    Color.Transparent
                                )
                            ),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )

                        // Inner tint to give warm accent
                        drawRoundRect(
                            color = Color(SystemConfigColors.ACCENT_RED_PRIMARY).copy(alpha = glowAlpha * 0.12f),
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                } else Modifier
            )
            .clip(shape)
            // We will render the blurred background as a separate layer inside the card to avoid
            // blurring child content. The actual background color/texture will be drawn below.
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(SystemConfigColors.GLASS_BORDER).copy(alpha = 0.8f),
                        Color(SystemConfigColors.GLASS_BORDER).copy(alpha = 0.3f)
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
        // Background layer: blurred glass + tint. This is drawn before content so children stay sharp.
        if (blur) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Box(modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            val r = RenderEffect.createBlurEffect(
                                blurRadius.toPx(), blurRadius.toPx(), Shader.TileMode.CLAMP
                            )
                            renderEffect = r.asComposeRenderEffect()
                        }
                    )
                }

                // Glass tint overlay
                Box(modifier = Modifier
                    .matchParentSize()
                    .background(Color(SystemConfigColors.GLASS_PRIMARY))
                )
            }
        }
         // Top highlight stripe
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .height(1.dp)
                 .background(
                     Brush.horizontalGradient(
                         colors = listOf(
                             Color.Transparent,
                             Color.White.copy(alpha = 0.1f),
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
        cornerRadius = 8.dp,
        blur = false,
        bloom = false,
        contentPadding = PaddingValues(
            horizontal = spacing.medium,
            vertical = spacing.small
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
