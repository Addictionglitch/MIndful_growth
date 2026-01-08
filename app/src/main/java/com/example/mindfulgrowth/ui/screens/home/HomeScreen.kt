package com.example.mindfulgrowth.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulgrowth.ui.components.GlassCard
import com.example.mindfulgrowth.ui.theme.surfaceCard
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(onNavigateToSettings: () -> Unit) {
    var timeAway by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timeAway++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Background removed to use MainActivity's global background
            .clickable(onClick = {}, indication = null, interactionSource = remember { MutableInteractionSource() })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LockscreenHeader(onNavigateToSettings)
            TimeAwayCard(timeInSeconds = timeAway)
            TeamProgressRow()
        }
    }
}

@Composable
private fun LockscreenHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Good Day, User",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFF5F5F5) // TextPrimary
            )
        }
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Settings",
                tint = Color(0xFFA7A9A9) // TextSecondary
            )
        }
    }
}

@Composable
private fun TimeAwayCard(timeInSeconds: Long) {
    val hours = timeInSeconds / 3600
    val minutes = (timeInSeconds % 3600) / 60

    val timeString = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f),
        cornerRadius = 20.dp,
        blur = true,
        bloom = true,
        contentPadding = PaddingValues(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Time Away",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF5F5F5) // TextPrimary (use white for high contrast)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = timeString,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5F5F5) // TextPrimary
            )
        }
    }
}

@Composable
private fun TeamProgressRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { _ ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(surfaceCard)
            )
        }
    }
}
