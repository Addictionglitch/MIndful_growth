
package com.example.mindfulgrowth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.mindfulgrowth.ui.MainAppScreen
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import com.example.mindfulgrowth.ui.theme.brushes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindfulGrowthTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.brushes.primaryBrush)
                ) {
                    MainAppScreen()
                }
            }
        }
    }
}
