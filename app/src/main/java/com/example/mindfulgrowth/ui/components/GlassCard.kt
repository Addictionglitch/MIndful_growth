
package com.example.mindfulgrowth.ui.components

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.theme.LocalGlassStyle
import com.example.mindfulgrowth.ui.theme.MindfulPalette

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val glassStyle = LocalGlassStyle.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MindfulPalette.Glass.copy(alpha = glassStyle.backgroundAlpha))
            .glassmorphism(
                cornerRadius = cornerRadius,
                strokeWidth = glassStyle.rimStrokeWidth
            )
            .padding(16.dp)
    ) {
        content()
    }
}

private fun Modifier.glassmorphism(
    cornerRadius: Dp,
    strokeWidth: Dp
): Modifier = composed {
    val rimBrush = Brush.verticalGradient(
        colors = listOf(MindfulPalette.Rim, Color.Transparent),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    this.then(
        Modifier.drawBehind {
            // Rim light border
            drawRoundRect(
                brush = rimBrush,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx())
            )
        }
    )
}
