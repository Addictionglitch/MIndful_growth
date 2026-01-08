package com.example.mindfulgrowth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.core.view.WindowCompat
import com.example.mindfulgrowth.ui.navigation.MindfulGrowthApp
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import com.example.mindfulgrowth.ui.theme.MindfulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MindfulGrowthTheme {
                // GLOBAL BACKGROUND CONTAINER
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MindfulTheme.colors.gradientStart,
                                    MindfulTheme.colors.gradientMid,
                                    MindfulTheme.colors.gradientEnd
                                )
                            )
                        )
                ) {
                    // The App UI (Transparent layers on top)
                    MindfulGrowthApp()
                }
            }
        }
    }
}