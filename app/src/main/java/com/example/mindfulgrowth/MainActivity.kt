package com.example.mindfulgrowth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import com.example.mindfulgrowth.ui.navigation.MindfulGrowthApp
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // --- IMAGE ADJUSTMENT VARIABLES ---
        val gamma = 1f
        val highlights = 1f
        val shadows = 1f

        setContent {
            MindfulGrowthTheme {
                // GLOBAL BACKGROUND CONTAINER
                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. The Background Gradient Image ("The Underlay")
                    // Make sure your image file is named 'app_background' in res/drawable
                    Image(
                        painter = painterResource(id = R.drawable.`app_background`),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.colorMatrix(
                            ColorMatrix().apply {
                                val scale = gamma * highlights
                                setToScale(scale, scale, scale, 1f)
                                this[0, 4] = shadows
                                this[1, 4] = shadows
                                this[2, 4] = shadows
                            }
                        )
                    )

                    // 2. The App UI (Transparent layers on top)
                    MindfulGrowthApp()
                }
            }
        }
    }
}