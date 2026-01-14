package com.example.mindfulgrowth.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun setDuration(minutes: Int) {
        if (!_timerState.value.isActive) {
            val clamped = minutes.coerceIn(5, 720)
            _timerState.value = _timerState.value.copy(
                selectedDuration = clamped.toLong() * 60,
                timeRemaining = clamped.toLong() * 60,
                duration = clamped.toLong() * 60
            )
        }
    }

    fun toggleTimer() {
        if (_timerState.value.isActive) stopTimer() else startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timerState.value = _timerState.value.copy(isActive = true, mode = TimerMode.FOCUSING)

            // Phase 1: Countdown
            while (_timerState.value.timeRemaining > 0 && _timerState.value.isActive) {
                delay(1000)
                _timerState.value = _timerState.value.copy(timeRemaining = _timerState.value.timeRemaining - 1)
            }

            // Phase 2: Overgrowth (Count Up)
            if (_timerState.value.isActive) {
                _timerState.value = _timerState.value.copy(mode = TimerMode.OVERGROWTH, timeRemaining = 0)
                while (_timerState.value.isActive) {
                    delay(1000)
                    _timerState.value = _timerState.value.copy(timeRemaining = _timerState.value.timeRemaining + 1)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        // Here you would save the session to the database
        _timerState.value = _timerState.value.copy(
            isActive = false,
            mode = TimerMode.IDLE,
            timeRemaining = _timerState.value.selectedDuration
        )
    }
}

enum class TimerMode { IDLE, FOCUSING, OVERGROWTH }

data class TimerState(
    val selectedDuration: Long = 25 * 60,
    val duration: Long = 25 * 60,
    val timeRemaining: Long = 25 * 60,
    val isActive: Boolean = false,
    val mode: TimerMode = TimerMode.IDLE,

    // Stats Data (Mocked for now)
    val streakDays: Int = 3,
    val dailyGoalCurrent: Int = 45, // minutes
    val dailyGoalTarget: Int = 120  // minutes
)