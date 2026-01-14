
package com.example.mindfulgrowth.ui.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.NeonCyan

@Composable
fun FeedPostCard(post: Post) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        cornerRadius = 24.dp
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray) // Avatar Placeholder
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = post.username,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = post.actionText,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            // Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.DarkGray.copy(alpha = 0.5f))
            )

            // Caption & Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.caption,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { /* TODO: Handle Resonance */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = "Resonance",
                        tint = NeonCyan
                    )
                }
            }
        }
    }
}
