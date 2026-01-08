package com.example.mindfulgrowth.ui.aod

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mindfulgrowth.ui.theme.SystemConfigColors

class AODActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configure Window to sit ON TOP of Lock Screen
        setupWindowFlags()

        // 2. Battery Optimization (Black Screen, Low Brightness)
        minimizeBatteryImpact()

        // 3. The UI Content
        setContent {
            // Root container must be PURE BLACK
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(SystemConfigColors.PRIMARY_BG))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                // Double tap to exit AOD and reveal System Lock Screen
                                finish()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Phase 3 Designs
                Text(text = "12:00", color = Color(SystemConfigColors.TEXT_PRIMARY), fontSize = 64.sp)
                Text(
                    text = "Double tap to wake",
                    color = Color(SystemConfigColors.TEXT_SECONDARY),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    private fun setupWindowFlags() {
        // Show over Keyguard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true) // Wakes the screen up to show our activity
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        // Hide System Bars (Immersive Mode)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun minimizeBatteryImpact() {
        val layoutParams = window.attributes
        layoutParams.screenBrightness = 0.01f

        // Fix: logic to prefer lower refresh rate if available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.let { disp ->
                // Find the mode with the lowest refresh rate (e.g. 1Hz, 10Hz, 60Hz)
                val bestMode = disp.supportedModes.minByOrNull { it.refreshRate }
                bestMode?.let {
                    layoutParams.preferredDisplayModeId = it.modeId
                }
            }
        }
        window.attributes = layoutParams
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}