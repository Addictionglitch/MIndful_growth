
package com.example.mindfulgrowth.ui

import androidx.compose.runtime.Composable
import com.example.mindfulgrowth.ui.navigation.MindfulGrowthApp

@Composable
fun MainAppScreen() {
    // The new Navigation.kt provides the MindfulGrowthApp composable,
    // which now serves as the entire app's scaffold and navigation structure.
    MindfulGrowthApp()
}
