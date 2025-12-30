package com.example.mindfulgrowth.ui.screens.stats

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTimeSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
        listOf("Day", "Week", "Month").forEach { text ->
            FilterChip(
                selected = selectedOption == text,
                onClick = { onOptionSelected(text) },
                label = { Text(text) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}