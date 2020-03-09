package com.sleticalboy.dailywork.debug

import android.content.ComponentName
import android.content.Intent
import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import kotlinx.android.synthetic.main.activity_debug.*

class DebugUI : BaseActivity() {

    override fun layoutResId(): Int = R.layout.activity_debug

    override fun initView() {
        openSettings.setOnClickListener { openSettings() }
    }

    private fun openSettings() {
        val intent = Intent("android.settings.SETTINGS")
        val pkg = "com.android.settings"
        intent.component = ComponentName(pkg, "$pkg.Settings")
        startActivity(intent)
    }

}