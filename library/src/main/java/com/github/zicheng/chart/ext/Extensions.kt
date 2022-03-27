package com.github.zicheng.chart.ext

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import java.math.BigDecimal

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
    )

internal fun Float.scale(scale: Int): Float{
    return BigDecimal(this.toDouble())
        .setScale(scale, BigDecimal.ROUND_HALF_UP).toFloat()
}

