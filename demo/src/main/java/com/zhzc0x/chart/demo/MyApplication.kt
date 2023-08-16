package com.zhzc0x.chart.demo

import android.app.Application
import com.zhzc0x.chart.demo.BuildConfig
import timber.log.Timber

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}