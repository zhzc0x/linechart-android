package com.zhzc0x.chart.demo

import android.app.Application
import android.content.res.Resources
import android.util.TypedValue

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}

internal val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )
