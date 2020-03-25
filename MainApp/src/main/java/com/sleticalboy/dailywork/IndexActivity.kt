package com.sleticalboy.dailywork

import com.sleticalboy.dailywork.base.BaseActivity
import com.sleticalboy.dailywork.bean.ModuleItem
import com.sleticalboy.dailywork.debug.DebugUI

class IndexActivity : BaseActivity() {

    private val modules = listOf(
            ModuleItem("调试工具", DebugUI::class.java)
    )

    override fun layoutResId(): Int = R.layout.activity_index

    override fun initView() {
    }
}