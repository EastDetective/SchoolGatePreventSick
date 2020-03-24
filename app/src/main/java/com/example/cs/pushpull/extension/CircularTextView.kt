package com.example.cs.pushpull.extension

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView

class CircularTextView : TextView {

    private var strokeWidth: Float = 0.toFloat()
    var strokeColor = 0
    private var solidColorA = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun draw(canvas: Canvas?) {

        val circlePaint = Paint()
        circlePaint.color = solidColorA
        circlePaint.flags = Paint.ANTI_ALIAS_FLAG

        val strokePaint = Paint()
        strokePaint.color = strokeColor
        strokePaint.flags = Paint.ANTI_ALIAS_FLAG

        val h = height
        val w = width

        val diameter = if (h > w) h else w
        val radius = diameter / 2

        height = diameter
        width = diameter

        canvas?.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius.toFloat(), strokePaint)
        canvas?.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius - strokeWidth, circlePaint)

        super.draw(canvas)
    }

    fun setStrokeWidth(dp: Int) {
        val scale = context.resources.displayMetrics.density
        strokeWidth = dp * scale
    }

    fun setStrokeColor(color: String) = { strokeColor = Color.parseColor(color) }

    fun setSolidColor(color: String) {
        solidColorA = Color.parseColor(color)
    }
}