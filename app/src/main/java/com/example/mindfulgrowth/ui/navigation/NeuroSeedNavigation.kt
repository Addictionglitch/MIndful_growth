
package com.example.mindfulgrowth.ui.navigation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public // Import for Feed icon
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material.icons.rounded.Yard
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.MindfulPalette // Import MindfulPalette

private enum class SatelliteState { Collapsed, Expanded }

private data class SatelliteMenuItem(val icon: ImageVector, val targetPage: Int)

@Composable
fun NeuroSeedNavigation(
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val transition = updateTransition(
        targetState = if (isMenuOpen) SatelliteState.Expanded else SatelliteState.Collapsed,
        label = "MenuTransition"
    )

    // Updated menu items: Home (central), Feed, Stats, Garden
    val menuItems = listOf(
        SatelliteMenuItem(Icons.Default.Public, 1), // Feed
        SatelliteMenuItem(Icons.Rounded.QueryStats, 2), // Stats
        SatelliteMenuItem(Icons.Rounded.Yard, 3)       // Garden
    )

    // Breathing animation for the central anchor
    val infiniteTransition = rememberInfiniteTransition(label = "BreathingAnchor")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "breathingScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Satellite Buttons
        menuItems.forEachIndexed { index, item ->
            val targetOffset = when (index) {
                0 -> Offset(-80f, -40f) // Left - Feed
                1 -> Offset(0f, -90f)   // Top - Stats
                else -> Offset(80f, -40f) // Right - Garden
            }

            val satelliteOffset by transition.animateOffset(
                transitionSpec = { spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium) },
                label = "SatelliteOffset$index"
            ) { state ->
                if (state == SatelliteState.Expanded) targetOffset else Offset.Zero
            }

            val satelliteAlpha by transition.animateFloat(
                transitionSpec = { tween(durationMillis = 200) },
                label = "SatelliteAlpha$index"
            ) { state ->
                if (state == SatelliteState.Expanded) 1f else 0f
            }

            GlassCard(
                modifier = Modifier
                    .offset(satelliteOffset.x.dp, satelliteOffset.y.dp)
                    .scale(satelliteAlpha)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onTabSelected(item.targetPage)
                            isMenuOpen = false
                        }
                    },
                shape = CircleShape // Replaced cornerRadius with shape
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp).size(24.dp)
                )
            }
        }

        // The Central Anchor (Seed) - Assumed to be Home
        GlassCard(
            modifier = Modifier
                .size(64.dp)
                .scale(if (isMenuOpen) 1.1f else breathingScale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isMenuOpen = !isMenuOpen
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        when {
                            dragAmount.x < -10 -> { // Swipe Left to Stats
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(2)
                            }
                            dragAmount.x > 10 -> { // Swipe Right to Garden
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onTabSelected(3)
                            }
                        }
                    }
                },
            shape = CircleShape // Replaced cornerRadius with shape
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(MindfulPalette.TextHigh.copy(0.3f), Color.Transparent),
                    ),
                    radius = size.minDimension / 2.0f
                )
            }
        }
    }
}
