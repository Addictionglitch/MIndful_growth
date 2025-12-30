package com.example.mindfulgrowth.ui.screens.customization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mindfulgrowth.model.RewardItem

@Composable
fun UnlockableItem(
    item: RewardItem,
    currentXp: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isUnlocked = item.isUnlocked(currentXp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFBB86FC) else Color(0xFF2C2C2C))
                .clickable(enabled = isUnlocked) { onSelect() },
            contentAlignment = Alignment.Center
        ) {
            if (isUnlocked) {
                // Render the Actual Item Preview here (e.g., Icon or Color swatch)
                Text(text = item.name.first().toString(), color = Color.White)
            } else {
                // Render Locked State
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                )
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isUnlocked) Color.White else Color.Gray
        )

        if (!isUnlocked) {
            Text(
                text = "${item.xpRequired} XP",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFBB86FC)
            )
        }
    }
}
