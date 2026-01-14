
package com.example.mindfulgrowth.ui.screens.ranked

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.NeonCyan
import com.example.mindfulgrowth.ui.theme.NeonGreen

enum class RankTier(val color: Color) {
    IRON(Color.Gray.copy(alpha = 0.6f)),
    GOLD(Color(0xFFFFD700)),
    DIAMOND(NeonCyan),
    VOID(Color(0xFFE040FB))
}

@Composable
fun RankBadge(tier: RankTier, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val width = size.width
        val height = size.height
        val path = Path()

        when (tier) {
            RankTier.IRON -> {
                path.moveTo(width / 2, 0f)
                path.lineTo(width, height)
                path.lineTo(0f, height)
                path.close()
            }
            RankTier.GOLD -> {
                path.moveTo(width / 2, 0f)
                path.lineTo(width, height / 2)
                path.lineTo(width / 2, height)
                path.lineTo(0f, height / 2)
                path.close()
            }
            RankTier.DIAMOND -> {
                val hexSize = width / 2
                path.moveTo(hexSize, 0f)
                path.lineTo(width, height * 0.25f)
                path.lineTo(width, height * 0.75f)
                path.lineTo(hexSize, height)
                path.lineTo(0f, height * 0.75f)
                path.lineTo(0f, height * 0.25f)
                path.close()
            }
            RankTier.VOID -> {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(tier.color, Color.Transparent)
                    ),
                    radius = width / 2
                )
            }
        }
        drawPath(path, color = tier.color, style = Stroke(width = 3.dp.toPx()))
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    name: String,
    tier: RankTier,
    focusTime: String,
    isTopThree: Boolean
) {
    val topThreeBrush = when (rank) {
        1 -> Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFF0F0F0)))
        2 -> Brush.linearGradient(listOf(Color(0xFFC0C0C0), Color(0xFFE0E0E0)))
        3 -> Brush.linearGradient(listOf(Color(0xFFCD7F32), Color(0xFFE0E0E0)))
        else -> null
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = "#$rank",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(end = 16.dp)
                )
                RankBadge(tier = tier)
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp),
                    style = if (topThreeBrush != null) MaterialTheme.typography.bodyLarge.copy(
                        brush = topThreeBrush
                    ) else LocalTextStyle.current
                )
            }
            Text(
                text = focusTime,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                color = NeonGreen
            )
        }
    }
}

@Composable
fun UserPinnedCard(rank: Int, pointsToNext: Int) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        cornerRadius = 24.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("YOUR RANK", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text("#$rank", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NEXT RANK", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text("$pointsToNext MINS", color = NeonCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
