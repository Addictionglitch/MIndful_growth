package com.example.mindfulgrowth.ui.aod

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.mindfulgrowth.MindfulLockScreen
import com.example.mindfulgrowth.data.AppDatabase
import com.example.mindfulgrowth.data.FocusSession
import com.example.mindfulgrowth.ui.theme.MindfulGrowthTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class LockScreenActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var proximitySensor: Sensor? = null

    // Holds the state for "Pocket Mode" (screen off if covered)
    private var isCovered by mutableStateOf(false)

    // Get the database instance
    private val db by lazy {
        androidx.room.Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "mindful-db"
        ).build()
    }

    private val dao by lazy { db.focusDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup Window Flags & Black Background
        setupWindowFlags()

        // CRITICAL: Set native background to black immediately.
        window.decorView.setBackgroundColor(android.graphics.Color.BLACK)

        // 2. Hide System Bars (Full Immersive)
        hideSystemUI()
        // 3. Sensors for Pocket Mode
        setupSensors()

        setContent {
            MindfulGrowthTheme {
                // State for the timer/progress
                var progress by remember { mutableFloatStateOf(0f) }
                var currentTime by remember { mutableStateOf(LocalDateTime.now()) }
                // Ambient Mode State (Always true for AOD efficiency)
                val isAmbient by remember { mutableStateOf(true) }

                // --- OPTIMIZED TIMER LOGIC ---
                LaunchedEffect(Unit) {
                    val durationSeconds = 60 * 20 // 20 Minutes
                    val startTime = System.currentTimeMillis() // Capture exact start time

                    while (isActive && progress < 1f) {
                        // 1. Calculate progress based on REAL time diff (Delta)
                        // This is robust against sleep modes.
                        val now = System.currentTimeMillis()
                        val elapsedSeconds = (now - startTime) / 1000

                        progress = (elapsedSeconds.toFloat() / durationSeconds).coerceIn(0f, 1f)
                        currentTime = LocalDateTime.now()

                        if (progress >= 1f) {
                            onTimerFinished(startTime, durationSeconds.toLong())
                            break
                        }

                        // 2. NATIVE SLEEP: Update only once per minute (60s)
                        // This allows the CPU to deep sleep, saving massive battery.
                        // If you really want 10s, change to 10_000L.
                        delay(60_000L)
                    }
                }

                // --- POCKET MODE LOGIC ---
                if (!isCovered) {
                    // 1. NORMAL AOD: Show content
                    MindfulLockScreen(
                        timerProgress = progress,
                        currentTime = currentTime,
                        isAmbient = isAmbient
                    )
                } else {
                    // 2. POCKET MODE: Render PURE BLACK box
                    // Pixels off, minimal power.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }
            }
        }
    }

    private fun onTimerFinished(startTime: Long, duration: Long) {
        val now = System.currentTimeMillis()
        val session = FocusSession(
            startTime = startTime,
            endTime = now,
            durationSeconds = duration
        )

        lifecycleScope.launch {
            dao.insertSession(session)
        }
    }

    private fun setupWindowFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initial brightness
        updateBrightness(isCovered = false)
    }

    private fun updateBrightness(isCovered: Boolean) {
        val params = window.attributes
        // 0.0f = Lowest possible (Backlight off on some OLEDs)
        // 0.01f = Dim AOD brightness (visible but efficient)
        params.screenBrightness = if (isCovered) 0.0f else 0.01f
        window.attributes = params
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    // --- SENSOR LOGIC (Event Driven - Most Efficient) ---
    private fun setupSensors() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
        // SENSOR_DELAY_NORMAL is approx 200ms latency, very low power.
        sensorManager?.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = proximitySensor?.maximumRange ?: 5f

            // Detect if covered (Close to 0)
            val newIsCovered = distance < maxRange && distance < 1f

            if (isCovered != newIsCovered) {
                isCovered = newIsCovered
                updateBrightness(isCovered)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}