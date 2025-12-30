package com.example.mindfulgrowth.ui.screens.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulgrowth.model.RewardItem
import com.example.mindfulgrowth.ui.screens.customization.components.UnlockableItem

@Composable
fun CustomizationScreen(viewModel: CustomizationViewModel = viewModel()) {
    // You need the same XP state here to determine lock status
    val xpState by viewModel.xpState.collectAsState()

    val rewardItem = RewardItem("Cosmetic Aurora", 1000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ... Grid or Row layout ...
        UnlockableItem(
            item = rewardItem,
            currentXp = xpState.currentXp,
            isSelected = false, // Add logic for selection
            onSelect = { /* Apply cosmetic */ }
        )
    }
}
