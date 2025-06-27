package com.zhzc0x.chart

import androidx.annotation.ColorInt

var debugLineChart = true

enum class AmplitudeMode {
    FIXED, // 固定模式
    MAX_NEGATE, // 自动模式：实时计算最大幅值，最小幅值=最大幅值取反
    MAX_MIN    // 自动模式：实时计算最大、最小幅值
}

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

