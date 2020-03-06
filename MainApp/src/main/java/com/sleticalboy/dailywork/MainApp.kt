package com.sleticalboy.dailywork

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created on 18-3-5.
 *
 * @author leebin
 * @version 1.0
 */
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        mApp = this
        installLeakCanary();
        adaptAndroidO()
    }

    private fun installLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            mRefWatcher = LeakCanary.install(this)
        }
    }

    private fun adaptAndroidO() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        Log.d("MainApp", "adapt android O")
        registerReceivers()
    }

    private fun registerReceivers() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_SCREEN_ON)
        intentFilter.addAction(Intent.ACTION_USER_PRESENT)
        registerReceiver(GlobalReceiver(), intentFilter)
    }

    companion object {

        private var mApp: Application? = null
        private var mRefWatcher: RefWatcher? = null

        val app: Context? get() = mApp

        val refWatcher: RefWatcher? get() = mRefWatcher
    }
}
