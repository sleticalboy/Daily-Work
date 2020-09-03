package com.sleticalboy.learning.debug

import com.sleticalboy.learning.R
import com.sleticalboy.learning.base.BaseActivity
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