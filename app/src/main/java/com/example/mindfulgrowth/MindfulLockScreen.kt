package com.example.mindfulgrowth

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import com.example.mindfulgrowth.model.defaultTrees
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// --- MAIN ENTRY POINT ---
@Composable
fun MindfulLockScreen(
    timerProgress: Float = 0.65f, // 0.0 to 1.0
    currentTime: LocalDateTime = LocalDateTime.now(),
    isAmbient: Boolean = false,
    treeStages: List<Int> = defaultTrees.first().stageResIds
) {
    // 1. Calculate Growth Index (0 to 4)
    val stageIndex = (timerProgress * 5).toInt().coerceIn(0, 4)
    val currentDrawable = treeStages[stageIndex]

    // 2. Battery Protection: ALWAYS Pure Black for AOD efficiency
    val bgColor = Color.Black
    // Dim text slightly in ambient mode to prevent burn-in/glare
    val textColor = if (isAmbient) Color.Gray else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 64.dp), // Vertical padding for spacing
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Pushes content to Top, Center, Bottom
        ) {

            // --- TOP: CLOCK (Moved Here) ---
            ClockContent(currentTime, textColor)

            // --- CENTER: TREE ONLY (No Ring) ---
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = currentDrawable),
                    contentDescription = "Growth Stage ${stageIndex + 1}",
                    modifier = Modifier
                        .size(670.dp) // <--- CHANGE TREE SIZE HERE (Increase/Decrease)
                )
            }

            // --- BOTTOM: STATUS (Moved Here) ---
            // Only show the frosted card if NOT in ambient mode (save pixels)
            // Or show simple text if you want it visible in AOD too.
            if (!isAmbient) {
                FrostedGlassCard(
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "🌱 Focus Active",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else {
                // In AOD, just show simple text or nothing to save battery
                Text(
                    text = "Focus Active",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --- COMPONENT: Clock Content ---
@Composable
fun ClockContent(currentTime: LocalDateTime, textColor: Color) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = timeFormatter.format(currentTime),
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 86.sp,
                fontWeight = FontWeight.Thin,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-2).sp
            ),
            color = textColor,
            modifier = Modifier.graphicsLayer { alpha = 0.95f }
        )
        Text(
            text = dateFormatter.format(currentTime).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = textColor.copy(alpha = 0.6f),
            letterSpacing = 2.sp
        )
    }
}

// --- COMPONENT: Real Frosted Glass (Android 12+) ---
@Composable
fun FrostedGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White.copy(alpha = 0.06f)) // subtle tint
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.06f)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
    ) {
        // Apply a very small native blur only to the background layer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(modifier = Modifier.matchParentSize().graphicsLayer {
                val blur = RenderEffect.createBlurEffect(4f, 4f, Shader.TileMode.CLAMP)
                renderEffect = blur.asComposeRenderEffect()
            })
        }
        content()
    }
}