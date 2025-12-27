package com.example.mindfulgrowth.service

import android.app.Notification
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
import kotlinx.coroutines.*
import com.example.mindfulgrowth.ui.aod.LockScreenActivity

class ScreenStateService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var aodJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null 

    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    // REVERTED: Removed the "isActive" check. 
                    // We just blindly launch the AOD when the screen turns off.
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
            // Fast 500ms delay
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
    
    // ... [Standard Boilerplate] ...
    private fun startForegroundService() {
        val channelId = "MindfulGrowth_Service"
        val channelName = "Background Monitor"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Mindful Growth is Active")
            .setContentText("Monitoring screen time...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, notification)
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
    }
}
