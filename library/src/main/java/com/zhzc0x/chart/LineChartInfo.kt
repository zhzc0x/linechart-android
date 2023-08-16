package com.zhzc0x.chart

import android.graphics.Color
import androidx.annotation.ColorInt

internal enum class TextAlign {
    LEFT, CENTER, RIGHT
}

data class PointInfo(val x: Float, val y: Float)

data class AxisInfo(val value: Float, val showText: String = value.toString())

data class ShowPointInfo(
    val x: Float,
    val y: Float,
    val radius: Float,//px
    @ColorInt val color: Int,
    val strokeWidth: Float,//px
    @ColorInt val strokeColor: Int,
    val text: String,
    val textSize: Float,//px
    @ColorInt val textColor: Int,
    val textPadding: Float//px
)