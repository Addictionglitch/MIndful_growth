package com.example.mindfulgrowth

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.core.view.WindowCompat
import com.example.mindfulgrowth.service.ScreenStateService
import com.example.mindfulgrowth.ui.navigation.MindfulGrowthApp
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import com.example.mindfulgrowth.ui.theme.MindfulTheme

class MainActivity : ComponentActivity() {

    // Permission launcher for Notifications (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // After notification permission decision, check Overlay
        checkAndRequestOverlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MindfulGrowthTheme {
                // UI Content
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
                    MindfulGrowthApp()
                }
            }
        }

        // Start the permission flow once UI is ready
        checkPermissionsAndStartService()
    }

    override fun onResume() {
        super.onResume()
        // Re-check permissions when returning from Settings
        if (hasAllPermissions()) {
            startAodService()
        }
    }

    private fun checkPermissionsAndStartService() {
        // 1. POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
                return // Stop here, wait for callback
            }
        }
        
        // 2. SYSTEM_ALERT_WINDOW (Overlay)
        checkAndRequestOverlay()
    }

    private fun checkAndRequestOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            showPermissionDialog(
                title = "Display over other apps",
                message = "Mindful Growth needs to display over other apps to work as a lock screen. Please enable this permission.",
                action = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            )
        } else {
            // All good! Start service.
            startAodService()
        }
    }

    private fun startAodService() {
        val intent = Intent(this, ScreenStateService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun hasAllPermissions(): Boolean {
        val overlayOk = Settings.canDrawOverlays(this)
        val notifyOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true

        return overlayOk && notifyOk
    }

    private fun showPermissionDialog(title: String, message: String, action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Go to Settings") { _, _ -> action() }
            .setCancelable(false) // User MUST accept to proceed
            .show()
    }
}
