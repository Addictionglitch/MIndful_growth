package com.example.mindfulgrowth.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.ui.aod.LockScreenActivity

class ScreenStateService : Service() {

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                Log.d("MindfulAOD", "Screen Off Detected! Launching AOD...")
                launchLockScreen(context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        
        // Register for Screen OFF events
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart if killed
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun launchLockScreen(context: Context) {
        val lockIntent = Intent(context, LockScreenActivity::class.java)
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        // Critical for AOD:
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        context.startActivity(lockIntent)
    }

    private fun createNotification(): Notification {
        val channelId = "mindful_growth_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Mindful Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mindful Growth Active")
            .setContentText("Monitoring screen state for AOD")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
