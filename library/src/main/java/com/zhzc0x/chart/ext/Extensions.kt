package com.zhzc0x.chart.ext

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import java.math.BigDecimal
import java.math.RoundingMode

internal val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

internal val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        displayMetrics
    )

internal val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        displayMetrics
    ).toInt()

internal fun Float.scale(scale: Int): Float{
    return BigDecimal(this.toDouble()).setScale(scale, RoundingMode.HALF_UP).toFloat()
}

