
package com.example.mindfulgrowth.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mindfulgrowth.ui.theme.MindfulPalette

@Composable
fun GlassScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        containerColor = MindfulPalette.Void,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MindfulPalette.Void)
                    .padding(paddingValues)
            ) {
                content(paddingValues)
            }
        }
    )
}
