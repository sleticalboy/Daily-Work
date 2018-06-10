package com.sleticalboy.dailywork.util

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils
import android.util.Log

/**
 * Created on 18-5-17.
 *
 * @author sleticalboy
 * @description
 */
object OSUtils {

    private val LOG_TAG = "OSUtils ->"

    fun isMainProcess(context: Context): Boolean {
        val processName = getProcessName(context.applicationContext, android.os.Process.myPid())
        Log.e(LOG_TAG, processName)
        return !TextUtils.isEmpty(processName) && !processName!!.contains(":")
    }

    private fun getProcessName(context: Context, pid: Int): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        return runningApps
                .firstOrNull { it.pid == pid }
                ?.processName
    }
}
