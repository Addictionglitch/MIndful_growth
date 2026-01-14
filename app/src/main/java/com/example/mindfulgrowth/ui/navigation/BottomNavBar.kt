
package com.example.mindfulgrowth.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlin.math.abs

data class NavItem(
    val route: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LiquidGlassBottomNavigation(
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem(Screen.Home.route, Icons.Rounded.Home),
        NavItem(Screen.Feed.route, Icons.Rounded.RssFeed),
        NavItem(Screen.Stats.route, Icons.Rounded.QueryStats),
        NavItem(Screen.Profile.route, Icons.Rounded.Person) // Was Garden/Yard
    )

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize() 
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF080808))
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val tabWidth = maxWidth / items.size

                val indicatorX by animateDpAsState(
                    targetValue = (tabWidth * pagerState.currentPage) + (tabWidth * pagerState.currentPageOffsetFraction),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "IndicatorX"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val indicatorHeight = size.height - (4.dp.toPx() * 2)
                    val indicatorPadding = (size.height - indicatorHeight) / 2

                    drawRoundRect(
                        color = Color(0xFFE0E0E0),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            indicatorX.toPx() + indicatorPadding,
                            indicatorPadding
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            tabWidth.toPx() - (indicatorPadding * 2),
                            indicatorHeight
                        ),
                        cornerRadius = CornerRadius(50.dp.toPx())
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        val selectionFraction = 1f - (abs(pagerState.currentPage - index + pagerState.currentPageOffsetFraction)).coerceIn(0f, 1f)
                        val iconColor by animateColorAsState(
                            targetValue = lerp(Color.Gray, Color.Black, selectionFraction),
                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                            label = "IconColor"
                        )

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.route,
                            tint = iconColor,
                            modifier = Modifier
                                .size(24.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onTabSelected(index)
                                        }
                                    )
                                }
                        )
                    }
                }
            }
        }
    }
}
