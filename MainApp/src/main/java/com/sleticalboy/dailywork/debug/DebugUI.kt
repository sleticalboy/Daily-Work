package com.sleticalboy.dailywork.debug

import com.sleticalboy.dailywork.R
import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.util.DebugUtils
import kotlinx.android.synthetic.main.activity_debug.*

class DebugUI : BaseActivity() {

    override fun layoutResId(): Int = R.layout.activity_debug

    override fun initView() {
        openSettings.setOnClickListener { DebugUtils.openSettings(application) }
        debugLayout.setOnCheckedChangeListener { _, isChecked ->
            DebugUtils.debugLayout(isChecked)
        }
    }
}