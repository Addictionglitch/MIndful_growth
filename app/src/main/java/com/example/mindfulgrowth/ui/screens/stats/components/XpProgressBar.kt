package com.example.mindfulgrowth.ui.screens.stats.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.model.RewardItem

@Composable
fun XpProgressBar(
    currentXp: Int,
    reward: RewardItem,
    modifier: Modifier = Modifier
) {
    // Calculate progress (0.0 to 1.0)
    val progress = remember(currentXp, reward.xpRequired) {
        (currentXp.toFloat() / reward.xpRequired.toFloat()).coerceIn(0f, 1f)
    }

    // Animate the bar filling up smoothly
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnimation")

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), // Dark theme background
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next Reward: ${reward.name}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // The Progress Bar
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFFBB86FC), // Your brand primary color
                trackColor = Color(0xFF333333),
            )

            // XP Counter Text
            Text(
                text = "$currentXp / ${reward.xpRequired} XP",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
