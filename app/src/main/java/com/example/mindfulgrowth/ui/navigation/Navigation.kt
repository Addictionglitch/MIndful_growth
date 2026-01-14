
package com.example.mindfulgrowth.ui.navigation

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material.icons.filled.Settings // Import for Settings icon
import androidx.compose.material.icons.filled.Info // Added import
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // Added import if not present implicitly
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf // Import for mutableStateOf
import androidx.compose.runtime.setValue // Import for setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily // Added import
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.ui.feed.FeedScreen
import com.example.mindfulgrowth.ui.screens.GardenScreen
import com.example.mindfulgrowth.ui.screens.home.HomeScreen
import com.example.mindfulgrowth.ui.screens.stats.StatsScreen
import com.example.mindfulgrowth.ui.theme.MindfulPalette
import com.example.mindfulgrowth.ui.components.ModalBottomSheet // Import ModalBottomSheet
import com.example.mindfulgrowth.ui.components.GlassCard // Import GlassCard (retained if used elsewhere, but not for header)
import com.example.mindfulgrowth.ui.screens.settings.SettingsScreen // Import SettingsScreen
import kotlinx.coroutines.launch

// --- Constants & Configuration ---
private val DockGlassColor = Color(0xFF0A0A0A).copy(alpha = 0.85f)
private val DockShape = RoundedCornerShape(32.dp)
private val GhostLightIndicatorSize = 80.dp

// --- Data Model for Navigation ---
sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Default.Home, "Home")
    object Feed : NavigationItem("feed", Icons.Default.Public, "Feed")
    object Stats : NavigationItem("stats", Icons.Default.BarChart, "Stats")
    object Garden : NavigationItem("garden", Icons.Default.Yard, "Garden")
}

@Composable
fun MindfulGrowthApp() {
    val navItems = listOf(NavigationItem.Home, NavigationItem.Feed, NavigationItem.Stats, NavigationItem.Garden)
    val pagerState = rememberPagerState(pageCount = { navItems.size })
    val coroutineScope = rememberCoroutineScope()
    var showSettings by remember { mutableStateOf(false) } // State for showing settings modal

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // MainActivity provides the gradient
    ) {
        Column(modifier = Modifier.fillMaxSize()) { // New Column to stack header and pager
            // --- NEW: Overarching Heading ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black) // Pure black background
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Padding for visual spacing
                    .windowInsetsPadding(WindowInsets.statusBars), // Respect status bar
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showSettings = true }) { // Settings button
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
                Text(
                    text = "Mindful Growth", // App Title
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Monospace
                )
                IconButton(onClick = { /* TODO: Handle Info click */ }) { // Info button
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
                }
            }

            // --- Content Pager (now takes remaining height) ---
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f) // Takes remaining vertical space
            ) { page ->
                when (navItems[page]) {
                    NavigationItem.Home -> HomeScreen()
                    NavigationItem.Feed -> FeedScreen()
                    NavigationItem.Stats -> StatsScreen()
                    NavigationItem.Garden -> GardenScreen()
                }
            }
        } // End of Column

        // --- OLED Glass Navigation Dock --- (Remains at bottom of the Box)
        LiquidGlassBottomNavigation(
            navItems = navItems,
            pagerState = pagerState,
            onTabSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // --- Settings Modal Bottom Sheet ---
        ModalBottomSheet(
            visible = showSettings,
            onDismiss = { showSettings = false }
        ) {
            SettingsScreen(onClose = { showSettings = false })
        }
    }
}

@Composable
private fun LiquidGlassBottomNavigation(
    navItems: List<NavigationItem>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
    ) {
        val tabWidth = maxWidth / navItems.size

        val indicatorOffset by animateDpAsState(
            targetValue = (tabWidth * pagerState.currentPage) + (tabWidth / 2) - (GhostLightIndicatorSize / 2),
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
            label = "IndicatorOffset"
        )

        Canvas(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .size(GhostLightIndicatorSize)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(MindfulPalette.NeonGreen.copy(alpha = 0.3f), Color.Transparent),
                ),
                radius = size.minDimension / 2.0f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .glassmorphism()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(0.1f), Color.White.copy(0.02f))
                    ),
                    shape = DockShape
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = pagerState.currentPage == index
                DockIcon(
                    item = item,
                    isSelected = isSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun DockIcon(
    item: NavigationItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "IconScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MindfulPalette.NeonGreen else Color.White.copy(alpha = 0.4f),
        animationSpec = spring(),
        label = "IconColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .scale(scale)
                .shadow(
                    elevation = if (isSelected) 8.dp else 0.dp,
                    spotColor = MindfulPalette.NeonGreen,
                    shape = RoundedCornerShape(24.dp)
                )
        )
    }
}

private fun Modifier.glassmorphism(): Modifier = composed {
    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.graphicsLayer {
            renderEffect = RenderEffect.createBlurEffect(
                20f, 20f, Shader.TileMode.DECAL
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    this
        .clip(DockShape)
        .then(glassModifier)
        .background(DockGlassColor)
}
