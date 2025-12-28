package com.example.mindfulgrowth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.mindfulgrowth.ui.screens.stats.StatsScreen
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MindfulGrowthTheme {
                    StatsScreen()
                }
            }
        }
    }
}