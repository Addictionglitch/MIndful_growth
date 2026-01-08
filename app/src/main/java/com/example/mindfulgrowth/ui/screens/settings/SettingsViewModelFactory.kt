package com.example.mindfulgrowth.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            // Pass the context to the ViewModel constructor
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}