package com.example.mindfulgrowth.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import android.widget.Switch // <--- CHANGED: Using standard Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.service.ScreenStateService
import com.google.android.material.button.MaterialButton

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var btnSingle: AppCompatButton
    private lateinit var btnDouble: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Start AOD Button
        val btnStartAOD = view.findViewById<MaterialButton>(R.id.btnStartAOD)
        btnStartAOD.setOnClickListener {
            startAodService()
        }

        // 2. Setup Switches (Now using standard Switch to match XML)
        val switchAOD = view.findViewById<Switch>(R.id.switchAOD)
        val switchNotifs = view.findViewById<Switch>(R.id.switchNotifs)

        switchAOD.setOnCheckedChangeListener { _, isChecked ->
            // Save preference logic here later
        }

        // 3. Setup Wake Gesture Buttons
        btnSingle = view.findViewById(R.id.btnSingleTap)
        btnDouble = view.findViewById(R.id.btnDoubleTap)

        // Default UI State
        updateGestureUI(isSingleTap = true)

        btnSingle.setOnClickListener {
            updateGestureUI(isSingleTap = true)
            Toast.makeText(context, "Single Tap Enabled", Toast.LENGTH_SHORT).show()
        }

        btnDouble.setOnClickListener {
            updateGestureUI(isSingleTap = false)
            Toast.makeText(context, "Double Tap Enabled", Toast.LENGTH_SHORT).show()
        }

        // 4. Setup Brightness Slider
        val seekBar = view.findViewById<SeekBar>(R.id.seekBarBrightness)
        val tvBrightness = view.findViewById<TextView>(R.id.tvBrightnessLevel)

        // Load saved value (Default 0.5f which is 50%)
        val sharedPref = requireActivity().getSharedPreferences("MindfulPrefs", Context.MODE_PRIVATE)
        val savedBrightness = sharedPref.getFloat("aod_brightness", 0.5f)
        val progress = (savedBrightness * 100).toInt()

        seekBar.progress = progress
        tvBrightness.text = "$progress%"

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBrightness.text = "$progress%"
                // Save immediately as Float (0.0 to 1.0)
                val brightnessValue = progress / 100f
                sharedPref.edit().putFloat("aod_brightness", brightnessValue).apply()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        checkPermission()
    }

    private fun updateGestureUI(isSingleTap: Boolean) {
        val context = requireContext()
        val colorDark = ContextCompat.getColor(context, R.color.bg_dark)
        val colorTextSec = ContextCompat.getColor(context, R.color.text_secondary)

        if (isSingleTap) {
            btnSingle.setBackgroundResource(R.drawable.bg_btn_selected)
            btnSingle.setTextColor(colorDark)

            btnDouble.setBackgroundResource(R.drawable.bg_btn_unselected)
            btnDouble.setTextColor(colorTextSec)
        } else {
            btnSingle.setBackgroundResource(R.drawable.bg_btn_unselected)
            btnSingle.setTextColor(colorTextSec)

            btnDouble.setBackgroundResource(R.drawable.bg_btn_selected)
            btnDouble.setTextColor(colorDark)
        }
    }

    private fun startAodService() {
        try {
            val context = requireContext()
            // CRITICAL: Check Overlay Permission
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "Grant 'Display over other apps' to use AOD", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                startActivity(intent)
                return
            }

            val serviceIntent = Intent(context, ScreenStateService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            Toast.makeText(context, "Focus Mode Activated", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (context != null && !Settings.canDrawOverlays(requireContext())) {
            // Optional prompt
        }
    }
}