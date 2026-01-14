
package com.example.mindfulgrowth.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.mindfulgrowth.ui.theme.SystemConfigColors
import com.example.mindfulgrowth.ui.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun TooltipPopup(
    text: String,
    visible: Boolean,
    offset: DpOffset = DpOffset(0.dp, (-40).dp),
    autoDismissDelay: Long = 3000,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (visible) {
        LaunchedEffect(Unit) {
            delay(autoDismissDelay)
            onDismiss()
        }
    }
    val mediumSpacing = spacing.medium
    val smallSpacing = spacing.small

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            transformOrigin = TransformOrigin(0.5f, 1f),
            animationSpec = spring()
        ),
        exit = fadeOut() + scaleOut(
            transformOrigin = TransformOrigin(0.5f, 1f)
        )
    ) {
        val intOffset = with(LocalDensity.current) {
            IntOffset(offset.x.roundToPx(), offset.y.roundToPx())
        }
        Popup(
            alignment = Alignment.Center,
            offset = intOffset
        ) {
            GlassCard(
                modifier = modifier,
                cornerRadius = 4.dp
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(SystemConfigColors.TEXT_PRIMARY),
                    modifier = Modifier.padding(
                        horizontal = mediumSpacing,
                        vertical = smallSpacing
                    )
                )
            }
        }
    }
}
