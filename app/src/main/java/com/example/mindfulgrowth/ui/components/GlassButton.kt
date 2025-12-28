package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.theme.MindfulTheme

enum class GlassButtonStyle {
    PRIMARY,    // Gold filled
    SECONDARY,  // Green filled
    OUTLINED,   // Transparent with border
    GLASS       // Full glass effect
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.PRIMARY,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    cornerRadius: Dp = MindfulTheme.shapes.medium
) {
    val colors = MindfulTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Press animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )
    
    // Shimmer effect for PRIMARY style
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    
    val shape = RoundedCornerShape(cornerRadius)
    
    val (bgColor, contentColor, borderColor) = when (style) {
        GlassButtonStyle.PRIMARY -> Triple(
            colors.goldPrimary.copy(alpha = if (enabled) 1f else 0.5f),
            Color(0xFF121212),
            Color.Transparent
        )
        GlassButtonStyle.SECONDARY -> Triple(
            colors.greenAccent.copy(alpha = if (enabled) 1f else 0.5f),
            Color.White,
            Color.Transparent
        )
        GlassButtonStyle.OUTLINED -> Triple(
            Color.Transparent,
            colors.goldPrimary.copy(alpha = if (enabled) 1f else 0.5f),
            colors.goldPrimary.copy(alpha = if (enabled) 1f else 0.3f)
        )
        GlassButtonStyle.GLASS -> Triple(
            colors.glassBackground,
            colors.textPrimary.copy(alpha = if (enabled) 1f else 0.5f),
            colors.glassBorder
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(56.dp)
            .clip(shape)
            .then(
                if (style == GlassButtonStyle.PRIMARY && enabled) {
                    Modifier.drawBehind {                        // Animated shimmer overlay
                        val shimmerX = size.width * shimmerOffset
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                startX = shimmerX - 100f,
                                endX = shimmerX + 100f
                            )
                        )
                    }
                } else Modifier
            )
            .background(bgColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, shape)
                } else Modifier
            )
            .clickable(
                onClick = onClick,
                enabled = enabled && !isLoading,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null
            )
            .padding(horizontal = MindfulTheme.spacing.lg),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            LoadingIndicator(color = contentColor)
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(MindfulTheme.spacing.sm))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "loadingRotation"
    )
    
    Box(
        modifier = modifier
            .size(24.dp)
            .graphicsLayer { rotationZ = rotation }
            .drawBehind {
                drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = size.minDimension / 2
                )
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }
    )
}

/**
 * Segmented control for Settings (Tap/Double Tap selector)
 */
@Composable
fun GlassSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MindfulTheme.colors
    val animatedOffset by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "segmentOffset"
    )
    
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(MindfulTheme.shapes.medium))
            .background(colors.glassBackground)
            .border(1.dp, colors.glassBorder, RoundedCornerShape(MindfulTheme.shapes.medium))
    ) {
        // Animated selector background
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f / options.size)
                .offset(x = (animatedOffset * (1f / options.size) * 100).dp * options.size)
                .padding(4.dp)
                .clip(RoundedCornerShape(MindfulTheme.shapes.small))
                .background(colors.goldPrimary)
        )
        
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectionChange(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (index == selectedIndex) {
                            Color(0xFF121212)
                        } else {
                            colors.textSecondary
                        }
                    )
                }
            }
        }
    }
}