package com.example.mindfulgrowth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.theme.SystemConfigColors

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // We accept Ints from SystemConfigColors to match your config
    textColor: Int = SystemConfigColors.TEXT_PRIMARY,
    backgroundColor: Int = SystemConfigColors.GLASS_PRIMARY,
    borderColor: Int = SystemConfigColors.GLASS_BORDER
) {
    // FIX: Convert Int to Color immediately
    val safeTextColor = Color(textColor)
    val safeBackgroundColor = Color(backgroundColor)
    val safeBorderColor = Color(borderColor)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            // Use safeBackgroundColor (Color) instead of the Int
            .background(safeBackgroundColor.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        safeBorderColor.copy(alpha = 0.5f),
                        safeBorderColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = safeTextColor, // Use safeTextColor (Color)
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// Based on your errors around line 212, this file also contains a Chip/Tag component.
// I have included the fix for that here as well.
@Composable
fun GlassChip(
    text: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Int = SystemConfigColors.ACCENT_RED_PRIMARY,
    inactiveColor: Int = SystemConfigColors.GLASS_SECONDARY,
    textColor: Int = SystemConfigColors.TEXT_PRIMARY
) {
    // FIX: Convert Int to Color immediately
    val safeActiveColor = Color(activeColor)
    val safeInactiveColor = Color(inactiveColor)
    val safeTextColor = Color(textColor)

    val backgroundColor = if (selected) safeActiveColor else safeInactiveColor
    val borderBrush = if (selected) {
        Brush.verticalGradient(
            listOf(
                safeActiveColor.copy(alpha = 0.8f),
                safeActiveColor.copy(alpha = 0.2f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(SystemConfigColors.GLASS_BORDER).copy(alpha = 0.3f),
                Color(SystemConfigColors.GLASS_BORDER).copy(alpha = 0.1f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50)) // Pill shape
            .background(backgroundColor.copy(alpha = if (selected) 0.3f else 0.15f))
            .border(1.dp, borderBrush, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelected
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) safeActiveColor else safeTextColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}