package com.example.mindfulgrowth.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.mindfulgrowth.ui.aod.LockScreenActivity
import kotlinx.coroutines.*

class ScreenStateService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var aodJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "ScreenStateServiceChannel"
    }

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    acquireWakeLock()
                    scheduleAodLaunch(context)
                }
                Intent.ACTION_SCREEN_ON -> {
                    aodJob?.cancel()
                    releaseWakeLock()
                    android.util.Log.d("DEBUG_AOD", "Screen ON: Cancelled AOD launch")
                }
            }
        }
    }

    private fun scheduleAodLaunch(context: Context) {
        aodJob?.cancel()
        aodJob = serviceScope.launch {
            delay(500)

            if (isActive) {
                android.util.Log.d("DEBUG_AOD", "Launching AOD...")
                val lockIntent = Intent(context, LockScreenActivity::class.java)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(lockIntent)

                delay(1000)
                releaseWakeLock()
            } else {
                releaseWakeLock()
            }
        }
    }

    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) return
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MindfulGrowth:LaunchLock")
        wakeLock?.acquire(10 * 1000L)
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.priority = 1000
        registerReceiver(screenReceiver, filter)
    }

    private fun startForegroundService() {
        try {
            // Create notification channel for Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Screen State Service",
                    NotificationManager.IMPORTANCE_LOW
                )
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager?.createNotificationChannel(channel)
            }

            // Build notification
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mindful Growth Running")
                .setContentText("Tracking screen state...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()

            // Start foreground service with compatible type
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                // For Android 14+, specify compatible foreground service type
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                // For Android 12-13
                startForeground(NOTIFICATION_ID, notification)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Gracefully handle foreground service startup failure
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        aodJob?.cancel()
        releaseWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}
