package com.example.mindfulgrowth.ui.screens.customization

import androidx.lifecycle.ViewModel
import com.example.mindfulgrowth.model.ProgressionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomizationViewModel : ViewModel() {
    private val _xpState = MutableStateFlow(ProgressionState(currentXp = 250))
    val xpState = _xpState.asStateFlow()
}