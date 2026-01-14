package com.example.mindfulgrowth.ui.feed

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.rounded.RemoveRedEye
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import com.example.mindfulgrowth.ui.theme.MindfulPalette // Import MindfulPalette

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindfulPalette.DarkPurple)
    ) {
        // Custom Glass Top Bar
        GlassTopBar(
            globalActiveUserCount = uiState.globalActiveUserCount,
            userTokenBalance = uiState.userTokenBalance
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.posts) { post ->
                FeedPostCard(
                    post = post,
                    onBoostClick = viewModel::onBoostClick
                )
            }
        }
    }
}

@Composable
fun GlassTopBar(
    globalActiveUserCount: Int,
    userTokenBalance: Int
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp, 8.dp, 16.dp, 8.dp),
        shape = RoundedCornerShape(16.dp) // Changed from cornerRadius
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mindful Growth",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Global Pulse
                Icon(
                    imageVector = Icons.Rounded.RemoveRedEye,
                    contentDescription = "Global Pulse",
                    tint = MindfulPalette.NeonGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "$globalActiveUserCount Online",
                    color = Color.White,
                    fontSize = 14.sp
                )

                // Token Counter
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = "Tokens",
                    tint = Color.Yellow,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "$userTokenBalance",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FeedPostCard(
    post: FeedPost,
    onBoostClick: (String) -> Unit
) {
    var isAnimatingBoost by remember { mutableStateOf(false) }

    val boostScale by animateFloatAsState(
        targetValue = if (isAnimatingBoost) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        label = "boostScaleAnimation"
    ) {
        if (it == 1.2f) { // Only reset after scaling up
            isAnimatingBoost = false
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp) // Changed from cornerRadius
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${post.username} focused for ${post.duration}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Placeholder Image (Scanline effect)
                Box(
                    modifier = Modifier
                        .size(80.dp, 40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Gray.copy(alpha = 0.3f), Color.DarkGray.copy(alpha = 0.5f)),
                                startY = 0f,
                                endY = 40.dp.value
                            )
                        )
                ) {
                    // Scanline effect
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val brush = SolidColor(Color.White.copy(alpha = 0.1f))
                        val lineCount = 10
                        val lineHeight = size.height / lineCount
                        for (i in 0 until lineCount) {
                            drawLine(
                                brush = brush,
                                start = Offset(0f, i * lineHeight),
                                end = Offset(size.width, i * lineHeight),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                }
            }

            // Boost Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (!post.isBoostedByMe) { // Only allow boosting if not already boosted by me
                            isAnimatingBoost = true
                            onBoostClick(post.id)
                        }
                    },
                    modifier = Modifier.scale(boostScale)
                ) {
                    val icon = if (post.isBoostedByMe) Icons.Filled.ElectricBolt else Icons.Outlined.ElectricBolt
                    val tint = if (post.isBoostedByMe) MindfulPalette.NeonGreen else Color.White
                    Icon(
                        imageVector = icon,
                        contentDescription = "Boost Post",
                        tint = tint,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "${post.boostCount}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedScreen() {
    MindfulGrowthTheme {
        FeedScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGlassTopBar() {
    MindfulGrowthTheme {
        GlassTopBar(globalActiveUserCount = 1245, userTokenBalance = 50)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFeedPostCard() {
    MindfulGrowthTheme {
        FeedPostCard(
            post = FeedPost("1", "User_X", "45m", 12, false),
            onBoostClick = {}
        )
    }
}
