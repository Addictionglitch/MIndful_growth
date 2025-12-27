package com.example.mindfulgrowth.ui

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mindfulgrowth.R
import com.example.mindfulgrowth.ui.components.WaveGraphView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsFragment : Fragment(R.layout.fragment_stats) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (hasUsageStatsPermission()) {
            loadStats()
        } else {
            view.findViewById<TextView>(R.id.tvWelcome).setOnClickListener {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
            Toast.makeText(context, "Tap 'Welcome' to enable stats", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            loadStats()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            requireContext().packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadStats() {
        val context = context ?: return
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        
        val weeklyData = ArrayList<Float>()
        val dayLabels = ArrayList<String>()
        val labelFormatter = SimpleDateFormat("EEE", Locale.getDefault()) // "Mon", "Tue"

        val calendar = Calendar.getInstance() // Start with current time

        // Loop backwards from Today (i=0) to 6 days ago (i=6)
        for (i in 0..6) {
            val dayCal = calendar.clone() as Calendar
            dayCal.add(Calendar.DAY_OF_YEAR, -i)

            // Calculate Start of Day (00:00:00.000)
            val startCal = dayCal.clone() as Calendar
            startCal.set(Calendar.HOUR_OF_DAY, 0)
            startCal.set(Calendar.MINUTE, 0)
            startCal.set(Calendar.SECOND, 0)
            startCal.set(Calendar.MILLISECOND, 0)
            val startTime = startCal.timeInMillis

            // Calculate End of Day (23:59:59.999)
            // For today, we can use current time, but for past days, use end of day.
            val endTime = if (i == 0) {
                System.currentTimeMillis()
            } else {
                val endCal = dayCal.clone() as Calendar
                endCal.set(Calendar.HOUR_OF_DAY, 23)
                endCal.set(Calendar.MINUTE, 59)
                endCal.set(Calendar.SECOND, 59)
                endCal.set(Calendar.MILLISECOND, 999)
                endCal.timeInMillis
            }

            // Query usage for this specific time range
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            var dayTotalMillis = 0L
            if (stats != null) {
                for (stat in stats) {
                    // Double-check that the stat falls within our start time boundary
                    if (stat.lastTimeUsed >= startTime) {
                        dayTotalMillis += stat.totalTimeInForeground
                    }
                }
            }

            // Add to front of list to maintain chronological order (Day-6 -> Today)
            weeklyData.add(0, dayTotalMillis / (1000f * 60 * 60))
            
            // Create label: "Today" or "Mon", "Tue", etc.
            val label = if (i == 0) "Today" else labelFormatter.format(dayCal.time)
            dayLabels.add(0, label)
        }

        // Update Graph View
        view?.findViewById<WaveGraphView>(R.id.waveGraphView)?.setData(weeklyData, dayLabels)
    }
}
