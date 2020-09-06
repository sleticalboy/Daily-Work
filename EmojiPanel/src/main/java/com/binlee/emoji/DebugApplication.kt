package com.binlee.emoji

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context


class DebugApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        sApp = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sApp: Application? = null
    }
}