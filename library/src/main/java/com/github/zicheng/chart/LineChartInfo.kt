package com.github.zicheng.chart



data class PointInfo(val x: Float, val y: Float)

data class AxisInfo(val value: Float, val showText: String = value.toString())

internal enum class TextAlign{
    LEFT, CENTER, RIGHT
}