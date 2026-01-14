
package com.example.mindfulgrowth.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun GardenScreen() {
    Box(Modifier.fillMaxSize()) {
        Text("Garden Screen", modifier = Modifier.align(Alignment.Center))
    }
}