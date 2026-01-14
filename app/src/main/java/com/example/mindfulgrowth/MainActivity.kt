
package com.example.mindfulgrowth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.mindfulgrowth.ui.navigation.AppNavHost
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MindfulGrowthTheme {
                AppNavHost()
            }
        }
    }
}
