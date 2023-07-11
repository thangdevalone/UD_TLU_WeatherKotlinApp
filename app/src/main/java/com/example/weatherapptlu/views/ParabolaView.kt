package com.example.weatherapptlu.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class ParabolaView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val strokeWidth = 2f
    private val parabolaColor = Color.WHITE

    private val paint = Paint().apply {
        color = parabolaColor
        style = Paint.Style.STROKE
        strokeWidth = this@ParabolaView.strokeWidth
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = measuredWidth.toFloat()

        val startX = -width / 2
        val endX = width / 2

        val path = Path()
        path.moveTo(startX, calculateY(startX))
        for (x in startX.toInt() until endX.toInt()) {
            val y = calculateY(x.toFloat())
            path.lineTo(x.toFloat(), y)
        }

        canvas.translate(width / 2, 10F)
        canvas.scale(1f, -1f)
        canvas.drawPath(path, paint)
    }

    private fun calculateY(x: Float): Float {
        return (-0.001f * x * x + 4)
    }
}
