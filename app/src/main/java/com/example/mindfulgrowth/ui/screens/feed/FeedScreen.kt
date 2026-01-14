
package com.example.mindfulgrowth.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.ui.components.GlassCard

@Composable
fun FeedScreen(viewModel: FeedViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Main content with conditional blur
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(if (!uiState.isUnlocked) Modifier.blur(20.dp) else Modifier),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.posts) { post ->
                FeedPostCard(post = post)
            }
        }

        // Locked Overlay
        if (!uiState.isUnlocked) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                cornerRadius = 24.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SIGNAL ENCRYPTED",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete a focus session to decrypt the Mindful Network.",
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
