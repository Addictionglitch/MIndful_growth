package com.example.mindfulgrowth.ui.navigation

import android.graphics.BlurMaskFilter
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas // FIXED: Added import
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

import com.example.mindfulgrowth.ui.screens.customize.CustomizeScreen
import com.example.mindfulgrowth.ui.screens.settings.SettingsScreen
import com.example.mindfulgrowth.ui.screens.stats.StatsScreen

// --- COLOR PALETTE ---
private val CrimsonCore = Color(0xFFFF0007)
private val VoidBlack = Color(0xFF050505)
private val GradientHighlight = CrimsonCore
private val GlassBorderTop = Color(0x50FFFFFF)
private val GlassBorderBottom = Color(0x15FFFFFF)

sealed class Screen(val index: Int, val title: String, val icon: ImageVector) {
    object Stats : Screen(0, "Stats", Icons.Rounded.BarChart)
    object Customize : Screen(1, "Customize", Icons.Rounded.Palette)
    object Settings : Screen(2, "Settings", Icons.Rounded.Settings)
}

@Composable
fun MindfulGrowthApp() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // 1. HORIZONTAL PAGER (Enables Swiping)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> StatsScreen()
                1 -> CustomizeScreen()
                2 -> SettingsScreen()
            }
        }

        // 2. NAV BAR CONTAINER WITH BLUR & FADE
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            VoidBlack.copy(alpha = 0.8f),
                            VoidBlack
                        )
                    )
                )
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = androidx.compose.ui.graphics.Paint()
                        paint.asFrameworkPaint().maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
                        canvas.drawRect(0f, 0f, size.width, size.height, androidx.compose.ui.graphics.Paint().apply {
                            asFrameworkPaint().apply {
                                color = android.graphics.Color.TRANSPARENT
                                maskFilter = BlurMaskFilter(60f, BlurMaskFilter.Blur.NORMAL)
                            }
                        })
                    }
                }
        ) {
            LiquidGlassBottomNavigation(
                pagerState = pagerState,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp, start = 25.dp, end = 25.dp)
            )
        }
    }
}

@Composable
private fun LiquidGlassBottomNavigation(
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(Screen.Stats, Screen.Customize, Screen.Settings)

    // FIXED: Changed to BoxWithConstraints to correctly use maxWidth
    BoxWithConstraints(
        modifier = modifier
            .height(68.dp) // FIXED: Made 2px/4dp skinnier (72dp -> 68dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
    ) {
        val width = maxWidth
        val tabWidth = width / items.size

        // --- BACKGROUND LAYERS ---
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(GlassBorderTop, Color.Transparent, GlassBorderBottom)
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
        )

        // --- LIQUID MORPHING INDICATOR ---
        LiquidIndicator(
            pagerState = pagerState,
            tabWidth = tabWidth,
            totalHeight = 68.dp // Match container height
        )

        // --- ICON LAYER ---
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, screen ->
                val isSelected = pagerState.currentPage == index
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(tabWidth)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) GradientHighlight else Color.White.copy(alpha = 0.4f),
                        animationSpec = tween(300),
                        label = "Color"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.2f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
                        label = "Scale"
                    )

                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = iconColor,
                        modifier = Modifier
                            .size(28.dp)
                            .scale(scale)
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidIndicator(
    pagerState: PagerState,
    tabWidth: Dp,
    totalHeight: Dp
) {
    val density = LocalDensity.current

    val currentOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val tabWidthPx = with(density) { tabWidth.toPx() }
    val heightPx = with(density) { totalHeight.toPx() }

    val stretchFactor = 1f + (0.4f * (1f - (pagerState.currentPageOffsetFraction.absoluteValue - 0.5f).absoluteValue * 2))

    val blobWidth = 60.dp
    val blobWidthPx = with(density) { blobWidth.toPx() } * stretchFactor

    val indicatorCenterX = (currentOffset * tabWidthPx) + (tabWidthPx / 2)

    // FIXED: Using Canvas composable to resolve DrawScope
    Canvas(modifier = Modifier.fillMaxSize()) {
        val left = indicatorCenterX - (blobWidthPx / 2)
        val top = (heightPx / 2) - (blobWidthPx / 2)

        // 1. Inner Glow
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    GradientHighlight.copy(alpha = 0.15f),
                    CrimsonCore.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(indicatorCenterX, heightPx/2),
                radius = blobWidthPx
            ),
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(blobWidthPx, blobWidthPx),
            cornerRadius = CornerRadius(blobWidthPx / 2)
        )

        // 2. Glass Border
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color.White.copy(alpha = 0.05f),
                    Color.White.copy(alpha = 0.2f)
                ),
                start = Offset(left, top),
                end = Offset(left + blobWidthPx, top + blobWidthPx)
            ),
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(blobWidthPx, blobWidthPx),
            cornerRadius = CornerRadius(blobWidthPx / 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )
    }
}