package com.example.mindfulgrowth.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mindfulgrowth.R
import kotlin.math.ceil
import kotlin.math.max

class WaveGraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // --- Paints ---
    private val linePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.graph_line)
        strokeWidth = 5f // Thinner, more elegant line
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_secondary_dark)
        textSize = 28f
        textAlign = Paint.Align.RIGHT
        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.text_secondary_dark)
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val gridPaint = Paint().apply {
        color = Color.parseColor("#1AFFFFFF") // Very subtle grid
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    // --- Data ---
    private var dataPoints: List<Float> = listOf()
    private var xLabels: List<String> = listOf()
    private var maxDataValue = 1f 

    fun setData(usageHours: List<Float>, days: List<String>) {
        if (usageHours.isEmpty()) return
        dataPoints = usageHours
        xLabels = days
        
        // Calculate max value and round up to nearest even number for cleaner grid
        val maxActual = usageHours.maxOrNull() ?: 0f
        maxDataValue = max(ceil(maxActual / 2f) * 2f, 4f) 
        
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        
        // Layout measurements
        val paddingLeft = 70f
        val paddingBottom = 50f
        val graphWidth = w - paddingLeft
        val graphHeight = h - paddingBottom
        
        // --- 1. Draw Y-Axis (Hourly Marks & Grid) ---
        val steps = (maxDataValue / 2).toInt() // Draw line every 2 hours
        for (i in 0..steps) {
            val value = i * 2f
            val yPos = graphHeight - ((value / maxDataValue) * graphHeight)
            
            // Text: "2h", "4h"
            canvas.drawText("${value.toInt()}h", paddingLeft - 12f, yPos + 10f, textPaint)
            
            // Grid Line (skip bottom line)
            if (i > 0) { 
                canvas.drawLine(paddingLeft, yPos, w, yPos, gridPaint)
            }
        }

        // --- 2. Draw X-Axis Labels & Wave ---
        val path = Path()
        val fillPath = Path()
        val stepX = graphWidth / (dataPoints.size - 1)

        fun getY(value: Float) = graphHeight - ((value / maxDataValue) * graphHeight)

        // Start Path
        val startY = getY(dataPoints[0])
        path.moveTo(paddingLeft, startY)
        fillPath.moveTo(paddingLeft, graphHeight)
        fillPath.lineTo(paddingLeft, startY)

        for (i in 0 until dataPoints.size) {
            val xPos = paddingLeft + (i * stepX)
            
            // Draw Label at bottom
            if (i < xLabels.size) {
                canvas.drawText(xLabels[i], xPos, h - 10f, labelPaint)
            }

            // Curve segment
            if (i < dataPoints.size - 1) {
                val thisX = xPos
                val thisY = getY(dataPoints[i])
                val nextX = paddingLeft + ((i + 1) * stepX)
                val nextY = getY(dataPoints[i + 1])

                // Cubic Bezier control points for smooth wave
                val controlX1 = thisX + (stepX * 0.4f)
                val controlY1 = thisY
                val controlX2 = nextX - (stepX * 0.4f)
                val controlY2 = nextY

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, nextX, nextY)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, nextX, nextY)
            }
        }

        // Close Fill Path
        fillPath.lineTo(w, graphHeight)
        fillPath.lineTo(paddingLeft, graphHeight)

        // Set Gradient Shader
        fillPaint.shader = LinearGradient(
            0f, 0f, 0f, graphHeight,
            ContextCompat.getColor(context, R.color.graph_fill_start),
            ContextCompat.getColor(context, R.color.graph_fill_end),
            Shader.TileMode.CLAMP
        )

        // Draw!
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(path, linePaint)
    }
}
