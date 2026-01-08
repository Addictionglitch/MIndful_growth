package com.example.mindfulgrowth.ui.aod

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.mindfulgrowth.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LockScreenActivity : AppCompatActivity() {

    // REVERTED: Removed companion object with isActive flag

    // UI Variables
    private lateinit var tvClock: TextView
    private lateinit var tvDate: TextView
    private lateinit var imgTree: ImageView
    private lateinit var tvStatus: TextView
    private lateinit var rootLayout: ConstraintLayout

    // Timer Logic
    private var secondsCounted = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var isTimerRunning = false

    // Gestures
    private lateinit var gestureDetector: GestureDetectorCompat

    // REVERTED: Removed finishReceiver (Power Button Listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("DEBUG_LOCK", "LockScreenActivity Created!")

        // 1. Setup Flags & Hiding IMMEDIATELY
        setupLockScreenFlags()
        hideSystemUI()
        
        setContentView(R.layout.activity_lock_screen)

        applyCustomBrightness()

        // Find Views
        rootLayout = findViewById(R.id.rootLayout)
        tvClock = findViewById(R.id.tvClock)
        tvDate = findViewById(R.id.tvDate)
        imgTree = findViewById(R.id.imgTree)
        tvStatus = findViewById(R.id.tvStatus)

        // Animation Start
        rootLayout.alpha = 0f
        rootLayout.animate().alpha(1f).setDuration(1500).setStartDelay(100).start()

        imgTree.alpha = 0.2f
        
        // REVERTED: Removed registerReceiver for finishReceiver

        setupSingleTapGesture() // KEPT: We still use single tap!
    }

    private fun applyCustomBrightness() {
        // 1. Get the saved value
        val sharedPref = getSharedPreferences("MindfulPrefs", Context.MODE_PRIVATE)
        // Default to -1.0f (System Preferred) if not set, or 0.5f (50%)
        val customBrightness = sharedPref.getFloat("aod_brightness", 0.5f)

        // 2. Apply to THIS window only
        val layoutParams = window.attributes
        layoutParams.screenBrightness = customBrightness
        window.attributes = layoutParams
    }

    override fun onDestroy() {
        super.onDestroy()
        // REVERTED: Removed unregisterReceiver
    }

    override fun onResume() {
        super.onResume()
        startUpdates()
        hideSystemUI()
    }

    override fun onPause() {
        super.onPause()
        stopUpdates()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun startUpdates() {
        if (isTimerRunning) return

        isTimerRunning = true
        runnable = object : Runnable {
            override fun run() {
                val now = Date()
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

                tvClock.text = timeFormat.format(now)
                tvDate.text = dateFormat.format(now)

                secondsCounted++

                val progress = secondsCounted.toFloat() / (60 * 20)
                val cappedProgress = progress.coerceAtMost(1.0f)

                imgTree.alpha = 0.2f + (0.8f * cappedProgress) 
                imgTree.scaleX = 0.5f + (0.5f * cappedProgress) 
                imgTree.scaleY = 0.5f + (0.5f * cappedProgress)

                if (cappedProgress >= 1.0f) {
                    tvStatus.text = "Fully Grown!"
                    tvStatus.setTextColor(ContextCompat.getColor(this@LockScreenActivity, R.color.neon_green_accent))
                } else {
                    tvStatus.text = "Growing... ${(cappedProgress * 100).toInt()}%"
                }

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    private fun stopUpdates() {
        isTimerRunning = false
        handler.removeCallbacks(runnable)
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    private fun setupSingleTapGesture() {
        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                unlockAndGoHome()
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }
        })
    }

    private fun unlockAndGoHome() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
        
        finish() 
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun setupLockScreenFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }
}
